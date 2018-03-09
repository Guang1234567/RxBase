package com.gg.rxbase.net.okhttp.progress.listener.impl;

import android.os.Looper;
import android.os.Message;

import com.gg.rxbase.gc.WeakReferenceHandler;
import com.gg.rxbase.log.Log;
import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import java.io.Serializable;

public abstract class UIProgressListener implements IProgressListener {
    public final static String LOG_TAG = "UIProgressListener";
    private boolean mIsFirst = false;

    private final static class UiHandler extends WeakReferenceHandler<UIProgressListener> {

        public UiHandler(UIProgressListener progressListener) {
            super(progressListener, Looper.getMainLooper());
        }

        @Override
        protected void handleMessage(UIProgressListener progressListener, Message msg) {
            ProgressModel progressModel = (ProgressModel) msg.obj;
            switch (msg.what) {
                case MSG_START: {
                    progressListener.onStart(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    break;
                }
                case MSG_UPDATE: {
                    progressListener.onProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    break;
                }
                case MSG_FINISH: {
                    progressListener.onFinish(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    break;
                }
                default: {
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

    private static class ProgressModel implements Serializable {
        //当前读取字节长度
        private long currentBytes;
        //总字节长度
        private long contentLength;
        //是否读取完成
        private boolean done;

        public ProgressModel(long currentBytes, long contentLength, boolean done) {
            this.currentBytes = currentBytes;
            this.contentLength = contentLength;
            this.done = done;
        }

        public long getCurrentBytes() {
            return currentBytes;
        }

        public void setCurrentBytes(long currentBytes) {
            this.currentBytes = currentBytes;
        }

        public long getContentLength() {
            return contentLength;
        }

        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(boolean done) {
            this.done = done;
        }

        @Override
        public String toString() {
            return "ProgressModel{" +
                    "currentBytes=" + currentBytes +
                    ", contentLength=" + contentLength +
                    ", done=" + done +
                    '}';
        }
    }

    public static final int MSG_START = 0x07;
    public static final int MSG_UPDATE = 0x08;
    public static final int MSG_FINISH = 0x09;

    private UiHandler mUiHandler;

    public UIProgressListener() {
        mUiHandler = new UiHandler(this);
    }

    @Override
    public final void update(long bytesReadOrWrite,
                             long contentLength,
                             boolean done) {

        if (contentLength < -1) {
            Log.e(LOG_TAG, "Illegal contentLength: " + String.valueOf(contentLength));
        } else if (contentLength == -1) {
            Log.w(LOG_TAG, "Unknown contentLength: " + String.valueOf(contentLength));
        } else if (contentLength == 0) {
            Log.e(LOG_TAG, "Unknown error, eg: 'Request Redirects(state-code: 302)'" + String.valueOf(contentLength));
        } else {
            if (!mIsFirst) {
                mIsFirst = true;
                Message startMsg = Message.obtain(mUiHandler, MSG_START, new ProgressModel(bytesReadOrWrite, contentLength, done));
                startMsg.sendToTarget();
            }

            Message updateMsg = Message.obtain(mUiHandler, MSG_UPDATE, new ProgressModel(bytesReadOrWrite, contentLength, done));
            updateMsg.sendToTarget();

            if (done) {
                Message finishMsg = Message.obtain(mUiHandler, MSG_FINISH, new ProgressModel(bytesReadOrWrite, contentLength, done));
                finishMsg.sendToTarget();
            }
        }
    }

    public abstract void onStart(long currentBytes, long contentLength, boolean done);

    public abstract void onProgress(long currentBytes, long contentLength, boolean done);

    public abstract void onFinish(long currentBytes, long contentLength, boolean done);
}
