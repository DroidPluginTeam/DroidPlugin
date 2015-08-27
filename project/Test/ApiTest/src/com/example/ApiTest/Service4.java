package com.example.ApiTest;

/**
 * Created by zhangyong6 on 2015/3/2.
 */
public class Service4 extends BaseService {

    @Override
    String getTag() {
        return Service4.class.getSimpleName();
    }

    public Service4() {
        tag = getTag();
    }


}
