package com.morgoo.binder;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.IBinder;
import android.os.Parcel;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangyong6 on 2015/3/23.
 */
public class SocketServiceManager {

    private static ExecutorService sServerExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        private int index = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("SocketBinderServer_" + (index++));
            return thread;
        }
    });
    private static ExecutorService sTaskExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        private int index = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("SocketBinderTask_" + (index++));
            return thread;
        }
    });


    private static Map<String, SocketServer> sServerCache = new HashMap<String, SocketServer>();

    public static void addService(String name, SocketBinder binder) throws IOException {
        SocketServer server = sServerCache.get(name);
        if (server != null) {
            //已经有了，正在运行。
        } else {
            sServerExecutor.submit(new SocketServer(name, binder));
        }
    }

    private static Map<String, SocketBinderProxy> sClientCache = new HashMap<String, SocketBinderProxy>();

    public static IBinder getService(String name) throws IOException {
        IBinder iBinder = sClientCache.get(name);
        if (iBinder == null) {
            LocalSocket socket = new LocalSocket();
            LocalSocketAddress address = new LocalSocketAddress(name);
            socket.connect(address);
            SocketBinderProxy socketBinderProxy = new SocketBinderProxy(socket);
            sClientCache.put(name, socketBinderProxy);
            return socketBinderProxy;
        } else if (iBinder != null && !iBinder.isBinderAlive()) {
            sClientCache.remove(name);
            LocalSocket socket = new LocalSocket();
            LocalSocketAddress address = new LocalSocketAddress(name);
            socket.connect(address);
            SocketBinderProxy socketBinderProxy = new SocketBinderProxy(socket);
            sClientCache.put(name, socketBinderProxy);
            return socketBinderProxy;
        } else {
            return iBinder;
        }
    }


    private static Map<String, List<SocketSession>> sRunningSessions = new HashMap<String, List<SocketSession>>();

    private static class SocketServer implements Runnable {
        private final String mName;
        private final SocketBinder mBinder;
        private AtomicBoolean mStop = new AtomicBoolean(false);


        private SocketServer(String name, SocketBinder binder) {
            this.mName = name;
            this.mBinder = binder;
        }

        void stopThis() {
            mStop.set(true);
        }

        @Override
        public void run() {
            LocalServerSocket localServerSocket = null;
            try {
                localServerSocket = new LocalServerSocket(mName);
                synchronized (sServerCache) {
                    sServerCache.put(mName, this);
                }
                while (!mStop.get()) {
                    LocalSocket socket = localServerSocket.accept();
                    SocketSession socketSession = new SocketSession(mName, socket, mBinder);
                    sServerExecutor.submit(socketSession);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (localServerSocket != null) {
                    try {
                        localServerSocket.close();
                    } catch (IOException e) {
                    }
                }
                synchronized (sServerCache) {
                    sServerCache.remove(mName);
                }
            }
        }
    }

    private static class SocketSession implements Runnable {
        private final LocalSocket mSocket;
        private AtomicBoolean mStop = new AtomicBoolean(false);
        private final DataInputStream mInputStream;
        private final DataOutputStream mOutputStream;
        private final SocketBinder mBinder;
        private final String mName;

        public SocketSession(String name, LocalSocket socket, SocketBinder binder) throws IOException {
            mName = name;
            mSocket = socket;
            mBinder = binder;
            mInputStream = new DataInputStream(mSocket.getInputStream());
            mOutputStream = new DataOutputStream(mSocket.getOutputStream());
            mBinder.setLocalSocket(socket);
        }

        private synchronized void sendReply(final Parcel reply) throws IOException {
            //OKAY+code+flag+8位数据长度+数据
            byte[] replyData = reply.marshall();
            mOutputStream.writeChars("OKAY");
            mOutputStream.writeInt(0);
            mOutputStream.writeInt(0);
            mOutputStream.writeLong(replyData.length);
            mOutputStream.write(replyData, 0, replyData.length);
        }

        void stopThis() {
            mStop.set(true);
        }

        @Override
        public void run() {
            try {
                synchronized (sRunningSessions) {
                    List<SocketSession> ls = sRunningSessions.get(mName);
                    if (ls == null) {
                        ls = new ArrayList<SocketSession>();
                        sRunningSessions.put(mName, ls);
                    }
                    ls.add(this);
                }

                //数据协议
                //OKAY+code+flag+8位数据长度+数据
                while (!mStop.get()) {
                    byte[] buf = new byte[4];
                    mInputStream.readFully(buf);
                    if (!"OKAY".equals(new String(buf))) {
                        throw new RuntimeException("Bad protocol");
                    }
                    final int code = mInputStream.readInt();
                    final int flags = mInputStream.readInt();

                    long dataLen = mInputStream.readLong();
                    byte[] dataBuffer = new byte[(int) dataLen];//FIXME 这里强制转换可能有问题。
                    mInputStream.readFully(dataBuffer);


                    final Parcel data = Parcel.obtain();
                    data.unmarshall(dataBuffer, 0, dataBuffer.length);

                    sTaskExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final Parcel reply = Parcel.obtain();
                                mBinder.execTransact(code, data, reply, flags);
                                sendReply(reply);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
                synchronized (sRunningSessions) {
                    List<SocketSession> ls = sRunningSessions.get(mName);
                    if (ls != null) {
                        ls.remove(this);
                    }
                }
            }
        }


        private void close() {
            silentClose(mInputStream);
            silentClose(mOutputStream);
            if (mSocket != null) {
                try {
                    mSocket.shutdownInput();
                    mSocket.shutdownOutput();
                    silentClose(mSocket);
                } catch (IOException e) {
                }
            }
        }


    }

    private static void silentClose(Closeable obj) {
        try {
            if (obj != null) {
                obj.close();
            }
        } catch (IOException e) {
        }
    }
}
