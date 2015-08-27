package com.example.ApiTest;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by zhangyong6 on 2015/3/11.
 */
public class ServiceTest2 extends ServiceTest1 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service1 = new Intent(this, Service3.class);
        service2 = new Intent(this, Service4.class);
    }
}