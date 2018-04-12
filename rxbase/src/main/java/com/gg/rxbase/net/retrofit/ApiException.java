package com.gg.rxbase.net.retrofit;

/**
 * @author Guang1234567
 * @date 2017/7/6 17:37
 */

public class ApiException extends RuntimeException {
    private ApiCode mErrorCode;

    public ApiException(ApiCode code, String msg) {
        super(msg);
        this.mErrorCode = code;
    }

    public ApiException(ApiCode code, String msg, Throwable cause) {
        super(msg, cause);
        mErrorCode = code;
    }

    public ApiCode getErrorCode() {
        return mErrorCode;
    }

}
