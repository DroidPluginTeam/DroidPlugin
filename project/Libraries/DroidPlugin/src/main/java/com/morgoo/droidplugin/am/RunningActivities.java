package com.morgoo.droidplugin.am;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 正在运行的Activity列表
 * Created by zhangyong6 on 2015/10/20.
 */
public class RunningActivities {

    private static final String TAG = RunningActivities.class.getSimpleName();
    private static Map<Activity, RunningActivityRecord> mRunningActivityList = new HashMap<>();
    private static Map<Integer, RunningActivityRecord> mRunningSingleStandardActivityList = new HashMap<>();
    private static Map<Integer, RunningActivityRecord> mRunningSingleTopActivityList = new HashMap<>();
    private static Map<Integer, RunningActivityRecord> mRunningSingleTaskActivityList = new HashMap<>();
    private static Map<Integer, RunningActivityRecord> mRunningSingleInstanceActivityList = new HashMap<>();

    public static void onActivtyOnNewIntent(Activity activity, ActivityInfo targetInfo, ActivityInfo stubInfo, Intent intent) {
        //TODO
    }


    private static class RunningActivityRecord {
        private final Activity activity;
        private final ActivityInfo targetActivityInfo;
        private final ActivityInfo stubActivityInfo;
        private int index = 0;

        private RunningActivityRecord(Activity activity, ActivityInfo targetActivityInfo, ActivityInfo stubActivityInfo, int index) {
            this.activity = activity;
            this.targetActivityInfo = targetActivityInfo;
            this.stubActivityInfo = stubActivityInfo;
            this.index = index;
        }

    }

    public static void onActivtyCreate(Activity activity, ActivityInfo targetActivityInfo, ActivityInfo stubActivityInfo) {
        synchronized (mRunningActivityList) {
            RunningActivityRecord value = new RunningActivityRecord(activity, targetActivityInfo, stubActivityInfo, findMaxIndex() + 1);
            mRunningActivityList.put(activity, value);
            if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                mRunningSingleStandardActivityList.put(value.index, value);
            } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TOP) {
                mRunningSingleTopActivityList.put(value.index, value);
            } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK) {
                mRunningSingleTaskActivityList.put(value.index, value);
            } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE) {
                mRunningSingleInstanceActivityList.put(value.index, value);
            }
        }
    }

    public static void onActivtyDestory(Activity activity) {
        synchronized (mRunningActivityList) {
            RunningActivityRecord value = mRunningActivityList.remove(activity);
            if (value != null) {
                ActivityInfo targetActivityInfo = value.targetActivityInfo;
                if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                    mRunningSingleStandardActivityList.remove(value.index);
                } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TOP) {
                    mRunningSingleTopActivityList.remove(value.index);
                } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK) {
                    mRunningSingleTaskActivityList.remove(value.index);
                } else if (targetActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE) {
                    mRunningSingleInstanceActivityList.remove(value.index);
                }
            }
        }
    }

    //在启动一个Activity时调用
    public static void beforeStartActivity() {
        synchronized (mRunningActivityList) {
            for (RunningActivityRecord record : mRunningActivityList.values()) {
                if (record.stubActivityInfo.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
                    continue;
                } else if (record.stubActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TOP) {
                    doFinshIt(mRunningSingleTopActivityList);
                } else if (record.stubActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK) {
                    doFinshIt(mRunningSingleTopActivityList);
                } else if (record.stubActivityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE) {
                    doFinshIt(mRunningSingleTopActivityList);
                }
            }
        }
    }

    private static final Comparator<RunningActivityRecord> sRunningActivityRecordComparator = new Comparator<RunningActivityRecord>() {
        @Override
        public int compare(RunningActivityRecord lhs, RunningActivityRecord rhs) {
            if (lhs != null && rhs != null) {
                if (lhs.index > rhs.index) {
                    return 1;
                } else if (lhs.index < rhs.index) {
                    return -1;
                } else {
                    return 0;
                }
            } else if (lhs != null && rhs == null) {
                return 1;
            } else if (lhs == null && rhs != null) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    private static void doFinshIt(Map<Integer, RunningActivityRecord> runningActivityList) {
        if (runningActivityList != null && runningActivityList.size() >= PluginManager.STUB_NO_ACTIVITY_MAX_NUM - 1) {
            List<RunningActivityRecord> activitys = new ArrayList<>(runningActivityList.size());
            activitys.addAll(runningActivityList.values());
            Collections.sort(activitys, sRunningActivityRecordComparator);
            RunningActivityRecord record = activitys.get(0);
            if (record.activity != null && !record.activity.isFinishing()) {
                record.activity.finish();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    record.activity.finishAndRemoveTask();
//                    record.activity.releaseInstance();
//                } else {
//                    record.activity.finish();
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    record.activity.finishAffinity();
//                }
//                Log.d(TAG, "ZYActivity.finish stub=%s target=%s", record.stubActivityInfo, record.targetActivityInfo);
            }
        }

    }

    private static int findMaxIndex() {
        int max = 0;
        synchronized (mRunningActivityList) {
            for (RunningActivityRecord record : mRunningActivityList.values()) {
                if (max < record.index) {
                    max = record.index;
                }
            }
        }
        return max;
    }
}
