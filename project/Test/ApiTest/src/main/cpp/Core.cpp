#include <jni.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/cdefs.h>
#include <dlfcn.h>

#include "help/nativehelper/JNIHelp.h"
#include "help/log.h"
#include "HelperJni/HelperJni.h"

#define NATIVE_CLASS "com/morgoo/nativec/NativeCHelper"

int registerNativeMethodsAndSetup(JNIEnv* env) {
	jclass nativeClass = env->FindClass(NATIVE_CLASS);
	if (clearJniExpcetion(env, TAG) || !nativeClass) {
		LOGE(TAG, "Can not found %s", NATIVE_CLASS);
		return 0;
	}

	env->UnregisterNatives(nativeClass);

	tryRegisterNativeMethodsHelperJni(env, NATIVE_CLASS);
	return 0;
}

__BEGIN_DECLS
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env;
	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
		LOGE(TAG, "JavaVM::GetEnv() failed");
		abort();
	}
	registerNativeMethodsAndSetup(env);

	LOGE(TAG, "JNI_OnLoad OKAY(pid=%d,uid=%d) jvm_addr[0x%x],JNIEnv_addr[0x%x]",
			getpid(), getuid(), (uint32_t ) vm, (uint32_t ) env);
	return JNI_VERSION_1_6;
}
__END_DECLS
