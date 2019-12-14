package com.morgoo.droidplugin.hook.handle;

import android.content.Context;
import android.text.TextUtils;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;

import java.lang.reflect.Method;

/**
 * IDisplayManagerHookHandle
 *
 * @author Liu Yichen
 * @date 16/6/13
 */
public class IDisplayManagerHookHandle extends BaseHookHandle {


    public IDisplayManagerHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {

        sHookedMethodHandlers.put("createVirtualDisplay", new createVirtualDisplay(mHostContext));
    }

    private static class createVirtualDisplay extends HookedMethodHandler {


        public createVirtualDisplay(Context hostContext) {
            super(hostContext);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {

            final int pkgIndex = 2;
            if (args != null && args.length > 0 && args[pkgIndex] instanceof String) {
                String pkg = (String) args[pkgIndex];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[pkgIndex] = mHostContext.getPackageName();
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

}
