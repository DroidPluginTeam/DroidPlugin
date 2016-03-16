/*
 * log.h
 *
 *  Created on: 2013年12月4日
 *      Author: zhangyong232@gmail.com
 */

#ifndef __LOG_H__
#define __LOG_H__

#include <android/log.h>
#include <string.h>
#include <sys/cdefs.h>
#include <stdio.h>

__BEGIN_DECLS

#define LOG_VERBOSE 1
#define LOG_DEBUG 2
#define LOG_INFO 3
#define LOG_WARNING 4
#define LOG_ERROR 5
#define LOG_FATAL 6
#define LOG_SILENT 7

#ifndef MY_LOG_LEVEL
#define MY_LOG_LEVEL LOG_SILENT
#endif

///是否打开日志开关
#ifndef DEBUG
#define DEBUG MY_LOG_LEVEL
#endif

#ifndef CURRENT_FILENAME
#define CURRENT_FILENAME (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)
#endif

//默认的日志tag "NdkNative[Test.cpp(main):25]"
#ifndef TAG
#define TAG \
		CURRENT_FILENAME,__FUNCTION__,__LINE__
#endif

inline void android_log_print(int prio, const char *file, const char* func,
		int line, const char *fmt, ...) {
	va_list args;
	va_start(args, fmt);

	char buf[1024];
	snprintf(buf, sizeof(buf), "Ndk[%s(%s()):%d]", file, func, line);
	__android_log_vprint(prio, buf, fmt, args);
	va_end(args);
}

#if(DEBUG <= LOG_VERBOSE)
#define LOGV(tag,...) android_log_print(ANDROID_LOG_VERBOSE,tag,##__VA_ARGS__)
#define LOGVA(...) LOGV(TAG,##__VA_ARGS__)
#else
#define LOGV(tag,...)
#define LOGVA(...)
#endif

#if(DEBUG <= LOG_DEBUG)
#define LOGD(tag,...) android_log_print(ANDROID_LOG_DEBUG,tag,##__VA_ARGS__)
#define LOGDA(...) LOGD(TAG,##__VA_ARGS__)
#else
#define LOGD(tag,...)
#define LOGDA(...)
#endif

#if(DEBUG <= LOG_INFO)
#define LOGI(tag,...) android_log_print(ANDROID_LOG_INFO,tag,##__VA_ARGS__)
#define LOGIA(...) LOGI(TAG,##__VA_ARGS__)
#else
#define LOGI(tag,...)
#define LOGIA(...)
#endif

#if(DEBUG <= LOG_WARN)
#define LOGW(tag,...) android_log_print(ANDROID_LOG_WARN,tag,##__VA_ARGS__)
#define LOGWA(...) LOGW(TAG,##__VA_ARGS__)
#else
#define LOGW(tag,...)
#define LOGWA(...)
#endif

#if(DEBUG  <= LOG_ERROR)
#define LOGE(tag,...) android_log_print(ANDROID_LOG_ERROR,tag,##__VA_ARGS__)
#define LOGEA(...) LOGE(TAG, ##__VA_ARGS__)
#else
#define LOGE(tag,...)
#define LOGEA(...)
#endif

#if(DEBUG <= LOG_FATAL)
#define LOGF(tag,...) android_log_print(ANDROID_LOG_FATAL,tag,##__VA_ARGS__)
#define LOGFA(...) LOGF(TAG,##__VA_ARGS__)
#else
#define LOGF(tag,...)
#define LOGFA(...)
#endif

__END_DECLS

#endif /* LOG_H_ */

