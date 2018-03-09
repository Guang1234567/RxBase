package com.gg.rxbase.net.retrofit;

import android.net.ParseException;

import com.gg.rxbase.log.Log;
import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.functions.Consumer;
import retrofit2.HttpException;

/**
 * @author Guang1234567
 * @date 2017/7/6 20:15
 */

public interface ApiThrowableHandler extends Consumer<Throwable> {
    String TAG = "ApiThrowableHandler";

    ApiThrowableHandler DEFAULT = new ApiThrowableHandler() {
        @Override
        public void accept(Throwable e) throws Exception {
            if (e instanceof HttpException) {             //HTTP错误

            } else if (e instanceof JsonParseException
                    || e instanceof JSONException
                    || e instanceof ParseException) { //均视为解析错误

            } else if (e instanceof ConnectException
                    || e instanceof SocketTimeoutException
                    || e instanceof ConnectTimeoutException) { // "连接失败"

            } else {

            }
            Log.e(TAG, "(DEFAULT) # accept : ", e);
        }
    };
}

