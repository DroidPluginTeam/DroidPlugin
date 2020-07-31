Droid Plugin
======

[中文文档](readme_cn.md "中文文档")

[Fllow me at github](https://github.com/cmzy)

DroidPlugin is a new **Plugin Framework** developed and maintained by Andy Zhang( [Fllow me at github](https://github.com/cmzy) ).
It enables the host app run any third-party apk without installation, modification and repackage, which benefit a lot for collaborative development on Android.

-------

# 2020-7-30

解决9.0启动activity

# 2020-7-31

基本适配好9.0

## 遗留问题 

1.native没测试

2.bindservice有异常信息待处理
```
 E/Service1: >>服务Service1:onCreate
2020-07-31 21:36:12.644 16124-16124/com.example.TestPlugin:PluginP06 E/Service1: >>服务Service1:onBind,intent=Intent { cmp=com.example.ApiTest/.Service1 }
2020-07-31 21:36:12.652 16124-16146/com.example.TestPlugin:PluginP06 E/JavaBinder: *** Uncaught remote exception!  (Exceptions are not yet supported across processes.)
    java.lang.AbstractMethodError: abstract method "void android.app.IServiceConnection.connected(android.content.ComponentName, android.os.IBinder, boolean)"
        at android.app.IServiceConnection$Stub.onTransact(IServiceConnection.java:61)
        at android.os.Binder.execTransact(Binder.java:731)
2020-07-31 21:36:12.652 16124-16124/com.example.TestPlugin:PluginP06 I/HookedMethodHandler: doHookInner method(android.app.IActivityManager.publishService) cost 6 ms
2020-07-31 21:36:12.652 16124-16146/com.example.TestPlugin:PluginP06 E/AndroidRuntime: FATAL EXCEPTION: Binder:16124_3
    Process: com.example.TestPlugin:PluginP06, PID: 16124
    java.lang.AbstractMethodError: abstract method "void android.app.IServiceConnection.connected(android.content.ComponentName, android.os.IBinder, boolean)"
        at android.app.IServiceConnection$Stub.onTransact(IServiceConnection.java:61)
        at android.os.Binder.execTransact(Binder.java:731)
2020-07-31 21:36:12.653 16124-16146/com.example.TestPlugin:PluginP06 E/MyCrashHandler: uncaughtExceptionjava.lang.AbstractMethodError: abstract method "void android.app.IServiceConnection.connected(android.content.ComponentName, android.os.IBinder, boolean)"
        at android.app.IServiceConnection$Stub.onTransact(IServiceConnection.java:61)
        at android.os.Binder.execTransact(Binder.java:731)
2020-07-31 21:36:12.660 16124-16146/com.example.TestPlugin:PluginP06 E/MyCrashHandler: 记录uncaughtExceptionjava.io.FileNotFoundException: /storage/emulated/0/PluginLog/CrashLog/CrashLog_20200731213612_16124.log (Permission denied)
        at java.io.FileOutputStream.open0(Native Method)
        at java.io.FileOutputStream.open(FileOutputStream.java:308)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:238)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:180)
        at java.io.PrintWriter.<init>(PrintWriter.java:263)
        at com.morgoo.droidplugin.MyCrashHandler.uncaughtException(MyCrashHandler.java:94)
        at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:1068)
        at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:1063)
        at java.lang.Thread.dispatchUncaughtException(Thread.java:1955)
```

3.有时候会提示Toolbar出问题，后面再看
```
Process: com.example.TestPlugin:PluginP06, PID: 15779
    android.os.BadParcelableException: ClassNotFoundException when unmarshalling: android.support.v7.widget.Toolbar$SavedState
        at android.os.Parcel.readParcelableCreator(Parcel.java:2839)
        at android.os.Parcel.readParcelable(Parcel.java:2765)
        at android.os.Parcel.readValue(Parcel.java:2668)
        at android.os.Parcel.readSparseArrayInternal(Parcel.java:3118)
        at android.os.Parcel.readSparseArray(Parcel.java:2351)
        at android.os.Parcel.readValue(Parcel.java:2725)
        at android.os.Parcel.readArrayMapInternal(Parcel.java:3037)
        at android.os.BaseBundle.initializeFromParcelLocked(BaseBundle.java:288)
        at android.os.BaseBundle.unparcel(BaseBundle.java:232)
        at android.os.Bundle.getSparseParcelableArray(Bundle.java:1010)
        at com.android.internal.policy.PhoneWindow.restoreHierarchyState(PhoneWindow.java:2133)
        at android.app.Activity.onRestoreInstanceState(Activity.java:1135)
        at android.app.Activity.performRestoreInstanceState(Activity.java:1090)
        at android.app.Instrumentation.callActivityOnRestoreInstanceState(Instrumentation.java:1317)
        at android.app.ActivityThread.handleStartActivity(ActivityThread.java:2991)
        at android.app.servertransaction.TransactionExecutor.performLifecycleSequence(TransactionExecutor.java:180)
        at android.app.servertransaction.TransactionExecutor.cycleToPath(TransactionExecutor.java:165)
        at android.app.servertransaction.TransactionExecutor.executeLifecycleState(TransactionExecutor.java:142)
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:70)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1816)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loop(Looper.java:193)
        at android.app.ActivityThread.main(ActivityThread.java:6718)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
2020-07-31 21:31:44.199 15779-15779/? E/MyCrashHandler: uncaughtExceptionandroid.os.BadParcelableException: ClassNotFoundException when unmarshalling: android.support.v7.widget.Toolbar$SavedState
        at android.os.Parcel.readParcelableCreator(Parcel.java:2839)
        at android.os.Parcel.readParcelable(Parcel.java:2765)
        at android.os.Parcel.readValue(Parcel.java:2668)
        at android.os.Parcel.readSparseArrayInternal(Parcel.java:3118)
        at android.os.Parcel.readSparseArray(Parcel.java:2351)
        at android.os.Parcel.readValue(Parcel.java:2725)
        at android.os.Parcel.readArrayMapInternal(Parcel.java:3037)
        at android.os.BaseBundle.initializeFromParcelLocked(BaseBundle.java:288)
        at android.os.BaseBundle.unparcel(BaseBundle.java:232)
        at android.os.Bundle.getSparseParcelableArray(Bundle.java:1010)
        at com.android.internal.policy.PhoneWindow.restoreHierarchyState(PhoneWindow.java:2133)
        at android.app.Activity.onRestoreInstanceState(Activity.java:1135)
        at android.app.Activity.performRestoreInstanceState(Activity.java:1090)
        at android.app.Instrumentation.callActivityOnRestoreInstanceState(Instrumentation.java:1317)
        at android.app.ActivityThread.handleStartActivity(ActivityThread.java:2991)
        at android.app.servertransaction.TransactionExecutor.performLifecycleSequence(TransactionExecutor.java:180)
        at android.app.servertransaction.TransactionExecutor.cycleToPath(TransactionExecutor.java:165)
        at android.app.servertransaction.TransactionExecutor.executeLifecycleState(TransactionExecutor.java:142)
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:70)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1816)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loop(Looper.java:193)
        at android.app.ActivityThread.main(ActivityThread.java:6718)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
2020-07-31 21:31:44.203 15779-15779/? E/MyCrashHandler: 记录uncaughtExceptionjava.io.FileNotFoundException: /storage/emulated/0/PluginLog/CrashLog/CrashLog_20200731213144_15779.log (Permission denied)
        at java.io.FileOutputStream.open0(Native Method)
        at java.io.FileOutputStream.open(FileOutputStream.java:308)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:238)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:180)
        at java.io.PrintWriter.<init>(PrintWriter.java:263)
        at com.morgoo.droidplugin.MyCrashHandler.uncaughtException(MyCrashHandler.java:94)
        at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:1068)
        at java.lang.ThreadGroup.uncaughtException(ThreadGroup.java:1063)
        at java.lang.Thread.dispatchUncaughtException(Thread.java:1955)
```

