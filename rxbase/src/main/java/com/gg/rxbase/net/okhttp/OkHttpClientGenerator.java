package com.gg.rxbase.net.okhttp;

import android.content.Context;
import android.util.Log;

import com.gg.rxbase.net.okhttp.interceptor.CacheInterceptor;
import com.gg.rxbase.net.okhttp.interceptor.CommonParamsInterceptor;
import com.gg.rxbase.net.okhttp.interceptor.HttpLoggingInterceptor;
import com.gg.rxbase.net.okhttp.interceptor.ProgressRequestInterceptor;
import com.gg.rxbase.net.okhttp.interceptor.ProgressResponseInterceptor;
import com.gg.rxbase.net.okhttp.progress.listener.IProgressListener;

import java.io.File;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * @author Guang1234567
 * @date 2017/3/17 17:19:39
 */

public class OkHttpClientGenerator {
    public final static String TAG = "OkHttpClientGenerator";

    private static volatile OkHttpClientGenerator sInst;

    private File mCacheDir;
    private final HttpLoggingInterceptor.Level DEFAULT_LOG_LEVEL = HttpLoggingInterceptor.Level.HEADERS;

    private OkHttpClient mSrcClient; // 通用配置
    private OkHttpClient mSrcHttpsClient; // 使用 SSL 协议的 http client

    private OkHttpClientGenerator(Context context, Map<String, String> headsMap) {
        context = context.getApplicationContext();
        if (headsMap == null) {
            headsMap = new HashMap<>();
        }

        mCacheDir = new File(context.getExternalCacheDir(), "okhttp_cache");
        int cacheSize = 100 * 1024 * 1024; // 100 MiB
        Cache cache = new Cache(mCacheDir, cacheSize);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                //HttpLoggingInterceptor.Logger.DEFAULT.log(message);

                // Split by line, then ensure each line can fit into Log's maximum length.
                for (int i = 0, length = message.length(); i < length; i++) {
                    int newline = message.indexOf('\n', i);
                    newline = newline != -1 ? newline : length;
                    do {
                        int end = Math.min(newline, i + HttpLoggingInterceptor.MAX_LOG_LENGTH);
                        Log.d(TAG, message.substring(i, end));
                        i = end;
                    } while (i < newline);
                }
            }
        });
        logging.setLevel(DEFAULT_LOG_LEVEL);

        Interceptor cacheInterceptor = new CacheInterceptor(context);

        mSrcClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(cache) // set external cache dir
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(new CommonParamsInterceptor(headsMap))
                .addNetworkInterceptor(cacheInterceptor)
                .addNetworkInterceptor(logging) // add logging as last interceptor
                .build();

        mSrcHttpsClient = createHttpsClient(mSrcClient);
    }

    private OkHttpClient createHttpsClient(OkHttpClient from) {
        // 创建使用 SSL 协议的 http client.
        X509TrustManager trustManager = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance(org.apache.http.conn.ssl.SSLSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            Log.e(TAG, "#cloneOkHttpsClient : ", e);
        }


        return from.newBuilder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .build();
    }

    /**
     * 初始化
     */
    public static void install(Context context, Map<String, String> headsMap) {
        sInst = new OkHttpClientGenerator(context.getApplicationContext(), headsMap);
    }

    /**
     * 卸载
     *
     * @param context
     */
    public static void uninstall(Context context) {
        //sInst = null;
    }

    /**
     * 获取全局的有缓存的 OkHttpClient
     */
    public static OkHttpClient getCacheOkHttpClient() {
        return sInst.mSrcClient;
    }

    /**
     * 获取全局的有缓存的支持 HTTPS 的 OkHttpClient
     */
    public static OkHttpClient getCacheOkHttpsClient() {
        return sInst.mSrcHttpsClient;
    }

    /**
     * 创建"可监听上传进度"的 OkHttpsClient
     *
     * @param progressListener
     * @return
     */
    public static OkHttpClient createProgressRequestHttpsClient(IProgressListener progressListener) {
        return getCacheOkHttpsClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .addNetworkInterceptor(new ProgressRequestInterceptor(progressListener))
                .build();
    }

    /**
     * 创建"可监听上传进度"的 OkHttpClient
     *
     * @param progressListener
     * @return
     */
    public static OkHttpClient createProgressRequestHttpClient(IProgressListener progressListener) {
        return getCacheOkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .addNetworkInterceptor(new ProgressRequestInterceptor(progressListener))
                .build();
    }

    /**
     * 创建"可监听下载进度"的 OkHttpsClient
     *
     * @param progressListener
     * @return
     */
    public static OkHttpClient createProgressResponseHttpsClient(IProgressListener progressListener) {
        return getCacheOkHttpsClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .addNetworkInterceptor(new ProgressResponseInterceptor(progressListener))
                .build();
    }

    /**
     * 创建"可监听下载进度"的 OkHttpClient
     *
     * @param progressListener
     * @return
     */
    public static OkHttpClient createProgressResponseHttpClient(IProgressListener progressListener) {
        return getCacheOkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .addNetworkInterceptor(new ProgressResponseInterceptor(progressListener))
                .build();
    }
}
