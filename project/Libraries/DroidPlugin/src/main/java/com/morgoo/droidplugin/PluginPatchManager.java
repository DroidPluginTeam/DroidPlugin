package com.morgoo.droidplugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.morgoo.droidplugin.pm.PluginManager;

/**
 * Created by zhanglong on 2015/12/21.
 */

// 处理插件异常情况
public class PluginPatchManager {

    private static final int MAX_WAIT_DAEAMON_TIME = 5000;
    private static final int CHECK_DAEAMON_INTERVAL = 300;

    private Context mContext;

    private Runnable mDelayRunnable;
    private Intent mDelayIntent;
    private Handler MainThreadHandler;
    private long lStartTime = 0;

    private static PluginPatchManager s_inst = new PluginPatchManager();
    public static PluginPatchManager getInstance() {
        return s_inst;
    }

    public void init(Context context){
        mContext = context;
    }

    // 是否可以启动插件的activity
    public boolean canStartPluginActivity(Intent intent) {
        if (intent == null || PluginManager.getInstance().isConnected())
            return true;

        ComponentName name = intent.getComponent();
        if (name != null && mContext != null && !name.getPackageName().equals(mContext.getPackageName()))
            return false;

        return true;
    }

    public boolean startPluginActivity(Intent intent) {
        if (intent == null)
            return false;

        if (PluginManager.getInstance().isConnected()) {
            mDelayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mDelayIntent);
            return true;
        }

        PluginManager.getInstance().connectToService();
        initInner();
        mDelayIntent = intent;
        lStartTime = System.currentTimeMillis();
        MainThreadHandler.postDelayed(mDelayRunnable, CHECK_DAEAMON_INTERVAL);
        return true;
    }

    private void postDelayImpl() {
        if (PluginManager.getInstance().isConnected()) {
            mDelayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mDelayIntent);
        } else {
            if (System.currentTimeMillis() - lStartTime < MAX_WAIT_DAEAMON_TIME) {
                MainThreadHandler.postDelayed(mDelayRunnable, CHECK_DAEAMON_INTERVAL);
            }
        }
    }

    private void initInner() {
        if (MainThreadHandler == null)
            MainThreadHandler = new Handler(Looper.getMainLooper());

        if (mDelayRunnable == null) {
            mDelayRunnable = new Runnable() {
                @Override
                public void run() {
                    postDelayImpl();
                }
            };
        }
    }
}
