前言：为什么要了解系统Activity，Service,,BroadCastReceiver,ContentProvider的启动流程，这是一个对于即将理解插件中的四大组件动态注册，占坑的前提，如果不了解的话，那么很难了解插件hook哪些东西，又是如何骗过AMS来启动Activity,Service,BroadCastReceiver，ContentProvider？

本节主要记录系统Activity的启动流程：
先看下时序图：
![这里写图片描述](http://img.blog.csdn.net/20160812104759495)

![这里写图片描述](http://img.blog.csdn.net/20160812104813573)

//接上面两张图：
![这里写图片描述](http://img.blog.csdn.net/20160812104827618)
和插件相关主要在第三张图，
先看H类：这是ActivityThread中的内部类，此类继承自Handler，重写handleMeaage方法, 将msg转换成一个ActivityClientRecord对象，调用ActivityThread类成员函数getPackageInfoNoCheck来获得一个LoadApk对象，并且保存在ActivityClientRecord对象r的成员变量packageInfo中。
ActivityThread有一个该类型的成员变量mH。scheduleLaunchActivity()里会发送一个LAUNCH_ACTIVITY类型的消息，该消息被mH处理并调用handleLaunchActivity()。

handleLaunchActivity(r,main)来启动ActivityClientRecord对象t所描述的一个Activity组件。即MyActivity

```
    private class H extends Handler {
        public static final int LAUNCH_ACTIVITY         = 100;
        public static final int PAUSE_ACTIVITY          = 101;
        public static final int PAUSE_ACTIVITY_FINISHING= 102;
        public static final int STOP_ACTIVITY_SHOW      = 103;
        public static final int STOP_ACTIVITY_HIDE      = 104;
        public static final int SHOW_WINDOW             = 105;
        public static final int HIDE_WINDOW             = 106;
        public static final int RESUME_ACTIVITY         = 107;
        public static final int SEND_RESULT             = 108;
        public static final int DESTROY_ACTIVITY        = 109;
        public static final int BIND_APPLICATION        = 110;
        public static final int EXIT_APPLICATION        = 111;
        public static final int NEW_INTENT              = 112;
        public static final int RECEIVER                = 113;
        public static final int CREATE_SERVICE          = 114;
        public static final int SERVICE_ARGS            = 115;
        public static final int STOP_SERVICE            = 116;
        public static final int REQUEST_THUMBNAIL       = 117;
        public static final int CONFIGURATION_CHANGED   = 118;
        public static final int CLEAN_UP_CONTEXT        = 119;
        public static final int GC_WHEN_IDLE            = 120;
        public static final int BIND_SERVICE            = 121;
        public static final int UNBIND_SERVICE          = 122;
        public static final int DUMP_SERVICE            = 123;
        public static final int LOW_MEMORY              = 124;
        public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
        public static final int RELAUNCH_ACTIVITY       = 126;
        public static final int PROFILER_CONTROL        = 127;
        public static final int CREATE_BACKUP_AGENT     = 128;
        public static final int DESTROY_BACKUP_AGENT    = 129;
        public static final int SUICIDE                 = 130;
        public static final int REMOVE_PROVIDER         = 131;
        public static final int ENABLE_JIT              = 132;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;
        public static final int SCHEDULE_CRASH          = 134;
        public static final int DUMP_HEAP               = 135;
        public static final int DUMP_ACTIVITY           = 136;
        public static final int SLEEPING                = 137;
        public static final int SET_CORE_SETTINGS       = 138;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
        public static final int TRIM_MEMORY             = 140;
        public static final int DUMP_PROVIDER           = 141;
        public static final int UNSTABLE_PROVIDER_DIED  = 142;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        public static final int INSTALL_PROVIDER        = 145;
        String codeToString(int code) {
            if (DEBUG_MESSAGES) {
                switch (code) {
                    case LAUNCH_ACTIVITY: return "LAUNCH_ACTIVITY";
                    case PAUSE_ACTIVITY: return "PAUSE_ACTIVITY";
                    case PAUSE_ACTIVITY_FINISHING: return "PAUSE_ACTIVITY_FINISHING";
                    case STOP_ACTIVITY_SHOW: return "STOP_ACTIVITY_SHOW";
                    case STOP_ACTIVITY_HIDE: return "STOP_ACTIVITY_HIDE";
                    case SHOW_WINDOW: return "SHOW_WINDOW";
                    case HIDE_WINDOW: return "HIDE_WINDOW";
                    case RESUME_ACTIVITY: return "RESUME_ACTIVITY";
                    case SEND_RESULT: return "SEND_RESULT";
                    case DESTROY_ACTIVITY: return "DESTROY_ACTIVITY";
                    case BIND_APPLICATION: return "BIND_APPLICATION";
                    case EXIT_APPLICATION: return "EXIT_APPLICATION";
                    case NEW_INTENT: return "NEW_INTENT";
                    case RECEIVER: return "RECEIVER";
                    case CREATE_SERVICE: return "CREATE_SERVICE";
                    case SERVICE_ARGS: return "SERVICE_ARGS";
                    case STOP_SERVICE: return "STOP_SERVICE";
                    case REQUEST_THUMBNAIL: return "REQUEST_THUMBNAIL";
                    case CONFIGURATION_CHANGED: return "CONFIGURATION_CHANGED";
                    case CLEAN_UP_CONTEXT: return "CLEAN_UP_CONTEXT";
                    case GC_WHEN_IDLE: return "GC_WHEN_IDLE";
                    case BIND_SERVICE: return "BIND_SERVICE";
                    case UNBIND_SERVICE: return "UNBIND_SERVICE";
                    case DUMP_SERVICE: return "DUMP_SERVICE";
                    case LOW_MEMORY: return "LOW_MEMORY";
                    case ACTIVITY_CONFIGURATION_CHANGED: return "ACTIVITY_CONFIGURATION_CHANGED";
                    case RELAUNCH_ACTIVITY: return "RELAUNCH_ACTIVITY";
                    case PROFILER_CONTROL: return "PROFILER_CONTROL";
                    case CREATE_BACKUP_AGENT: return "CREATE_BACKUP_AGENT";
                    case DESTROY_BACKUP_AGENT: return "DESTROY_BACKUP_AGENT";
                    case SUICIDE: return "SUICIDE";
                    case REMOVE_PROVIDER: return "REMOVE_PROVIDER";
                    case ENABLE_JIT: return "ENABLE_JIT";
                    case DISPATCH_PACKAGE_BROADCAST: return "DISPATCH_PACKAGE_BROADCAST";
                    case SCHEDULE_CRASH: return "SCHEDULE_CRASH";
                    case DUMP_HEAP: return "DUMP_HEAP";
                    case DUMP_ACTIVITY: return "DUMP_ACTIVITY";
                    case SLEEPING: return "SLEEPING";
                    case SET_CORE_SETTINGS: return "SET_CORE_SETTINGS";
                    case UPDATE_PACKAGE_COMPATIBILITY_INFO: return "UPDATE_PACKAGE_COMPATIBILITY_INFO";
                    case TRIM_MEMORY: return "TRIM_MEMORY";
                    case DUMP_PROVIDER: return "DUMP_PROVIDER";
                    case UNSTABLE_PROVIDER_DIED: return "UNSTABLE_PROVIDER_DIED";
                    case REQUEST_ASSIST_CONTEXT_EXTRAS: return "REQUEST_ASSIST_CONTEXT_EXTRAS";
                    case TRANSLUCENT_CONVERSION_COMPLETE: return "TRANSLUCENT_CONVERSION_COMPLETE";
                    case INSTALL_PROVIDER: return "INSTALL_PROVIDER";
                }
            }
            return Integer.toString(code);
        }
        public void handleMessage(Message msg) {
            if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
            switch (msg.what) {
                case LAUNCH_ACTIVITY: {
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
                    ActivityClientRecord r = (ActivityClientRecord)msg.obj;

                    r.packageInfo = getPackageInfoNoCheck(
                            r.activityInfo.applicationInfo, r.compatInfo);
                    handleLaunchActivity(r, null);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                } break;
                case RELAUNCH_ACTIVITY: {
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityRestart");
                    ActivityClientRecord r = (ActivityClientRecord)msg.obj;
                    handleRelaunchActivity(r);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                } break;
                case PAUSE_ACTIVITY:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPause");
                    handlePauseActivity((IBinder)msg.obj, false, msg.arg1 != 0, msg.arg2);
                    maybeSnapshot();
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case PAUSE_ACTIVITY_FINISHING:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityPause");
                    handlePauseActivity((IBinder)msg.obj, true, msg.arg1 != 0, msg.arg2);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case STOP_ACTIVITY_SHOW:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStop");
                    handleStopActivity((IBinder)msg.obj, true, msg.arg2);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case STOP_ACTIVITY_HIDE:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStop");
                    handleStopActivity((IBinder)msg.obj, false, msg.arg2);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SHOW_WINDOW:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityShowWindow");
                    handleWindowVisibility((IBinder)msg.obj, true);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case HIDE_WINDOW:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityHideWindow");
                    handleWindowVisibility((IBinder)msg.obj, false);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case RESUME_ACTIVITY:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityResume");
                    handleResumeActivity((IBinder)msg.obj, true,
                            msg.arg1 != 0, true);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SEND_RESULT:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityDeliverResult");
                    handleSendResult((ResultData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case DESTROY_ACTIVITY:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityDestroy");
                    handleDestroyActivity((IBinder)msg.obj, msg.arg1 != 0,
                            msg.arg2, false);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case BIND_APPLICATION:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "bindApplication");
                    AppBindData data = (AppBindData)msg.obj;
                    handleBindApplication(data);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case EXIT_APPLICATION:
                    if (mInitialApplication != null) {
                        mInitialApplication.onTerminate();
                    }
                    Looper.myLooper().quit();
                    break;
                case NEW_INTENT:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityNewIntent");
                    handleNewIntent((NewIntentData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case RECEIVER:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "broadcastReceiveComp");
                    handleReceiver((ReceiverData)msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case CREATE_SERVICE:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceCreate");
                    handleCreateService((CreateServiceData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case BIND_SERVICE:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceBind");
                    handleBindService((BindServiceData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case UNBIND_SERVICE:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceUnbind");
                    handleUnbindService((BindServiceData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SERVICE_ARGS:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceStart");
                    handleServiceArgs((ServiceArgsData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case STOP_SERVICE:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "serviceStop");
                    handleStopService((IBinder)msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case REQUEST_THUMBNAIL:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "requestThumbnail");
                    handleRequestThumbnail((IBinder)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case CONFIGURATION_CHANGED:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "configChanged");
                    mCurDefaultDisplayDpi = ((Configuration)msg.obj).densityDpi;
                    handleConfigurationChanged((Configuration)msg.obj, null);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case CLEAN_UP_CONTEXT:
                    ContextCleanupInfo cci = (ContextCleanupInfo)msg.obj;
                    cci.context.performFinalCleanup(cci.who, cci.what);
                    break;
                case GC_WHEN_IDLE:
                    scheduleGcIdler();
                    break;
                case DUMP_SERVICE:
                    handleDumpService((DumpComponentInfo)msg.obj);
                    break;
                case LOW_MEMORY:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "lowMemory");
                    handleLowMemory();
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case ACTIVITY_CONFIGURATION_CHANGED:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityConfigChanged");
                    handleActivityConfigurationChanged((IBinder)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case PROFILER_CONTROL:
                    handleProfilerControl(msg.arg1 != 0, (ProfilerControlData)msg.obj, msg.arg2);
                    break;
                case CREATE_BACKUP_AGENT:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "backupCreateAgent");
                    handleCreateBackupAgent((CreateBackupAgentData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case DESTROY_BACKUP_AGENT:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "backupDestroyAgent");
                    handleDestroyBackupAgent((CreateBackupAgentData)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SUICIDE:
                    Process.killProcess(Process.myPid());
                    break;
                case REMOVE_PROVIDER:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "providerRemove");
                    completeRemoveProvider((ProviderRefCount)msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case ENABLE_JIT:
                    ensureJitEnabled();
                    break;
                case DISPATCH_PACKAGE_BROADCAST:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "broadcastPackage");
                    handleDispatchPackageBroadcast(msg.arg1, (String[])msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SCHEDULE_CRASH:
                    throw new RemoteServiceException((String)msg.obj);
                case DUMP_HEAP:
                    handleDumpHeap(msg.arg1 != 0, (DumpHeapData)msg.obj);
                    break;
                case DUMP_ACTIVITY:
                    handleDumpActivity((DumpComponentInfo)msg.obj);
                    break;
                case DUMP_PROVIDER:
                    handleDumpProvider((DumpComponentInfo)msg.obj);
                    break;
                case SLEEPING:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "sleeping");
                    handleSleeping((IBinder)msg.obj, msg.arg1 != 0);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case SET_CORE_SETTINGS:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "setCoreSettings");
                    handleSetCoreSettings((Bundle) msg.obj);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case UPDATE_PACKAGE_COMPATIBILITY_INFO:
                    handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData)msg.obj);
                    break;
                case TRIM_MEMORY:
                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "trimMemory");
                    handleTrimMemory(msg.arg1);
                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
                    break;
                case UNSTABLE_PROVIDER_DIED:
                    handleUnstableProviderDied((IBinder)msg.obj, false);
                    break;
                case REQUEST_ASSIST_CONTEXT_EXTRAS:
                    handleRequestAssistContextExtras((RequestAssistContextExtras)msg.obj);
                    break;
                case TRANSLUCENT_CONVERSION_COMPLETE:
                    handleTranslucentConversionComplete((IBinder)msg.obj, msg.arg1 == 1);
                    break;
                case INSTALL_PROVIDER:
                    handleInstallProvider((ProviderInfo) msg.obj);
                    break;
            }
            if (DEBUG_MESSAGES) Slog.v(TAG, "<<< done: " + codeToString(msg.what));
        }
```
再看handlePeformLanchActivity方法, 发送一个已中止状态的进程间通信请求给ActivityThread.
![这里写图片描述](http://img.blog.csdn.net/20160812111503608)

以MyActivity继承为例（假定是我们app的第一个Activity），MyActivity被启动，共要经历5个大的步骤：
MyActivity组件是由Launcher组件来启动的，而Launcher组件又是Activity管理服务ActivityManagerService来启动的。

MyActivity组件、Launcher组件和ActivityManagerService组件分别运行在不同的进程中，MyActivity启动过程就涉及了
三个进程，这三个进程通过Binder进程间通信来完成。

Launcher组件启动MyActivity的过程如下：
 
 - **Lanuncher组件向ActivityManagerService发送一个启动MyActivity组件的进程间通信请求**
 - **ActivityManagerService首先将要启动的MyActivity组件的信息保存下来，然后再向Launcher组件发送一个中止状态的进程间通信请求**

 - **Launcher组件进入到中止状态后，就会向ActivityManagerService发送一个已中止状态的进程间通信请求，以便ActivityManagerService可以继续执行MyActivity组件的操作**

 - **ActivityManagerService发现用来运行MyActivity组件的应用程序不存在，因此，它就会启动一个新的应用程序进程**

 - **新的应用程序进程启动完成后，就会向ActivityManagerService发送一个启动完成的进程间通信请求，以便ActivityManagerService可以继续执行启动MyActivity组件的操作。**

 - **ActivityManagerService将第2步保存下来的MyActivity组件的信息发送给第4步创建的应用程序进程，以便它可以将yActivity组件启动起来。**

