package com.gg.rxbase.net.okhttp.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * @author Guang1234567
 * @date 2017/5/27 17:02:03
 */

public class CommonParamsInterceptor implements Interceptor {

    private Map<String, String> mHeadsMap;

    public CommonParamsInterceptor(Map<String, String> headsMap) {
        if (headsMap == null) {
            headsMap = new HashMap<>();
        }
        mHeadsMap = headsMap;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request request;

        HttpUrl.Builder urlBuilder = originRequest.url().newBuilder();
        Iterator<Map.Entry<String, String>> iterator = mHeadsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!TextUtils.isEmpty(entry.getKey())) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        request = originRequest.newBuilder().url(urlBuilder.build()).build();
        return chain.proceed(request);
    }
}
