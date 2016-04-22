package com.morgoo.helper.compat;

import android.annotation.TargetApi;
import android.os.Build;
import com.morgoo.droidplugin.reflect.MethodUtils;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class NativeLibraryHelperCompat {


    private static final Class nativeLibraryHelperClass() throws ClassNotFoundException {
        return Class.forName("com.android.internal.content.NativeLibraryHelper");
    }

    private static final Class handleClass() throws ClassNotFoundException {
        return Class.forName("com.android.internal.content.NativeLibraryHelper$Handle");
    }

    public static final int copyNativeBinaries(File apkFile, File sharedLibraryDir) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return copyNativeBinariesAfterM(apkFile, sharedLibraryDir);
        } else {
            return copyNativeBinariesBeforeM(apkFile, sharedLibraryDir);
        }

    }

    private static int copyNativeBinariesBeforeM(File apkFile, File sharedLibraryDir) {
        try {
            Object[] args = new Object[2];
            args[0] = apkFile;
            args[1] = sharedLibraryDir;

            return (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "copyNativeBinariesIfNeededLI", args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static int copyNativeBinariesAfterM(File apkFile, File sharedLibraryDir) {

        try {
            Object handleInstance = MethodUtils.invokeStaticMethod(handleClass(), "create", apkFile);
            if (handleInstance == null) {
                return -1;
            }
            String abi = null;
            if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                int abiIndex = (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "findSupportedAbi", handleInstance, Build.SUPPORTED_32_BIT_ABIS);
                abi = Build.SUPPORTED_32_BIT_ABIS[abiIndex];
            }

            if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
                int abiIndex = (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "findSupportedAbi", handleInstance, Build.SUPPORTED_64_BIT_ABIS);
                if (abiIndex >= 0) {
                    abi = Build.SUPPORTED_64_BIT_ABIS[abiIndex];
                }
            }

            Object[] args = new Object[3];
            args[0] = handleInstance;
            args[1] = sharedLibraryDir;
            args[2] = abi;
            return (int) MethodUtils.invokeStaticMethod(nativeLibraryHelperClass(), "copyNativeBinaries", args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
