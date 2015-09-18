package com.morgoo.droidplugin.hook.binder;

import android.content.Context;
import android.os.IBinder;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IClipboardHookHandle;
import com.morgoo.droidplugin.hook.handle.ISearchManagerHookHandle;
import com.morgoo.helper.compat.ISearchManagerCompat;

/**
 * Created by wyw on 15-9-18.
 */
public class ISearchManagerBinderHook extends BinderHook {

    private final static String SEARCH_MANAGER_SERVICE = "search";

    public ISearchManagerBinderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    Object getOldObj() throws Exception {
        IBinder iBinder = MyServiceManager.getOriginService(SEARCH_MANAGER_SERVICE);
        return ISearchManagerCompat.asInterface(iBinder);
    }

    @Override
    public String getServiceName() {
        return SEARCH_MANAGER_SERVICE;
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new ISearchManagerHookHandle(mHostContext);
    }
}
