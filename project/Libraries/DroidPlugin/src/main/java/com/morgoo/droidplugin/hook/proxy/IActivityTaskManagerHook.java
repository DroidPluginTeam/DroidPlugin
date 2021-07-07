package com.morgoo.droidplugin.hook.proxy;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IActivityManagerHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.Log;
import com.morgoo.helper.MyProxy;
import com.morgoo.helper.compat.SingletonCompat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C), 2018-2020
 * Author: ziqimo
 * Date: 2020/8/1 2:54 PM
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
public class IActivityTaskManagerHook extends ProxyHook {


    private static final String TAG = IActivityTaskManagerHook.class.getSimpleName();

    public IActivityTaskManagerHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public BaseHookHandle createHookHandle() {
        return new IActivityManagerHookHandle(mHostContext);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return super.invoke(proxy, method, args);
        } catch (SecurityException e) {
            String msg = String.format("msg[%s],args[%s]", e.getMessage(), Arrays.toString(args));
            SecurityException e1 = new SecurityException(msg);
            e1.initCause(e);
            throw e1;
        }
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        if (Build.VERSION.SDK_INT >= 28) {
            //參考
            //https://github.com/findandroidviewbyid/Hook
            // https://blog.csdn.net/u014379448/article/details/106299656/
            // Q
            Object singleton = null;
            try {
                Class<?> clazz = Class.forName("android.app.ActivityTaskManager");
                singleton = FieldUtils.readStaticField(clazz, "IActivityTaskManagerSingleton");
            } catch (Exception e) {
                Log.i(TAG, "ActivityTaskManager", e);
                singleton = FieldUtils.readStaticField(ActivityManager.class, "IActivityManagerSingleton");
            }
            Object obj1 = FieldUtils.readField(singleton, "mInstance");
            //IActivityTaskManager 这个实例
            if (obj1 == null) {
                SingletonCompat.get(singleton);
                obj1 = FieldUtils.readField(singleton, "mInstance");
            }
            setOldObj(obj1);
            Class<?> objClass = mOldObj.getClass();
            List<Class<?>> interfaces = Utils.getAllInterfaces(objClass);
            Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
            Object proxiedActivityManager = MyProxy.newProxyInstance(objClass.getClassLoader(), ifs, this);
            FieldUtils.writeField(singleton, "mInstance", proxiedActivityManager);
            return;
        }
    }
}
