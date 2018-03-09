package com.gg.rxbase.net.retrofit;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Guang1234567
 * @date 2017/7/6 19:41
 */

public class ApiFactory {

    private static ApiThrowableHandler sApiThrowableHandler = ApiThrowableHandler.DEFAULT;

    private ApiFactory() {
    }

    public static Retrofit.Builder newBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    public static void setApiThrowableHandler(ApiThrowableHandler apiThrowableHandler) {
        if (apiThrowableHandler != null) {
            sApiThrowableHandler = apiThrowableHandler;
        }
    }

    public static ApiThrowableHandler getApiThrowableHandler() {
        return sApiThrowableHandler;
    }

    public static UserApi getUserApi() {
        return newBuilder().baseUrl("https://www.xxoo.com").build().create(UserApi.class);
    }

    public interface UserApi {
        class MessageList {
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("MessageListError{")
                        .append("我是一个代表\"成功\"的结果")
                        .append('}');
                return sb.toString();
            }
        }

        class MessageListError {
            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("MessageListError{")
                        .append("我是一个代表\"错误\"的结果")
                .append('}');
                return sb.toString();
            }
        }

        @GET("/user-api/message/list")
        Observable<ApiResult<MessageList, MessageListError>> getMessageList(@Query("start") int start, @Query("length") int length);
    }
}
