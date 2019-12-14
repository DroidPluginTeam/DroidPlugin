package com.example.ApiTest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangyong6 on 2015/10/22.
 */
public abstract class LaunchModeTestActivity extends AppCompatActivity implements View.OnClickListener {
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
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);
        findViewById(R.id.button14).setOnClickListener(this);
        findViewById(R.id.button15).setOnClickListener(this);
        findViewById(R.id.button16).setOnClickListener(this);
        findViewById(R.id.button17).setOnClickListener(this);
        findViewById(R.id.button18).setOnClickListener(this);
        findViewById(R.id.button19).setOnClickListener(this);
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
        } else if (id == R.id.button4) {
            startActivity(new Intent(this, SingleInstanceActivity.class));
        } else if (id == R.id.button5) {
            startActivity(new Intent(this, SingleTopActivity.SingleTopActivity1.class));
        } else if (id == R.id.button6) {
            startActivity(new Intent(this, SingleTopActivity.SingleTopActivity2.class));
        } else if (id == R.id.button7) {
            startActivity(new Intent(this, SingleTopActivity.SingleTopActivity3.class));
        } else if (id == R.id.button8) {
            startActivity(new Intent(this, SingleTopActivity.SingleTopActivity4.class));
        } else if (id == R.id.button9) {
            startActivity(new Intent(this, SingleTopActivity.SingleTopActivity5.class));
        } else if (id == R.id.button10) {
            startActivity(new Intent(this, SingleTaskActivity.SingleTaskActivity1.class));
        } else if (id == R.id.button11) {
            startActivity(new Intent(this, SingleTaskActivity.SingleTaskActivity2.class));
        } else if (id == R.id.button12) {
            startActivity(new Intent(this, SingleTaskActivity.SingleTaskActivity3.class));
        } else if (id == R.id.button13) {
            startActivity(new Intent(this, SingleTaskActivity.SingleTaskActivity4.class));
        } else if (id == R.id.button14) {
            startActivity(new Intent(this, SingleTaskActivity.SingleTaskActivity5.class));
        } else if (id == R.id.button15) {
            startActivity(new Intent(this, SingleInstanceActivity.SingleInstanceActivity1.class));
        } else if (id == R.id.button16) {
            startActivity(new Intent(this, SingleInstanceActivity.SingleInstanceActivity2.class));
        } else if (id == R.id.button17) {
            startActivity(new Intent(this, SingleInstanceActivity.SingleInstanceActivity3.class));
        } else if (id == R.id.button18) {
            startActivity(new Intent(this, SingleInstanceActivity.SingleInstanceActivity4.class));
        } else if (id == R.id.button19) {
            startActivity(new Intent(this, SingleInstanceActivity.SingleInstanceActivity5.class));
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
