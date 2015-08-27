/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.morgoo.binder;

import android.net.LocalSocket;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;


public class SocketBinder implements IBinder {

    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final boolean CHECK_PARCEL_SIZE = false;
    static final String TAG = "Binder";

    /**
     * Control whether dump() calls are allowed.
     */
    private static String sDumpDisabled = null;

    private IInterface mOwner;
    private String mDescriptor;

    private LocalSocket mLocalSocket;

    public void setLocalSocket(LocalSocket localSocket) {
        this.mLocalSocket = localSocket;
    }

    public final int getCallingPid() throws IOException {
        return mLocalSocket.getPeerCredentials().getPid();
    }


    public final int getCallingUid() throws IOException {
        return mLocalSocket.getPeerCredentials().getUid();
    }

    public static final UserHandle getCallingUserHandle() {
        //TODO
        throw new UnsupportedOperationException("getCallingUserHandle");
    }


    public static final long clearCallingIdentity() {
        //TODO
        throw new UnsupportedOperationException("clearCallingIdentity");
    }

    public static final void restoreCallingIdentity(long token) {
        //TODO
        throw new UnsupportedOperationException("restoreCallingIdentity");
    }

    public static final void setThreadStrictModePolicy(int policyMask) {
        //TODO
        throw new UnsupportedOperationException();
    }


    public static final int getThreadStrictModePolicy() {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static final void flushPendingCommands() {
        //TODO
        throw new UnsupportedOperationException();
    }


    public static final void joinThreadPool() {
        //TODO
        throw new UnsupportedOperationException();

    }

    public static final boolean isProxy(IInterface iface) {
        return iface.asBinder() != iface;
    }

    /**
     * Default constructor initializes the object.
     */
    public SocketBinder() {
        init();

        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends SocketBinder> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following Binder class should be static or leaks might occur: " +
                        klass.getCanonicalName());
            }
        }
    }

    public void attachInterface(IInterface owner, String descriptor) {
        mOwner = owner;
        mDescriptor = descriptor;
    }


    public String getInterfaceDescriptor() {
        return mDescriptor;
    }


    public boolean pingBinder() {
        return true;
    }


    public boolean isBinderAlive() {
        return true;
    }


    public IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor.equals(descriptor)) {
            return mOwner;
        }
        return null;
    }

    public static void setDumpDisabled(String msg) {
        synchronized (Binder.class) {
            sDumpDisabled = msg;
        }
    }


    protected boolean onTransact(int code, Parcel data, Parcel reply,
                                 int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(getInterfaceDescriptor());
            return true;
        } else if (code == DUMP_TRANSACTION) {
            ParcelFileDescriptor fd = data.readFileDescriptor();
            String[] args = data.createStringArray();
            if (fd != null) {
                try {
                    dump(fd.getFileDescriptor(), args);
                } finally {
                    try {
                        fd.close();
                    } catch (IOException e) {
                        // swallowed, not propagated back to the caller
                    }
                }
            }
            // Write the StrictMode header.
            if (reply != null) {
                reply.writeNoException();
            } else {
//                StrictMode.clearGatheredViolations();
            }
            return true;
        }
        return false;
    }

    /**
     * Implemented to call the more convenient version
     * {@link #dump(FileDescriptor, PrintWriter, String[])}.
     */
    public void dump(FileDescriptor fd, String[] args) {
        FileOutputStream fout = new FileOutputStream(fd);
        PrintWriter pw = new PrintWriter(fout, true);
        try {
            final String disabled;
            synchronized (Binder.class) {
                disabled = sDumpDisabled;
            }
            if (disabled == null) {
                try {
                    dump(fd, pw, args);
                } catch (SecurityException e) {
                    pw.println("Security exception: " + e.getMessage());
                    throw e;
                } catch (Throwable e) {
                    // Unlike usual calls, in this case if an exception gets thrown
                    // back to us we want to print it back in to the dump data, since
                    // that is where the caller expects all interesting information to
                    // go.
                    pw.println();
                    pw.println("Exception occurred while dumping:");
                    e.printStackTrace(pw);
                }
            } else {
                pw.println(sDumpDisabled);
            }
        } finally {
            pw.flush();
        }
    }

    /**
     * Like {@link #dump(FileDescriptor, String[])}, but ensures the target
     * executes asynchronously.
     */
    public void dumpAsync(final FileDescriptor fd, final String[] args) {
        final FileOutputStream fout = new FileOutputStream(fd);
        final PrintWriter pw = new PrintWriter(fout, true);
        Thread thr = new Thread("Binder.dumpAsync") {
            public void run() {
                try {
                    dump(fd, pw, args);
                } finally {
                    pw.flush();
                }
            }
        };
        thr.start();
    }

    /**
     * Print the object's state into the given stream.
     *
     * @param fd   The raw file descriptor that the dump is being sent to.
     * @param fout The file to which you should dump your state.  This will be
     *             closed for you after you return.
     * @param args additional arguments to the dump request.
     */
    protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
    }

    /**
     * Default implementation rewinds the parcels and calls onTransact.  On
     * the remote side, transact calls into the binder to do the IPC.
     */
    public final boolean transact(int code, Parcel data, Parcel reply,
                                  int flags) throws RemoteException {
        if (false) Log.v("Binder", "Transact: " + code + " to " + this);
        if (data != null) {
            data.setDataPosition(0);
        }
        boolean r = onTransact(code, data, reply, flags);
        if (reply != null) {
            reply.setDataPosition(0);
        }
        return r;
    }

    /**
     * Local implementation is a no-op.
     */
    public void linkToDeath(DeathRecipient recipient, int flags) {
    }

    /**
     * Local implementation is a no-op.
     */
    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        return true;
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    static void checkParcel(IBinder obj, int code, Parcel parcel, String msg) {
        if (CHECK_PARCEL_SIZE && parcel.dataSize() >= 800 * 1024) {
            // Trying to send > 800k, this is way too much
            StringBuilder sb = new StringBuilder();
            sb.append(msg);
            sb.append(": on ");
            sb.append(obj);
            sb.append(" calling ");
            sb.append(code);
            sb.append(" size ");
            sb.append(parcel.dataSize());
            sb.append(" (data: ");
            parcel.setDataPosition(0);
            sb.append(parcel.readInt());
            sb.append(", ");
            sb.append(parcel.readInt());
            sb.append(", ");
            sb.append(parcel.readInt());
            sb.append(")");
            Log.wtf(TAG, sb.toString());
        }
    }

    private final void init() {

    }

    private final void destroy() {

    }

    // Entry point from android_util_Binder.cpp's onTransact
    boolean execTransact(int code, Parcel data, Parcel reply, int flags) {

        // theoretically, we should call transact, which will call onTransact,
        // but all that does is rewind it, and we just got these from an IPC,
        // so we'll just call it directly.
        boolean res;
        // Log any exceptions as warnings, don't silently suppress them.
        // If the call was FLAG_ONEWAY then these exceptions disappear into the ether.
        try {
            res = onTransact(code, data, reply, flags);
        } catch (RemoteException e) {
            if ((flags & FLAG_ONEWAY) != 0) {
                Log.w(TAG, "Binder call failed.", e);
            } else {
                reply.setDataPosition(0);
                reply.writeException(e);
            }
            res = true;
        } catch (RuntimeException e) {
            if ((flags & FLAG_ONEWAY) != 0) {
                Log.w(TAG, "Caught a RuntimeException from the binder stub implementation.", e);
            } else {
                reply.setDataPosition(0);
                reply.writeException(e);
            }
            res = true;
        } catch (OutOfMemoryError e) {
            // Unconditionally log this, since this is generally unrecoverable.
            Log.e(TAG, "Caught an OutOfMemoryError from the binder stub implementation.", e);
            RuntimeException re = new RuntimeException("Out of memory", e);
            reply.setDataPosition(0);
            reply.writeException(re);
            res = true;
        }
        checkParcel(this, code, reply, "Unreasonably large binder reply buffer");
        reply.recycle();
        data.recycle();

        // Just in case -- we are done with the IPC, so there should be no more strict
        // mode violations that have gathered for this thread.  Either they have been
        // parceled and are now in transport off to the caller, or we are returning back
        // to the main transaction loop to wait for another incoming transaction.  Either
        // way, strict mode begone!
//        StrictMode.clearGatheredViolations();

        return res;
    }
}

final class SocketBinderProxy implements IBinder {

    private final LocalSocket mLocalSocket;
    private final DataInputStream mInputStream;
    private final DataOutputStream mOutputStream;

    SocketBinderProxy(LocalSocket localSocket) throws IOException {
        mSelf = new WeakReference(this);
        mLocalSocket = localSocket;
        mInputStream = new DataInputStream(mLocalSocket.getInputStream());
        mOutputStream = new DataOutputStream(mLocalSocket.getOutputStream());
    }

    protected void finalize() throws Throwable {
        try {
            mLocalSocket.close();
            mInputStream.close();
            mOutputStream.close();
        } catch (Exception e) {

        } finally {
            super.finalize();
        }
    }

    public boolean pingBinder() {
        return mLocalSocket.isConnected();
    }

    public boolean isBinderAlive() {
        return mLocalSocket.isConnected();
    }

    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        SocketBinder.checkParcel(this, code, data, "Unreasonably large binder buffer");
        try {
            return transactNative(code, data, reply, flags);
        } catch (IOException e) {
            RemoteException remoteException = new RemoteException();
            remoteException.initCause(e);
            throw remoteException;
        }
    }

    public String getInterfaceDescriptor() throws RemoteException {
        throw new UnsupportedOperationException();
    }

    public boolean transactNative(int code, Parcel data, Parcel reply,
                                  int flags) throws RemoteException, IOException {
        mOutputStream.writeChars("OKAY");
        mOutputStream.writeInt(code);
        mOutputStream.writeInt(flags);
        byte[] dataBuffer = data.marshall();
        mOutputStream.writeLong((long) dataBuffer.length);
        mOutputStream.write(dataBuffer);
        mOutputStream.writeInt(flags);

        byte[] buffer = new byte[4];
        mInputStream.readFully(buffer);
        if (!"OKAY".equals(new String(buffer))) {
            throw new RuntimeException("Bad protocol");
        }
        final int newCode = mInputStream.readInt();
        final int newFlags = mInputStream.readInt();
        long dataLen = mInputStream.readLong();
        byte[] replyBuffer = new byte[(int) dataLen];//FIXME 这里强制转换可能有问题。
        mInputStream.readFully(replyBuffer);
        reply.unmarshall(replyBuffer, 0, replyBuffer.length);
        reply.setDataPosition(0);

        throw new UnsupportedOperationException();
    }

    public void linkToDeath(DeathRecipient recipient, int flags)
            throws RemoteException {
        throw new UnsupportedOperationException();
    }

    public boolean unlinkToDeath(DeathRecipient recipient, int flags) {
        throw new UnsupportedOperationException();
    }

    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        try {
            transact(DUMP_TRANSACTION, data, reply, 0);
            reply.readException();
        } finally {
            data.recycle();
            reply.recycle();
        }
    }

    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        data.writeFileDescriptor(fd);
        data.writeStringArray(args);
        try {
            transact(DUMP_TRANSACTION, data, reply, FLAG_ONEWAY);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }


    private static final void sendDeathNotice(DeathRecipient recipient) {
        if (false) Log.v("JavaBinder", "sendDeathNotice to " + recipient);
        try {
            recipient.binderDied();
        } catch (RuntimeException exc) {
            Log.w("BinderNative", "Uncaught exception from death notification",
                    exc);
        }
    }

    final private WeakReference mSelf;
}
