/**
 *
 */

package com.morgoo.nativec;

/**
 * @author zhangyong232@gmail.com
 */
public class NativeCHelper {

    private static boolean sSoLoaded = false;
    private static Throwable sThrowable;

    static {
        tryLoadLibraryByName("Test");
    }

    public static void tryLoadLibraryByName(String name) {
        try {
            System.loadLibrary(name);
            sSoLoaded = true;
        } catch (Throwable e) {
            sThrowable = e;
            sSoLoaded = false;
        }
    }

    public static void tryLoadLibraryByPath(String pathName) {
        try {
            System.load(pathName);
            sSoLoaded = true;
        } catch (Throwable e) {
            sThrowable = e;
            sSoLoaded = false;
        }
    }

    public static boolean isSoLoaded() {
        return sSoLoaded;
    }

    // ************************ Helper Start *******************************//
    private final native static int nativePing();

    public final static int ping() {
        if (sSoLoaded) {
            return nativePing();
        } else {
            if (sThrowable != null) {
                String msg = sThrowable.getMessage();
                UnsatisfiedLinkError error = new UnsatisfiedLinkError(
                        msg != null ? msg : "Can not lazy init zhook");
                error.initCause(sThrowable);
                throw error;
            } else {
                throw new UnsatisfiedLinkError(
                        "We can not load so,please see logcat");
            }
        }
    }
    // ************************ Helper End *******************************//
}
