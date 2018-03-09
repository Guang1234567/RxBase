package com.gg.rxbase.net.okhttp.progress.listener;

public interface IProgressListener {
    void update(long bytesReadOrWrite,
                long contentLength,
                boolean done);
}
