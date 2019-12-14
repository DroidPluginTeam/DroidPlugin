package com.morgoo.helper.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.lang.reflect.Method;

/**
 * Created by ifunbow-k on 2017/8/2.
 */

public class ProcessUtils {
    private static IProcessChecker sIProcessChecker;

    static {
        sIProcessChecker = new IProcessChecker() {
            @Override
            public boolean isPluginProcess(String processName) {
                return processName != null && processName.contains(":Plugin");
            }

            @Override
            public boolean isManagerProcess(String processName) {
                return processName != null && processName.contains(":CoreManager");
            }
        };
    }

    public static void setProcessChecker(IProcessChecker sIProcessChecker) {
        ProcessUtils.sIProcessChecker = sIProcessChecker;
    }

    public static IProcessChecker getProcessChecker() {
        return sIProcessChecker;
    }

    public interface IProcessChecker {
        boolean isPluginProcess(String processName);

        boolean isManagerProcess(String processName);
    }

    public static boolean isPluginProcess(Context context) {
        String processName = getProcessName(context);
        return getProcessChecker().isPluginProcess(processName);
    }

    public static boolean isManagerProcess(Context context) {
        String processName = getProcessName(context);
        return getProcessChecker().isManagerProcess(processName);
    }

    public static boolean isMainProcess(Context context) {
        String processName = getProcessName(context);
        return !getProcessChecker().isManagerProcess(processName)
                && !getProcessChecker().isPluginProcess(processName);
    }

    private static String getProcessName(Context context) {
        String processName = null;
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = ActivityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object am = currentActivityThread.invoke(null);
            Method getProcessName = ActivityThread.getDeclaredMethod("getProcessName");
            getProcessName.setAccessible(true);
            processName = (String) getProcessName.invoke(am);
        } catch (Exception e) {
            int pid = android.os.Process.myPid();
            ActivityManager manager = (ActivityManager) context.getSystemService(Context
                    .ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses
                    ()) {
                if (process.pid == pid) {
                    processName = process.processName;
                    break;
                }
            }
        }
        return processName;
    }
}
