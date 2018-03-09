package com.gg.rxbase.net.retrofit;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * @author Guang1234567
 * @date 2017/5/27 19:38:56
 */

public class HostSelectionInterceptor implements Interceptor {
    private volatile String host;

    public HostSelectionInterceptor setHost(String host) {
        this.host = host;
        return this;
    }

    @Override
    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        String host = this.host;
        if (host != null) {
            HttpUrl newUrl = request.url().newBuilder()
                    .host(host)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}
