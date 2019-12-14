package com.example.ApiTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


public class ActivityTestActivity extends AppCompatActivity implements View.OnClickListener {

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
            startActivityForResult(new Intent(this, StandardActivity.class), 1986);
        } else if (id == R.id.button2) {
            startActivity(new Intent(this, SingleTopActivity.class));
        } else if (id == R.id.button3) {
            startActivity(new Intent(this, SingleTaskActivity.class));
        } else if (id == R.id.button4) {
            startActivity(new Intent(this, SingleInstanceActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ActivityTestActivity", String.format("onActivityResult，requestCode=%s,resultCode=%s", requestCode, resultCode));
    }
}
