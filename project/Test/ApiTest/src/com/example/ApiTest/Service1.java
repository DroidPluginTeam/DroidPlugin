package com.example.ApiTest;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service1 extends BaseService {

    @Override
    String getTag() {
        return Service1.class.getSimpleName();
    }

    public  Service1() {
        tag = getTag();
    }

}
