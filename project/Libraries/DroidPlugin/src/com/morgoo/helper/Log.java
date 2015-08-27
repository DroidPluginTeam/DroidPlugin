/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.helper;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.morgoo.droidplugin.hook.HookFactory;
import com.morgoo.droidplugin.hook.proxy.LibCoreHook;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangyong on 14/10/18.
 */
public class Log {

    private static final String TAG = "Log";

    private static boolean sDebug = false;
    private static boolean sFileLog = false;
    private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat sFormat1 = new SimpleDateFormat("yyyyMMdd");

    private Log() {
    }

    private static final File sDir = new File(Environment.getExternalStorageDirectory(), "360Log/Plugin/");

    static {
        sFileLog = sDir.exists() && sDir.isDirectory();
        sDebug = sFileLog;
    }

    public static boolean isDebug() {
        return sDebug;
    }

    private static boolean isFileLog() {
        return sFileLog;
    }

    public static boolean isLoggable(int i) {
        return isDebug();
    }

    public static boolean isLoggable() {
        return isDebug();
    }

    private static String levelToStr(int level) {
        switch (level) {
            case android.util.Log.VERBOSE:
                return "V";
            case android.util.Log.DEBUG:
                return "D";
            case android.util.Log.INFO:
                return "I";
            case android.util.Log.WARN:
                return "W";
            case android.util.Log.ERROR:
                return "E";
            case android.util.Log.ASSERT:
                return "A";
            default:
                return "UNKNOWN";
        }
    }

    private static File getLogFile() {
        File file = new File(Environment.getExternalStorageDirectory(), String.format("360Log/Plugin/Log_%s_%s.log", sFormat1.format(new Date()), android.os.Process.myPid()));
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return file;
    }

    private static HandlerThread sHandlerThread;
    private static Handler sHandler;

    static {
        sHandlerThread = new HandlerThread("FileLogThread");
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }

    private static void logToFile(final int level, final String tag, final String format, final Object[] args, final Throwable tr) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                logToFileInner(level, tag, format, args, tr);
            }
        });
    }

    private static void logToFileInner(int level, String tag, String format, Object[] args, Throwable tr) {
        PrintWriter writer = null;
        try {
            if (!isFileLog()) {
                return;
            }

            //禁用LibCoreHook，防止方法循环调用。
            HookFactory.getInstance().setHookEnable(LibCoreHook.class, false);



            writer = new PrintWriter(new FileWriter(getLogFile(), true));
            String msg = String.format(format, args);
            String log = String.format("%s %s-%s/%s %s/%s %s", sFormat.format(new Date()), Process.myPid(), Process.myUid(), getProcessName(), levelToStr(level), tag, msg);
            writer.println(log);
            if (tr != null) {
                tr.printStackTrace(writer);
                writer.println();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Throwable e) {
                }
            }
            HookFactory.getInstance().setHookEnable(LibCoreHook.class, true);
        }
    }

    private static String getProcessName() {
        return "?";
    }


    private static void logToFileWtf(String tag, String format, Object[] args, Throwable tr) {
        logToFile(-1, tag, format, args, tr);
    }


    public static void v(String tag, String format, Object... args) {
        v(tag, format, null, args);
    }

    public static void v(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable(android.util.Log.VERBOSE)) {
            return;
        }
        logToFile(android.util.Log.VERBOSE, tag, format, args, tr);
        if (tr == null) {
            android.util.Log.v(tag, String.format(format, args));
        } else {
            android.util.Log.v(tag, String.format(format, args), tr);
        }
    }


    public static void d(String tag, String format, Object... args) {
        d(tag, format, null, args);
    }

    public static void d(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable(android.util.Log.DEBUG)) {
            return;
        }
        logToFile(android.util.Log.DEBUG, tag, format, args, tr);
        if (tr == null) {
            android.util.Log.d(tag, String.format(format, args));
        } else {
            android.util.Log.d(tag, String.format(format, args), tr);
        }
    }

    public static void i(String tag, String format, Object... args) {
        i(tag, format, null, args);
    }

    public static void i(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable(android.util.Log.INFO)) {
            return;
        }
        logToFile(android.util.Log.INFO, tag, format, args, tr);
        if (tr == null) {
            android.util.Log.i(tag, String.format(format, args));
        } else {
            android.util.Log.i(tag, String.format(format, args), tr);
        }
    }

    public static void w(String tag, String format, Object... args) {
        w(tag, format, null, args);
    }

    public static void w(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable(android.util.Log.WARN)) {
            return;
        }
        logToFile(android.util.Log.WARN, tag, format, args, tr);
        if (tr == null) {
            android.util.Log.w(tag, String.format(format, args));
        } else {
            android.util.Log.w(tag, String.format(format, args), tr);
        }
    }

    public static void w(String tag, Throwable tr) {
        w(tag, "Log.warn", tr);
    }

    public static void e(String tag, String format, Object... args) {
        e(tag, format, null, args);
    }

    public static void e(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable(android.util.Log.ERROR)) {
            return;
        }
        logToFile(android.util.Log.ERROR, tag, format, args, tr);
        if (tr == null) {
            android.util.Log.e(tag, String.format(format, args));
        } else {
            android.util.Log.e(tag, String.format(format, args), tr);
        }
    }

    public static void wtf(String tag, String format, Object... args) {
        wtf(tag, format, null, args);
    }

    public static void wtf(String tag, Throwable tr) {
        wtf(tag, "wtf", tr);
    }

    public static void wtf(String tag, String format, Throwable tr, Object... args) {
        if (!isLoggable()) {
            return;
        }
        logToFileWtf(tag, format, args, tr);
        if (tr == null) {
            android.util.Log.wtf(tag, String.format(format, args));
        } else {
            android.util.Log.wtf(tag, String.format(format, args), tr);
        }
    }


}
