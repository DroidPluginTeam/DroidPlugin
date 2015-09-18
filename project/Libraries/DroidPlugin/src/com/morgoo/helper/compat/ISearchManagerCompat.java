package com.morgoo.helper.compat;

import android.os.IBinder;

import com.morgoo.droidplugin.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by wyw on 15-9-18.
 */
public class ISearchManagerCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.app.ISearchManager");
        }
        return sClass;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException
            , NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("android.app.ISearchManager$Stub");
        return MethodUtils.invokeStaticMethod(clazz, "asInterface", binder);
    }
}
