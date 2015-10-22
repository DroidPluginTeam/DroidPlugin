package com.example.ApiTest;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangyong6 on 2015/10/22.
 */
public abstract class LaunchModeTestActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchmode);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("当前实例：" + this);

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        String text = "<b>当前Activity栈：</b><br/>" + getCurrentStack();
        textView2.setText(Html.fromHtml(text));
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            startActivity(new Intent(this, StandardActivity.class));
        } else if (id == R.id.button2) {
            startActivity(new Intent(this, SingleTopActivity.class));
        } else if (id == R.id.button3) {
            startActivity(new Intent(this, SingleTaskActivity.class));
        } else if (id == R.id.button4) {
            startActivity(new Intent(this, SingleInstanceActivity.class));
        }
    }

    public String getCurrentStack() {
        StringBuilder sb = new StringBuilder();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < infos.size(); i++) {
            ActivityManager.RunningTaskInfo info = infos.get(i);
            sb.append("<font color=\"#ff0000\">Stack" + i + "</font>").append("<br/>");
            sb.append("\t <b><i>ID:</i></b>" + info.id).append("<br/>");
            sb.append("\t <b><i>Num Running:</i></b>" + info.numRunning).append("<br/>");
            sb.append("\t <b><i>Num Activities:</i></b>" + info.numActivities).append("<br/>");
            sb.append("\t <b><i>Description:</i></b>" + info.description).append("<br/>");
            sb.append("\t <b><i>Top Activity:</i></b>" + toComponentName(info.topActivity)).append("<br/>");
            sb.append("\t <b><i>Base Activity:</i></b>" + toComponentName(info.baseActivity)).append("<br/>");
        }
        return sb.toString();
    }

    private String toComponentName(ComponentName cn) {
        if (cn == null) {
            return null;
        } else {
            return cn.getPackageName() + "/" + cn.getShortClassName();
        }
    }
}
