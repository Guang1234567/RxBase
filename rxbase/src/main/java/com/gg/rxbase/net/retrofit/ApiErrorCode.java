package com.gg.rxbase.net.retrofit;

/**
 * http://blog.csdn.net/dd864140130/article/details/52689010
 *
 * @author Guang1234567
 * @date 2017/7/6 17:38
 */

public enum ApiErrorCode {

    ERROR_CLIENT_AUTHORIZED(701, "客户端错误"),

    ERROR_USER_AUTHORIZED(702, "用户授权失败"),

    ERROR_REQUEST_PARAM(703, "请求参数错误"),

    ERROR_PARAM_CHECK(704, "参数检验不通过"),

    ERROR_OTHER(705, "自定义错误"),

    ERROR_NO_INTERNET(706, "无网络连接"),

    ERROR_UNKNOWN(Integer.MIN_VALUE, "未知业务错误");

    int mCode;
    String mDes;

    ApiErrorCode(int code, String des) {
        mCode = code;
        mDes = des;
    }

    public int code() {
        return mCode;
    }

    public String description() {
        return mDes;
    }

    public static ApiErrorCode valueOf(int code) {
        for (ApiErrorCode ec : ApiErrorCode.values()) {
            if (ec.code() == code) {
                return ec;
            }
        }
        return ERROR_UNKNOWN;
    }
}
