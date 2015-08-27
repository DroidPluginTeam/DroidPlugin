package com.example.ApiTest;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service2 extends BaseService {

    @Override
    String getTag() {
        return Service2.class.getSimpleName();
    }

    public Service2() {
        tag = getTag();
    }


}
