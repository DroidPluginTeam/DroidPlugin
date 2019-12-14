package com.example.ApiTest;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service3 extends Service1 {

    @Override
    String getTag() {
        return Service3.class.getSimpleName();
    }

    public Service3() {
        tag = getTag();
    }


}
