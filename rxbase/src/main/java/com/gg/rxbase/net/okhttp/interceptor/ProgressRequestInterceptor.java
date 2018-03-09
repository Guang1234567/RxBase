package com.gg.rxbase.net.okhttp.interceptor;

import com.gg.rxbase.net.okhttp.progress.body.ProgressRequestBody;
import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ProgressRequestInterceptor extends ProgressInterceptor {

    public ProgressRequestInterceptor(IProgressListener progressListener) {
        super(progressListener);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }

        ProgressRequestBody progressRequestBody = ProgressRequestBody.create(originalRequest.body(), getProgressListener());
        Request progressRequest = originalRequest.newBuilder()
                .put(progressRequestBody)
                .build();

        progressRequestBody.setOriginalRequest(originalRequest);
        progressRequestBody.setWrappedRequest(progressRequest);

        Response response = chain.proceed(progressRequest);
        return response;
    }
}
