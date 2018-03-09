package com.gg.rxbase.net.retrofit;

/**
 * http://blog.csdn.net/dd864140130/article/details/52689010
 *
 * @author Guang1234567
 * @date 2017/7/6 17:38
 */

public enum ApiOkCode {

    NORMAL(700, "请求有效, 响应正常"),

    NOT_OK(Integer.MAX_VALUE, "不成功");

    int mCode;
    String mDes;

    ApiOkCode(int code, String des) {
        mCode = code;
        mDes = des;
    }

    public int code() {
        return mCode;
    }

    public String description() {
        return mDes;
    }

    public static ApiOkCode valueOf(int code) {
        for (ApiOkCode ec : ApiOkCode.values()) {
            if (ec.code() == code) {
                return ec;
            }
        }
        return NOT_OK;
    }
}
