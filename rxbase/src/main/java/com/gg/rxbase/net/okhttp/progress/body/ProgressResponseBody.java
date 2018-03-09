package com.gg.rxbase.net.okhttp.progress.body;

import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {

    private Response mWrappedResponse;
    private Response mOriginalResponse;

    private final ResponseBody mResponseBody;
    private final IProgressListener mProgressListener;
    private BufferedSource mBufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, IProgressListener progressListener) {
        mResponseBody = responseBody;
        mProgressListener = progressListener;
    }

    public static ProgressResponseBody create(ResponseBody responseBody, IProgressListener progressListener) {
        return new ProgressResponseBody(responseBody, progressListener);
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long mTotalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                mTotalBytesRead += bytesRead != -1 ? bytesRead : 0;
                long curContentLength = mResponseBody.contentLength();
                if (mProgressListener != null) {
                    mProgressListener.update(mTotalBytesRead,
                            curContentLength,
                            bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }

    public Response getOriginalResponse() {
        return mOriginalResponse;
    }

    public void setOriginalResponse(Response originalResponse) {
        mOriginalResponse = originalResponse;
    }

    public Response getWrappedResponse() {
        return mWrappedResponse;
    }

    public void setWrappedResponse(Response wrappedResponse) {
        mWrappedResponse = wrappedResponse;
    }
}
