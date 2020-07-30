/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook.handle;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.core.PluginProcessManager;
import com.morgoo.droidplugin.hook.proxy.IPackageManagerHook;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.stub.ShortcutProxyActivity;
import com.morgoo.helper.Log;


public class PluginCallback implements Handler.Callback {

    private static final String TAG = "PluginCallback";

    public static final int LAUNCH_ACTIVITY = 100;
    public static final int PAUSE_ACTIVITY = 101;
    public static final int PAUSE_ACTIVITY_FINISHING = 102;
    public static final int STOP_ACTIVITY_SHOW = 103;
    public static final int STOP_ACTIVITY_HIDE = 104;
    public static final int SHOW_WINDOW = 105;
    public static final int HIDE_WINDOW = 106;
    public static final int RESUME_ACTIVITY = 107;
    public static final int SEND_RESULT = 108;
    public static final int DESTROY_ACTIVITY = 109;
    public static final int BIND_APPLICATION = 110;
    public static final int EXIT_APPLICATION = 111;
    public static final int NEW_INTENT = 112;
    public static final int RECEIVER = 113;
    public static final int CREATE_SERVICE = 114;
    public static final int SERVICE_ARGS = 115;
    public static final int STOP_SERVICE = 116;
    public static final int REQUEST_THUMBNAIL = 117;
    public static final int CONFIGURATION_CHANGED = 118;
    public static final int CLEAN_UP_CONTEXT = 119;
    public static final int GC_WHEN_IDLE = 120;
    public static final int BIND_SERVICE = 121;
    public static final int UNBIND_SERVICE = 122;
    public static final int DUMP_SERVICE = 123;
    public static final int LOW_MEMORY = 124;
    public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
    public static final int RELAUNCH_ACTIVITY = 126;
    public static final int PROFILER_CONTROL = 127;
    public static final int CREATE_BACKUP_AGENT = 128;
    public static final int DESTROY_BACKUP_AGENT = 129;
    public static final int SUICIDE = 130;
    public static final int REMOVE_PROVIDER = 131;
    public static final int ENABLE_JIT = 132;
    public static final int DISPATCH_PACKAGE_BROADCAST = 133;
    public static final int SCHEDULE_CRASH = 134;
    public static final int DUMP_HEAP = 135;
    public static final int DUMP_ACTIVITY = 136;
    public static final int SLEEPING = 137;
    public static final int SET_CORE_SETTINGS = 138;
    public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
    public static final int TRIM_MEMORY = 140;
    public static final int DUMP_PROVIDER = 141;
    public static final int UNSTABLE_PROVIDER_DIED = 142;
    public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
    public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
    public static final int INSTALL_PROVIDER = 145;
    public static final int ON_NEW_ACTIVITY_OPTIONS = 146;
    public static final int CANCEL_VISIBLE_BEHIND = 147;
    public static final int BACKGROUND_VISIBLE_BEHIND_CHANGED = 148;
    public static final int ENTER_ANIMATION_COMPLETE = 149;

    String codeToString(int code) {
        switch (code) {
            case LAUNCH_ACTIVITY:
                return "LAUNCH_ACTIVITY";
            case PAUSE_ACTIVITY:
                return "PAUSE_ACTIVITY";
            case PAUSE_ACTIVITY_FINISHING:
                return "PAUSE_ACTIVITY_FINISHING";
            case STOP_ACTIVITY_SHOW:
                return "STOP_ACTIVITY_SHOW";
            case STOP_ACTIVITY_HIDE:
                return "STOP_ACTIVITY_HIDE";
            case SHOW_WINDOW:
                return "SHOW_WINDOW";
            case HIDE_WINDOW:
                return "HIDE_WINDOW";
            case RESUME_ACTIVITY:
                return "RESUME_ACTIVITY";
            case SEND_RESULT:
                return "SEND_RESULT";
            case DESTROY_ACTIVITY:
                return "DESTROY_ACTIVITY";
            case BIND_APPLICATION:
                return "BIND_APPLICATION";
            case EXIT_APPLICATION:
                return "EXIT_APPLICATION";
            case NEW_INTENT:
                return "NEW_INTENT";
            case RECEIVER:
                return "RECEIVER";
            case CREATE_SERVICE:
                return "CREATE_SERVICE";
            case SERVICE_ARGS:
                return "SERVICE_ARGS";
            case STOP_SERVICE:
                return "STOP_SERVICE";
            case CONFIGURATION_CHANGED:
                return "CONFIGURATION_CHANGED";
            case CLEAN_UP_CONTEXT:
                return "CLEAN_UP_CONTEXT";
            case GC_WHEN_IDLE:
                return "GC_WHEN_IDLE";
            case BIND_SERVICE:
                return "BIND_SERVICE";
            case UNBIND_SERVICE:
                return "UNBIND_SERVICE";
            case DUMP_SERVICE:
                return "DUMP_SERVICE";
            case LOW_MEMORY:
                return "LOW_MEMORY";
            case ACTIVITY_CONFIGURATION_CHANGED:
                return "ACTIVITY_CONFIGURATION_CHANGED";
            case RELAUNCH_ACTIVITY:
                return "RELAUNCH_ACTIVITY";
            case PROFILER_CONTROL:
                return "PROFILER_CONTROL";
            case CREATE_BACKUP_AGENT:
                return "CREATE_BACKUP_AGENT";
            case DESTROY_BACKUP_AGENT:
                return "DESTROY_BACKUP_AGENT";
            case SUICIDE:
                return "SUICIDE";
            case REMOVE_PROVIDER:
                return "REMOVE_PROVIDER";
            case ENABLE_JIT:
                return "ENABLE_JIT";
            case DISPATCH_PACKAGE_BROADCAST:
                return "DISPATCH_PACKAGE_BROADCAST";
            case SCHEDULE_CRASH:
                return "SCHEDULE_CRASH";
            case DUMP_HEAP:
                return "DUMP_HEAP";
            case DUMP_ACTIVITY:
                return "DUMP_ACTIVITY";
            case SLEEPING:
                return "SLEEPING";
            case SET_CORE_SETTINGS:
                return "SET_CORE_SETTINGS";
            case UPDATE_PACKAGE_COMPATIBILITY_INFO:
                return "UPDATE_PACKAGE_COMPATIBILITY_INFO";
            case TRIM_MEMORY:
                return "TRIM_MEMORY";
            case DUMP_PROVIDER:
                return "DUMP_PROVIDER";
            case UNSTABLE_PROVIDER_DIED:
                return "UNSTABLE_PROVIDER_DIED";
            case REQUEST_ASSIST_CONTEXT_EXTRAS:
                return "REQUEST_ASSIST_CONTEXT_EXTRAS";
            case TRANSLUCENT_CONVERSION_COMPLETE:
                return "TRANSLUCENT_CONVERSION_COMPLETE";
            case INSTALL_PROVIDER:
                return "INSTALL_PROVIDER";
            case ON_NEW_ACTIVITY_OPTIONS:
                return "ON_NEW_ACTIVITY_OPTIONS";
            case CANCEL_VISIBLE_BEHIND:
                return "CANCEL_VISIBLE_BEHIND";
            case BACKGROUND_VISIBLE_BEHIND_CHANGED:
                return "BACKGROUND_VISIBLE_BEHIND_CHANGED";
            case ENTER_ANIMATION_COMPLETE:
                return "ENTER_ANIMATION_COMPLETE";
        }
        return Integer.toString(code);
    }


    private Handler mOldHandle = null;
    private Handler.Callback mCallback = null;
    private Context mHostContext;

    private boolean mEnable = false;

    public PluginCallback(Context hostContext, Handler oldHandle, Handler.Callback callback) {
        mOldHandle = oldHandle;
        mCallback = callback;
        mHostContext = hostContext;
    }

    public void setEnable(boolean enable) {
        this.mEnable = enable;
    }

    public boolean isEnable() {
        return mEnable;
    }

    @Override
    public boolean handleMessage(Message msg) {
        long b = System.currentTimeMillis();
        try {
            if (!mEnable) {
                return false;
            }

            if (PluginProcessManager.isPluginProcess(mHostContext)) {
                if (!PluginManager.getInstance().isConnected()) {
                    //这里必须要这么做。如果当前进程是插件进程，并且，还没有绑定上插件管理服务，我们则把消息延迟一段时间再处理。
                    //这样虽然会降低启动速度，但是可以解决在没绑定服务就启动，会导致的一系列时序问题。
                    Log.i(TAG, "handleMessage not isConnected post and wait,msg=%s", msg);
                    mOldHandle.sendMessageDelayed(Message.obtain(msg), 5);
                    //返回true，告诉下面的handle不要处理了。
                    return true;
                }
            }

            if (msg.what == LAUNCH_ACTIVITY) {
                return handleLaunchActivity(msg);
            } /*else if (msg.what == INSTALL_PROVIDER) {
                return handleInstallProvider(msg);
            } else if (msg.what == CREATE_BACKUP_AGENT) {
                //TODO 处理CREATE_BACKUP_AGENT
            } else if (msg.what == DESTROY_BACKUP_AGENT) {
                //TODO 处理DESTROY_BACKUP_AGENT
            } else if (msg.what == CREATE_SERVICE) {
    //            return handleCreateService(msg);
            } else if (msg.what == BIND_SERVICE) {
    //            return handleBindService(msg);
            } else if (msg.what == UNBIND_SERVICE) {
    //            return handleUnbindService(msg);
            } else if (msg.what == SERVICE_ARGS) {
    //            return handleServiceArgs(msg);
            }*/
            if (mCallback != null) {
                return mCallback.handleMessage(msg);
            } else {
                return false;
            }
        } finally {
            Log.i(TAG, "handleMessage(%s,%s) cost %s ms", msg.what, codeToString(msg.what), (System.currentTimeMillis() - b));

        }
    }

//    private boolean handleServiceArgs(Message msg) {
//        // handleServiceArgs((ServiceArgsData)msg.obj);
//        try {
//            Object obj = msg.obj;
//            Intent intent = (Intent) FieldUtils.readField(obj, "args", true);
//            if (intent != null) {
//                intent.setExtrasClassLoader(getClass().getClassLoader());
//                Intent originPluginIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
//                if (originPluginIntent != null) {
//                    FieldUtils.writeDeclaredField(msg.obj, "args", originPluginIntent, true);
//                    Log.i(TAG, "handleServiceArgs OK");
//                } else {
//                    Log.w(TAG, "handleServiceArgs pluginInfo==null");
//                }
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "handleServiceArgs", e);
//        }
//        return false;
//    }
//
//    private boolean handleUnbindService(Message msg) {
//        //  handleUnbindService((BindServiceData)msg.obj);
//        try {
//            Object obj = msg.obj;
//            Intent intent = (Intent) FieldUtils.readField(obj, "intent", true);
//            intent.setExtrasClassLoader(getClass().getClassLoader());
//            Intent originPluginIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
//            if (originPluginIntent != null) {
//                FieldUtils.writeDeclaredField(msg.obj, "intent", originPluginIntent, true);
//                Log.i(TAG, "handleUnbindService OK");
//            } else {
//                Log.w(TAG, "handleUnbindService pluginInfo==null");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "handleUnbindService", e);
//        }
//        return false;
//    }
//
//    private boolean handleBindService(Message msg) {
//        // handleBindService((BindServiceData)msg.obj);
//        //其实这里什么都不用做的。
//        try {
//            Object obj = msg.obj;
//            Intent intent = (Intent) FieldUtils.readField(obj, "intent", true);
//            intent.setExtrasClassLoader(getClass().getClassLoader());
//            Intent originPluginIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
//            if (originPluginIntent != null) {
//
//
//                FieldUtils.writeDeclaredField(msg.obj, "intent", originPluginIntent, true);
//                Log.i(TAG, "handleBindService OK");
//            } else {
//                Log.w(TAG, "handleBindService pluginInfo==null");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "handleBindService", e);
//        }
//        return false;
//    }
//
//    private boolean handleCreateService(Message msg) {
//        // handleCreateService((CreateServiceData)msg.obj);
//        try {
//            Object obj = msg.obj;
//            ServiceInfo info = (ServiceInfo) FieldUtils.readField(obj, "info", true);
//            if (info != null) {
//                ServiceInfo newServiceInfo = PluginManager.getInstance().getTargetServiceInfo(info);
//                if (newServiceInfo != null) {
//                    FieldUtils.writeDeclaredField(msg.obj, "info", newServiceInfo, true);
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "handleCreateService", e);
//        }
//        return false;
//    }

    private boolean handleLaunchActivity(Message msg) {
        try {
            Object obj = msg.obj;
            Intent stubIntent = (Intent) FieldUtils.readField(obj, "intent");
            //ActivityInfo activityInfo = (ActivityInfo) FieldUtils.readField(obj, "activityInfo", true);
            stubIntent.setExtrasClassLoader(mHostContext.getClassLoader());
            Intent targetIntent = stubIntent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
            // 这里多加一个isNotShortcutProxyActivity的判断，因为ShortcutProxyActivity的很特殊，启动它的时候，
            // 也会带上一个EXTRA_TARGET_INTENT的数据，就会导致这里误以为是启动插件Activity，所以这里要先做一个判断。
            // 之前ShortcutProxyActivity错误复用了key，但是为了兼容，所以这里就先这么判断吧。
            if (targetIntent != null && !isShortcutProxyActivity(stubIntent)) {
                IPackageManagerHook.fixContextPackageManager(mHostContext);
                ComponentName targetComponentName = targetIntent.resolveActivity(mHostContext.getPackageManager());
                ActivityInfo targetActivityInfo = PluginManager.getInstance().getActivityInfo(targetComponentName, 0);
                if (targetActivityInfo != null) {

                    if (targetComponentName != null && targetComponentName.getClassName().startsWith(".")) {
                        targetIntent.setClassName(targetComponentName.getPackageName(), targetComponentName.getPackageName() + targetComponentName.getClassName());
                    }

                    ResolveInfo resolveInfo = mHostContext.getPackageManager().resolveActivity(stubIntent, 0);
                    ActivityInfo stubActivityInfo = resolveInfo != null ? resolveInfo.activityInfo : null;
                    if (stubActivityInfo != null) {
                        PluginManager.getInstance().reportMyProcessName(stubActivityInfo.processName, targetActivityInfo.processName, targetActivityInfo.packageName);
                    }
                    PluginProcessManager.preLoadApk(mHostContext, targetActivityInfo);
                    ClassLoader pluginClassLoader = PluginProcessManager.getPluginClassLoader(targetComponentName.getPackageName());
                    setIntentClassLoader(targetIntent, pluginClassLoader);
                    setIntentClassLoader(stubIntent, pluginClassLoader);

                    boolean success = false;
                    try {
                        targetIntent.putExtra(Env.EXTRA_TARGET_INFO, targetActivityInfo);
                        if (stubActivityInfo != null) {
                            targetIntent.putExtra(Env.EXTRA_STUB_INFO, stubActivityInfo);
                        }
                        success = true;
                    } catch (Exception e) {
                        Log.e(TAG, "putExtra 1 fail", e);
                    }

                    if (!success && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        try {
                            ClassLoader oldParent = fixedClassLoader(pluginClassLoader);
                            targetIntent.putExtras(targetIntent.getExtras());

                            targetIntent.putExtra(Env.EXTRA_TARGET_INFO, targetActivityInfo);
                            if (stubActivityInfo != null) {
                                targetIntent.putExtra(Env.EXTRA_STUB_INFO, stubActivityInfo);
                            }
                            fixedClassLoader(oldParent);
                            success = true;
                        } catch (Exception e) {
                            Log.e(TAG, "putExtra 2 fail", e);
                        }
                    }

                    if (!success) {
                        Intent newTargetIntent = new Intent();
                        newTargetIntent.setComponent(targetIntent.getComponent());
                        newTargetIntent.putExtra(Env.EXTRA_TARGET_INFO, targetActivityInfo);
                        if (stubActivityInfo != null) {
                            newTargetIntent.putExtra(Env.EXTRA_STUB_INFO, stubActivityInfo);
                        }
                        FieldUtils.writeDeclaredField(msg.obj, "intent", newTargetIntent);
                    } else {
                        FieldUtils.writeDeclaredField(msg.obj, "intent", targetIntent);
                    }
                    FieldUtils.writeDeclaredField(msg.obj, "activityInfo", targetActivityInfo);

                    Log.i(TAG, "handleLaunchActivity OK");
                } else {
                    Log.e(TAG, "handleLaunchActivity oldInfo==null");
                }
            } else {
                Log.e(TAG, "handleLaunchActivity targetIntent==null");
            }
        } catch (Exception e) {
            Log.e(TAG, "handleLaunchActivity FAIL", e);
        }

        if (mCallback != null) {
            return mCallback.handleMessage(msg);
        } else {
            return false;
        }
    }

    private boolean isShortcutProxyActivity(Intent targetIntent) {
        try {
            if (PluginManager.ACTION_SHORTCUT_PROXY.equalsIgnoreCase(targetIntent.getAction())) {
                return true;
            }
            PackageManager pm = mHostContext.getPackageManager();
            ResolveInfo info = pm.resolveActivity(targetIntent, 0);
            if (info != null) {
                String name = info.activityInfo.name;
                if (name != null && name.startsWith(".")) {
                    name = info.activityInfo.packageName + info.activityInfo.name;
                }
                return ShortcutProxyActivity.class.getName().equals(name);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private ClassLoader fixedClassLoader(ClassLoader newParent) {
        ClassLoader nowClassLoader = PluginCallback.class.getClassLoader();
        ClassLoader oldParent = nowClassLoader.getParent();
        try {
            if (newParent != null && newParent != oldParent) {
                FieldUtils.writeField(nowClassLoader, "parent", newParent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldParent;
    }

    private void setIntentClassLoader(Intent intent, ClassLoader classLoader) {
        try {
            Bundle mExtras = (Bundle) FieldUtils.readField(intent, "mExtras");
            if (mExtras != null) {
                mExtras.setClassLoader(classLoader);
            } else {
                Bundle value = new Bundle();
                value.setClassLoader(classLoader);
                FieldUtils.writeField(intent, "mExtras", value);
            }
        } catch (Exception e) {
        } finally {
            intent.setExtrasClassLoader(classLoader);
        }
    }

}