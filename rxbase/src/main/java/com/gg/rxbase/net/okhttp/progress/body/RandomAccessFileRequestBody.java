package com.gg.rxbase.net.okhttp.progress.body;

import com.gg.rxbase.log.Log;
import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;
import com.joanzapata.utils.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * 从File特定的偏移值(offsetByteCount)开始, 上传File.
 * <p/>
 * RandomAccessFileRequestBody is a new library that complements {@link RequestBody#create(MediaType, File)}
 */
public class RandomAccessFileRequestBody extends RequestBody {
    public final static String LOG_TAG = "RAFileRequestBody";

    private File mFile;
    private MediaType mContentType;
    private long mOffset;

    private Request mWrappedRequest;
    private Request mOriginalRequest;
    private IProgressListener mProgressListener;
    private BufferedSink mBufferedSink;

    public RandomAccessFileRequestBody(MediaType contentType,
                                       File file,
                                       final long offset,
                                       IProgressListener progressListener) {
        super();
        if (file == null) throw new NullPointerException("file == null");
        if (!file.exists()) throw new IllegalArgumentException("file not exists");
        mFile = file;
        mContentType = contentType != null ? contentType : MediaType.parse("application/octet-stream");
        mProgressListener = progressListener;

        long newOffset = getOffsetSafe(file.length(), offset); //occur IOException
        if (newOffset != offset) {
            Log.d(LOG_TAG, "dest offset beyond eof, so clamp to newOffset: " + newOffset);
        }
        mOffset = newOffset;
    }

    public static RandomAccessFileRequestBody create(MediaType contentType, File file, final long offset, IProgressListener progressListener) {
        return new RandomAccessFileRequestBody(contentType, file, offset, progressListener);
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length() - mOffset;
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Log.d(LOG_TAG,
                Strings.format("Start upload from offset({offset}), dest total upload byte({contentLength})")
                        .with("offset", mOffset)
                        .with("contentLength", contentLength())
                        .build());
        long totalBytesUpload = 0L;
        if (mOffset > 0) {
            totalBytesUpload = writeOffsetSrcFile(sink, mFile, mOffset);
        } else {
            totalBytesUpload = writeAllSrcFile(sink, mFile);
        }
        if (totalBytesUpload < contentLength()) {
            Log.d(LOG_TAG, "Finish upload a part of file!  uploaded bytes: " + totalBytesUpload);
        } else {
            Log.d(LOG_TAG, "Finish upload whole file!: " + totalBytesUpload);
        }
    }

    /**
     * 返回已上传的字节数.
     * 出现异常时, 返回 zero.
     */
    private long writeOffsetSrcFile(BufferedSink sink, File srcFile, long offset) throws IOException {
        long totalBytesRead = 0L;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(srcFile, "r"); //occur FileNotFoundException
            long newOffset = seekSafe(randomAccessFile, offset); //occur IOException
            if (newOffset != offset) {
                Log.e(LOG_TAG, "dest offset beyond eof, so clamp to newOffset: " + newOffset);
            }

            if (mBufferedSink == null) {
                mBufferedSink = Okio.buffer(new OffsetForwardingSink(sink));
            }

            // 追加到目标文件中
            totalBytesRead = writeRAFile(mBufferedSink, randomAccessFile);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "srcFile not exists: " + srcFile.getAbsolutePath(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException in writeOffsetSrcFile(...)", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown Exception in writeOffsetSrcFile(...)", e);
        } finally {
            if (mBufferedSink != null) {
                mBufferedSink.flush();
            }
            Util.closeQuietly(randomAccessFile);
        }

        return totalBytesRead;
    }

    public static long writeRAFile(BufferedSink sink, RandomAccessFile randomAccessFile) throws IOException {
        if (sink == null) throw new IllegalArgumentException("sink == null");
        if (randomAccessFile == null)
            throw new IllegalArgumentException("randomAccessFile == null");

        long totalBytesRead = 0L;
        byte[] cacheBuf = new byte[1024];
        for (int readCount; (readCount = randomAccessFile.read(cacheBuf, 0, 1024)) != -1; ) { //occur IOException
            sink.write(cacheBuf, 0, readCount); //occur IOException
            totalBytesRead += readCount;
        }
        return totalBytesRead;
    }

    public static long seekSafe(RandomAccessFile randomAccessFile, long offset) throws IOException {
        long newOffset = 0;
        if (offset > 0) {
            long eof = randomAccessFile.length();
            newOffset = offset > eof ? eof : offset;
            randomAccessFile.seek(newOffset);
        }
        return newOffset;
    }

    private static long getOffsetSafe(long contentLength, long offset) {
        long newOffset = 0;
        if (offset > 0) {
            long eof = contentLength;
            newOffset = offset > eof ? eof : offset;
        }
        return newOffset;
    }

    /**
     * 返回已上传的字节数.
     * 出现异常时, 返回 zero.
     */
    private long writeAllSrcFile(BufferedSink sink, File srcFile) throws IOException {
        long totalBytesRead = 0L;
        Source source = null;
        try {
            if (mBufferedSink == null) {
                mBufferedSink = Okio.buffer(new OffsetForwardingSink(sink));
            }
            source = Okio.source(srcFile);
            totalBytesRead = mBufferedSink.writeAll(source);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException in writeAllSrcFile(...)", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unknown Exception in writeAllSrcFile(...)", e);
        } finally {
            if (mBufferedSink != null) {
                mBufferedSink.flush();
            }
            Util.closeQuietly(source);
        }
        return totalBytesRead;
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

    private class OffsetForwardingSink extends ForwardingSink {
        private long mTotalBytesWrite;

        public OffsetForwardingSink(Sink delegate) {
            super(delegate);
            mTotalBytesWrite = 0L;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            mTotalBytesWrite += byteCount;
            long curContentLength = contentLength();
            if (mProgressListener != null) {
                mProgressListener.update(mTotalBytesWrite,
                        curContentLength,
                        mTotalBytesWrite == curContentLength);
            }
        }
    }
}
