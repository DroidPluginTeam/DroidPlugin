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

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.morgoo.droidplugin.core.PluginProcessManager;
import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.helper.Log;
import com.morgoo.droidplugin.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/28.
 */
public class INotificationManagerHookHandle extends BaseHookHandle {

    private static final String TAG = INotificationManagerHookHandle.class.getSimpleName();

    public INotificationManagerHookHandle(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        init1();
        sHookedMethodHandlers.put("enqueueNotification", new enqueueNotification(mHostContext));
        sHookedMethodHandlers.put("cancelNotification", new cancelNotification(mHostContext));
        sHookedMethodHandlers.put("cancelAllNotifications", new cancelAllNotifications(mHostContext));
        sHookedMethodHandlers.put("enqueueToast", new enqueueToast(mHostContext));
        sHookedMethodHandlers.put("cancelToast", new cancelToast(mHostContext));
        sHookedMethodHandlers.put("enqueueNotificationWithTag", new enqueueNotificationWithTag(mHostContext));
        sHookedMethodHandlers.put("enqueueNotificationWithTagPriority", new enqueueNotificationWithTagPriority(mHostContext));
        sHookedMethodHandlers.put("cancelNotificationWithTag", new cancelNotificationWithTag(mHostContext));
        sHookedMethodHandlers.put("setNotificationsEnabledForPackage", new setNotificationsEnabledForPackage(mHostContext));
        sHookedMethodHandlers.put("areNotificationsEnabledForPackage", new areNotificationsEnabledForPackage(mHostContext));
    }
//    public void cancelAllNotifications(String pkg, int userId);
//    public void enqueueToast(String pkg, ITransientNotification callback, int duration);
//    public void cancelToast(String pkg, ITransientNotification callback);
//    public void enqueueNotificationWithTag(String pkg, String opPkg, String tag, int id, Notification notification, int[] idReceived, int userId);
//    public void cancelNotificationWithTag(String pkg, String tag, int id, int userId);
//    public void setNotificationsEnabledForPackage(String pkg, int uid, boolean enabled);
//    public boolean areNotificationsEnabledForPackage(String pkg, int uid);
//    public void setPackagePriority(String pkg, int uid, int priority);
//    public int getPackagePriority(String pkg, int uid);
//    public void setPackageVisibilityOverride(String pkg, int uid, int visibility);
//    public int getPackageVisibilityOverride(String pkg, int uid);
//    public StatusBarNotification[] getActiveNotifications(String callingPkg);
//    public StatusBarNotification[] getHistoricalNotifications(String callingPkg, int count);
//    public void registerListener(INotificationListener listener, ComponentName component, int userid);
//    public void unregisterListener(INotificationListener listener, int userid);
//    public void cancelNotificationFromListener(INotificationListener token, String pkg, String tag, int id);
//    public void cancelNotificationsFromListener(INotificationListener token, String[] keys);
//    public pm.ParceledListSlice getActiveNotificationsFromListener(INotificationListener token, String[] keys, int trim);
//    public void requestHintsFromListener(INotificationListener token, int hints);
//    public int getHintsFromListener(INotificationListener token);
//    public void requestInterruptionFilterFromListener(INotificationListener token, int interruptionFilter);
//    public int getInterruptionFilterFromListener(INotificationListener token);
//    public void setOnNotificationPostedTrimFromListener(INotificationListener token, int trim);
//    public ComponentName getEffectsSuppressor();
//    public boolean matchesCallFilter(android.os.Bundle extras);
//    public ZenModeConfig getZenModeConfig();
//    public boolean setZenModeConfig(ZenModeConfig config);
//    public void notifyConditions(String pkg, IConditionProvider provider, Condition[] conditions);
//    public void requestZenModeConditions(IConditionListener callback, int relevance);
//    public void setZenModeCondition(Condition condition);
//    public void setAutomaticZenModeConditions(android.net.Uri[] conditionIds);
//    public Condition[] getAutomaticZenModeConditions();

    private static Map<Integer, String> sSystemLayoutResIds = new HashMap<Integer, String>(0);

    private static void init1() {
        try {
            //read all com.android.internal.R
            Class clazz = Class.forName("com.android.internal.R$layout");
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //public static final
                if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    try {
                        int id = field.getInt(null);
                        sSystemLayoutResIds.put(id, field.getName());
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "read com.android.internal.R$layout.%s", e, field.getName());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "read com.android.internal.R$layout", e);
        }
    }

    public static int getResIdByName(String name) {
        for (Integer integer : sSystemLayoutResIds.keySet()) {
            if (TextUtils.equals(name, sSystemLayoutResIds.get(integer))) {
                return integer;
            }
        }
        return -1;
    }

    private class MyNotification extends HookedMethodHandler {
        public MyNotification(Context context) {
            super(context);
        }

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int index = 0;
            if (args != null && args.length > index) {
                if (args[index] instanceof String) {
                    String pkg = (String) args[index];
                    if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                        args[index] = mHostContext.getPackageName();
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private int findFisrtNotificationIndex(Object[] args) {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Notification) {
                    return i;
                }
            }
        }
        return -1;
    }

    private class enqueueNotification extends MyNotification {
        public enqueueNotification(Context context) {
            super(context);
        }

        //2.3.2_r1, 4.0.1_r1
        /* public void enqueueNotification(String pkg, int id, Notification notification, int[] idReceived);*/
        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof String) {
                String pkg = (String) args[index];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[index] = mHostContext.getPackageName();
                }
            }
            final int index2 = findFisrtNotificationIndex(args);
            if (index2 >= 0) {
                Notification notification = (Notification) args[index2];//nobug
                if (isPluginNotification(notification)) {
                    if (shouldBlock(notification)) {
                        Log.e(TAG, "We has blocked a notification[%s]", notification);
                        return true;
                    } else {
                        //这里要修改通知。
                        hackNotification(notification);
                        return false;
                    }
                }
            }
            return false;
        }
    }


    private void hackRemoteViews(RemoteViews remoteViews) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        if (remoteViews != null && !TextUtils.equals(remoteViews.getPackage(), mHostContext.getPackageName())) {
            if (sSystemLayoutResIds.containsKey(remoteViews.getLayoutId())) {
                Object mActionsObj = FieldUtils.readField(remoteViews, "mActions");
                if (mActionsObj instanceof Collection) {
                    Collection mActions = (Collection) mActionsObj;
                    String aPackage = remoteViews.getPackage();
                    Application pluginContent = PluginProcessManager.getPluginContext(aPackage);
                    if (pluginContent != null) {
                        Iterator iterable = mActions.iterator();
                        Class TextViewDrawableActionClass = null;
                        try {
                            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                                TextViewDrawableActionClass = Class.forName(RemoteViews.class.getName() + "$TextViewDrawableAction");
                            }
                        } catch (ClassNotFoundException e) {
                        }
                        Class ReflectionActionClass = Class.forName(RemoteViews.class.getName() + "$ReflectionAction");
                        while (iterable.hasNext()) {
                            Object action = iterable.next();
                            if (ReflectionActionClass.isInstance(action)) {//???这里这样是对的么？
                                String methodName = (String) FieldUtils.readField(action, "methodName");
                                //String methodName;,int type; Object value;
                                if ("setImageResource".equals(methodName)) { //setInt(viewId, "setImageResource", srcId);
                                    Object BITMAP = FieldUtils.readStaticField(action.getClass(), "BITMAP");
                                    int resId = (Integer) FieldUtils.readField(action, "value");
                                    Bitmap bitmap = BitmapFactory.decodeResource(pluginContent.getResources(), resId);
                                    FieldUtils.writeField(action, "type", BITMAP);
                                    FieldUtils.writeField(action, "value", bitmap);
                                    FieldUtils.writeField(action, "methodName", "setImageBitmap");
                                } else if ("setImageURI".equals(methodName)) {//setUri(viewId, "setImageURI", uri);
                                    iterable.remove();   //TODO RemoteViews.setImageURI 其实应该适配的。
                                } else if ("setLabelFor".equals(methodName)) {
                                    iterable.remove();   //TODO RemoteViews.setLabelFor 其实应该适配的。
                                }
                            } else if (TextViewDrawableActionClass != null && TextViewDrawableActionClass.isInstance(action)) {
                                iterable.remove();
//                                if ("setTextViewCompoundDrawables".equals(methodName)) {
//                                    iterable.remove();   //TODO RemoteViews.setTextViewCompoundDrawables 其实应该适配的。
//                                } else if ("setTextViewCompoundDrawablesRelative".equals(methodName)) {
//                                    iterable.remove();   //TODO RemoteViews.setTextViewCompoundDrawablesRelative 其实应该适配的。
//                                }
                            }
                        }
                    }
                }

            }
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                FieldUtils.writeField(remoteViews, "mApplication", mHostContext.getApplicationInfo());
            } else {
                FieldUtils.writeField(remoteViews, "mPackage", mHostContext.getPackageName());
            }
        }
    }

    private boolean shouldBlockByRemoteViews(RemoteViews remoteViews) {
        if (remoteViews == null) {
            return false;
        } else if (remoteViews != null && sSystemLayoutResIds.containsKey(remoteViews.getLayoutId())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean shouldBlock(Notification notification) {
        if (shouldBlockByRemoteViews(notification.contentView)) {
            return true;
        }
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            if (shouldBlockByRemoteViews(notification.tickerView)) {
                return true;
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            if (shouldBlockByRemoteViews(notification.bigContentView)) {
                return true;
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            if (shouldBlockByRemoteViews(notification.headsUpContentView)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPluginNotification(Notification notification) {
        if (notification == null) {
            return false;
        }

        if (notification.contentView != null && !TextUtils.equals(mHostContext.getPackageName(), notification.contentView.getPackage())) {
            return true;
        }

        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            if (notification.tickerView != null && !TextUtils.equals(mHostContext.getPackageName(), notification.tickerView.getPackage())) {
                return true;
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            if (notification.bigContentView != null && !TextUtils.equals(mHostContext.getPackageName(), notification.bigContentView.getPackage())) {
                return true;
            }
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            if (notification.headsUpContentView != null && !TextUtils.equals(mHostContext.getPackageName(), notification.headsUpContentView.getPackage())) {
                return true;
            }
        }
        return false;
    }

    private void hackNotification(Notification notification) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        //        remoteViews com.android.internal.R.layout.notification_template_material_media
        //        com.android.internal.R.layout.notification_template_material_big_media_narrow;
        //        com.android.internal.R.layout.notification_template_material_big_media;
        //        //getBaseLayoutResource
        //        R.layout.notification_template_material_base;
        //        //getBigBaseLayoutResource
        //        R.layout.notification_template_material_big_base;
        //        //getBigPictureLayoutResource
        //        R.layout.notification_template_material_big_picture;
        //        //getBigTextLayoutResource
        //        R.layout.notification_template_material_big_text;
        //        //getInboxLayoutResource
        //        R.layout.notification_template_material_inbox;
        //        //getActionLayoutResource
        //        R.layout.notification_material_action;
        //        //getActionTombstoneLayoutResource
        //        R.layout.notification_material_action_tombstone;
        if (notification != null) {
            notification.icon = mHostContext.getApplicationInfo().icon;
            if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                hackRemoteViews(notification.tickerView);
            }
            hackRemoteViews(notification.contentView);
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                hackRemoteViews(notification.bigContentView);
            }
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                hackRemoteViews(notification.headsUpContentView);
            }
        }
    }

    private class cancelNotification extends MyNotification {
        public cancelNotification(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /* public void cancelNotification(String pkg, int id);*/

    }

    private class cancelAllNotifications extends MyNotification {
        public cancelAllNotifications(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /*   public void cancelAllNotifications(String pkg);*/
    }

    private class enqueueToast extends MyNotification {
        public enqueueToast(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /*   public void enqueueToast(String pkg, ITransientNotification callback, int duration) ;*/

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            //这里适配在android 5.0的机器上无法现实toast的问题。但是我也不知道还有那些机器需要这样做。
            if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                int index = 1;
                if (args != null && args.length > index) {
                    Object obj = args[index];
                    View view = (View) FieldUtils.readField(obj, "mView");
                    View nextView = (View) FieldUtils.readField(obj, "mNextView");
                    if (nextView != null) {
                        FieldUtils.writeField(nextView, "mContext", mHostContext);
                    }
                    if (view != null) {
                        FieldUtils.writeField(view, "mContext", mHostContext);
                    }
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class cancelToast extends MyNotification {
        public cancelToast(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /* public void cancelToast(String pkg, ITransientNotification callback);*/

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            int index = 1;
            if (args != null && args.length > index) {
                Object obj = args[index];
                View view = (View) FieldUtils.readField(obj, "mView");
                View nextView = (View) FieldUtils.readField(obj, "mNextView");
                if (nextView != null) {
                    FieldUtils.writeField(nextView, "mContext", mHostContext);
                }
                if (view != null) {
                    FieldUtils.writeField(view, "mContext", mHostContext);
                }
            }
            return super.beforeInvoke(receiver, method, args);
        }
    }

    private class enqueueNotificationWithTag extends MyNotification {
        public enqueueNotificationWithTag(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /*public void enqueueNotificationWithTag(String pkg, String tag, int id, Notification notification, int[] idReceived) ;*/

        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof String) {
                String pkg = (String) args[index];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[index] = mHostContext.getPackageName();
                }
            }
            final int index2 = findFisrtNotificationIndex(args);
            if (index2 >= 0) {
                Notification notification = (Notification) args[index2];//nobug
                if (isPluginNotification(notification)) {
                    if (shouldBlock(notification)) {
                        Log.e(TAG, "We has blocked a notification[%s]", notification);
                        return true;
                    } else {
                        //这里要修改通知。
                        hackNotification(notification);
                        return false;
                    }
                }
            }
            return false;
        }
    }

    private class enqueueNotificationWithTagPriority extends MyNotification {
        public enqueueNotificationWithTagPriority(Context context) {
            super(context);
        }

        //4.0.1_r1
        /*public void enqueueNotificationWithTagPriority(String pkg, String tag, int id, int priority, Notification notification, int[] idReceived);*/
        @Override
        protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
            final int index = 0;
            if (args != null && args.length > index && args[index] instanceof String) {
                String pkg = (String) args[index];
                if (!TextUtils.equals(pkg, mHostContext.getPackageName())) {
                    args[index] = mHostContext.getPackageName();
                }
            }

            final int index2 = findFisrtNotificationIndex(args);
            if (index2 >= 0) {
                Notification notification = (Notification) args[index2];//nobug
                if (isPluginNotification(notification)) {
                    if (shouldBlock(notification)) {
                        Log.e(TAG, "We has blocked a notification[%s]", notification);
                        return true;
                    } else {
                        //这里要修改通知。
                        hackNotification(notification);
                        return false;
                    }
                }
            }
            return false;
        }
    }

    private class cancelNotificationWithTag extends MyNotification {
        public cancelNotificationWithTag(Context context) {
            super(context);
        }
        //2.3.2_r1, 4.0.1_r1
        /*   public void cancelNotificationWithTag(String pkg, String tag, int id) ;*/
    }

    private class setNotificationsEnabledForPackage extends MyNotification {
        public setNotificationsEnabledForPackage(Context context) {
            super(context);
        }
    }

    private class areNotificationsEnabledForPackage extends MyNotification {
        public areNotificationsEnabledForPackage(Context context) {
            super(context);
        }
    }
}
