package com.ly.network.retrofit;

import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by LanYang on 2018/9/27
 */
public class RetrofitLogger implements HttpLoggingInterceptor.Logger {
    private boolean isShowLog;

    public RetrofitLogger(boolean isShowLog) {
        this.isShowLog = isShowLog;
    }

    @Override
    public void log(String message) {
        if (isShowLog) {
            Platform.get().log(INFO, message, null);
        }
    }
}
