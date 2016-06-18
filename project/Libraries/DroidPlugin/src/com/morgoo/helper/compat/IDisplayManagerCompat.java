package com.morgoo.helper.compat;

import android.os.IBinder;

import com.morgoo.droidplugin.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * IDisplayManagerCompat
 *
 * @author Liu Yichen
 * @date 16/6/13
 */
public class IDisplayManagerCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.hardware.display.IDisplayManager");
        }
        return sClass;
    }

    public static Object asInterface(IBinder binder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class clazz = Class.forName("android.hardware.display.IDisplayManager$Stub");
        return MethodUtils.invokeStaticMethod(clazz, "asInterface", binder);
    }
}
