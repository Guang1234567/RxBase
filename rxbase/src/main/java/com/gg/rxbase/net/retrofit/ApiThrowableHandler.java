package com.gg.rxbase.net.retrofit;

import android.net.ParseException;
import android.util.Log;

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

    /**
     * default implement
     */
    ApiThrowableHandler DEFAULT = new ApiThrowableHandler() {
        @Override
        public void accept(Throwable e) throws Exception {
            Log.e(TAG, "(DEFAULT) # accept : ", e);
            if (e instanceof HttpException) {             //HTTP错误, 如配置了 Https 但证书不匹配?
                // handle
            } else if (e instanceof JsonParseException
                    || e instanceof JSONException
                    || e instanceof ParseException) { //均视为协议解析错误, 此时应该检查协议与代码是否一致
                // handle
            } else if (e instanceof ConnectException
                    || e instanceof SocketTimeoutException
                    || e instanceof ConnectTimeoutException) { // "连接失败"
                // handle
            } else if (e instanceof ApiException) { // 内部协议错误
                ApiException apiException = (ApiException) e;
                ApiCode code = apiException.getErrorCode();
                switch (code) {
                    case ERROR_CLIENT_AUTHORIZED:
                        // handle
                        break;
                    case ERROR_USER_AUTHORIZED:
                        // handle
                        break;
                    case ERROR_REQUEST_PARAM:
                        // handle
                        break;
                    case ERROR_PARAM_CHECK:
                        // handle
                        break;
                    case ERROR_OTHER:
                        // handle
                        break;
                    case ERROR_NO_INTERNET:
                        // handle
                        break;
                }
            } else {
                // handle
            }
        }
    };
}

