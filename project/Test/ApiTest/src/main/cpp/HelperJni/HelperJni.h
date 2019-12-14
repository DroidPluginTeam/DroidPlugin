/*
 * HelperJni.h
 *
 *  Created on: 2014年5月28日
 *      Author: zhangyong6
 */

#ifndef HELPERJNI_H_
#define HELPERJNI_H_

#include <jni.h>

int tryRegisterNativeMethodsHelperJni(JNIEnv* env, const char* clazz);

inline bool clearJniExpcetion(JNIEnv* env, const char *tag, const char* func,
                              int line) {
    if (env->ExceptionCheck()) {
        jthrowable exception = env->ExceptionOccurred();
        if (exception) {
            jniLogException(env, ANDROID_LOG_ERROR, tag, exception);
        }
        env->ExceptionClear();
        return true;
    }
    return false;
}

#endif /* HELPERJNI_H_ */
