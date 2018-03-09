package com.gg.rxbase.net.okhttp.interceptor;

import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import okhttp3.Interceptor;

public abstract class ProgressInterceptor implements Interceptor {
    private IProgressListener mProgressListener;

    public ProgressInterceptor(IProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    protected IProgressListener getProgressListener() {
        return mProgressListener;
    }
}
