package com.example.TestPlugin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.Log;

import java.io.File;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread() {
            @Override
            public void run() {
                doInstall(new File(Environment.getExternalStorageDirectory(), "app.apk"));
            }
        }.start();
    }

    public Activity getActivity() {
        return this;
    }

    private void doInstall(final File apkPath) {
        try {
            final PackageInfo info = getPackageManager().getPackageArchiveInfo(apkPath.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (info == null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "apk损坏\n" + apkPath.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            final int re = PluginManager.getInstance().installPackage(apkPath.getAbsolutePath(), 0);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    switch (re) {
                        case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                            Toast.makeText(getActivity(), "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_FAILED_NOT_SUPPORT_ABI:
                            Toast.makeText(getActivity(), "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_SUCCEEDED:
                            Toast.makeText(getActivity(), "安装完成", Toast.LENGTH_SHORT).show();
                            PackageManager pm = getActivity().getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(info.packageName);
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Log.i("DroidPlugin", "start " + info.packageName + "@" + intent);
                                startActivity(intent);
                            } else {
                                Log.e("DroidPlugin", "pm " + pm.toString() + " no find intent " + info.packageName);
                            }
                            break;
                    }

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
