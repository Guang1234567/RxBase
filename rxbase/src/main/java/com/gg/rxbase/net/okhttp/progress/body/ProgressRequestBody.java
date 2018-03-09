/**
 * Copyright 2015 ZhangQu Li
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gg.rxbase.net.okhttp.progress.body;

import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private Request mWrappedRequest;
    private Request mOriginalRequest;

    private final RequestBody mRequestBody;
    private final IProgressListener mProgressListener;
    private BufferedSink mBufferedSink;

    private ProgressRequestBody(RequestBody requestBody, IProgressListener progressListener) {
        mRequestBody = requestBody;
        mProgressListener = progressListener;
    }

    public static ProgressRequestBody create(RequestBody requestBody, IProgressListener progressListener) {
        return new ProgressRequestBody(requestBody, progressListener);
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink));
        }
        mRequestBody.writeTo(mBufferedSink);
        mBufferedSink.flush();

    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long mTotalBytesWrite = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                mTotalBytesWrite += byteCount;
                long curContentLength = mRequestBody.contentLength();
                if (mProgressListener != null) {
                    mProgressListener.update(mTotalBytesWrite,
                            curContentLength,
                            mTotalBytesWrite == curContentLength);
                }
            }
        };
    }

    public Request getOriginalRequest() {
        return mOriginalRequest;
    }

    public void setOriginalRequest(Request originalRequest) {
        mOriginalRequest = originalRequest;
    }

    public Request getWrappedRequest() {
        return mWrappedRequest;
    }

    public void setWrappedRequest(Request wrappedRequest) {
        mWrappedRequest = wrappedRequest;
    }
}