package com.gg.rxbase.net.retrofit;

/**
 * @author Guang1234567
 * @date 2017/7/6 17:37
 */

public class ApiException extends RuntimeException {
    private ApiErrorCode mErrorCode;

    public ApiException(ApiErrorCode code, String msg) {
        super(msg);
        this.mErrorCode = code;
    }

    public ApiException(ApiErrorCode code, String msg, Throwable cause) {
        super(msg, cause);
        mErrorCode = code;
    }

    public ApiErrorCode getErrorCode() {
        return mErrorCode;
    }

}
