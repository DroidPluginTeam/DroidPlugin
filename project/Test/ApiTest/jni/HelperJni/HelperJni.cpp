/*
 * HelperJni.cpp
 *
 *  Created on: 2014年5月28日
 *      Author: zhangyong6
 */

#include <errno.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <linux/limits.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include <sys/prctl.h>

#include "help/nativehelper/JNIHelp.h"
#include "help/log.h"
#include "HelperJni/HelperJni.h"


int nativePing(JNIEnv* env, jobject obj) {
	LOGE(TAG, "nativePing");
	return 1986;
}


static JNINativeMethod gMethods[] = {
NATIVE_METHOD((void*) nativePing,"nativePing", "()I" )};

int tryRegisterNativeMethodsHelperJni(JNIEnv* env, const char* clazzname) {
	if (!clazzname) {
		LOGW(TAG, "Can not fund class skip register native method");
		return JNI_ERR;
	}
	return jniRegisterNativeMethods2(env, clazzname, gMethods, NELEM(gMethods));
}
