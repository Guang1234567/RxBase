package com.gg.rxbase.net.retrofit;

/**
 * 响应数据映射实体数据模型.
 * http://blog.csdn.net/dd864140130/article/details/52689010
 *
 * @author Guang1234567
 * @date 2017/7/6 17:32
 */
/* 对应下面的格式:
{
  "code": 700,
  "msg": "请求成功",
  "data": {
    "id": 1,
    "account": "121313",
    "accountName": "alipay",
    "income": "600.000000",
    ...
  }
}

or

{
  "code": -700,
  "msg": "请求失败",
  "error": {
    "id": 1,
    "account": "121313",
    "accountName": "alipay",
    "income": "600.000000",
    ...
  }
}
*/
public final class ApiResult<DATA, ERROR> {
    private int code;
    private String msg;
    private DATA data;
    private ERROR error;

    public boolean isOk() {
        ApiCode code = getApiCode();
        return ApiCode.SUCCESS.equals(code);
    }

    public ApiCode getApiCode() {
        return ApiCode.valueOf(code);
    }

    public String getMsg() {
        return msg;
    }

    public DATA getData() {
        return data;
    }

    public ERROR getError() {
        return error;
    }

    public static <D, E> ApiResult<D, E> ok(int code, String msg, D data) {
        ApiResult<D, E> r = new ApiResult<>();
        r.code = code;
        r.msg = msg;
        r.data = data;
        return r;
    }

    public static <D, E> ApiResult<D, E> fail(int code, String msg, E error) {
        ApiResult<D, E> r = new ApiResult<>();
        r.code = code;
        r.msg = msg;
        r.error = error;
        return r;
    }
}
