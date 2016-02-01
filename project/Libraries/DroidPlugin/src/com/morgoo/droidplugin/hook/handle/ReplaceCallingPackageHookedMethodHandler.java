package com.morgoo.droidplugin.hook.handle;

import android.content.Context;
import android.os.RemoteException;

import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.droidplugin.pm.PluginManager;

import java.lang.reflect.Method;

class ReplaceCallingPackageHookedMethodHandler extends HookedMethodHandler {

    public ReplaceCallingPackageHookedMethodHandler(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
        if (args != null && args.length > 0) {
            for (int index = 0; index < args.length; index++) {
                if (args[index] != null && (args[index] instanceof String)) {
                    String str = ((String) args[index]);
                    if (isPackagePlugin(str)) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
        }
        return super.beforeInvoke(receiver, method, args);
    }

    private static boolean isPackagePlugin(String packageName) throws RemoteException {
        return PluginManager.getInstance().isPluginPackage(packageName);
    }
}