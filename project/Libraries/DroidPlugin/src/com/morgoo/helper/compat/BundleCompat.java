package com.morgoo.helper.compat;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Method;

/**
 * @author Lody
 */
public class BundleCompat {
    static Method getIBinder, putIBinder;

    static {
        if (Build.VERSION.SDK_INT < 18) {
            try {
                getIBinder = Bundle.class.getDeclaredMethod("getIBinder", String.class);
            } catch (NoSuchMethodException e) {
                //ignore
            }
            try {
                putIBinder = Bundle.class.getDeclaredMethod("getIBinder", String.class, IBinder.class);
            } catch (NoSuchMethodException e) {
                //ignore
            }
        }
    }

    public static IBinder getBinder(Bundle bundle, String key) {
        if (Build.VERSION.SDK_INT >= 18) {
            return bundle.getBinder(key);
        } else {
            if(getIBinder != null) {
                try {
                    return (IBinder) getIBinder.invoke(bundle, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static void putBinder(Bundle bundle, String key, IBinder value) {
        if (Build.VERSION.SDK_INT >= 18) {
            bundle.putBinder(key, value);
        } else {
            if(putIBinder != null) {
                try {
                    putIBinder.invoke(bundle, key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
