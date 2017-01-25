package com.example.administrator.coolweather.util;

/**
 * Created by Administrator on 2017/1/24.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
