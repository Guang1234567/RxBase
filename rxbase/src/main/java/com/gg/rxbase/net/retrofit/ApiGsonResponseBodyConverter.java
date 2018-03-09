package com.gg.rxbase.net.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author Guang1234567
 * @date 2017/7/6 17:54
 */
final class ApiGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    ApiGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            ApiResult result = gson.fromJson(gson.newJsonReader(value.charStream()), ApiResult.class);
            if (!result.isOk()) {
                throw new ApiException(result.getErrorCode(), result.getMsg());
            }
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
