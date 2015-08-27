package com.example.ApiTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class ActivityTestActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_test);
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
}
