package com.shuidun;

import com.google.gson.Gson;

/**
 * JavaBean，转化为json字符串作为响应体
 */
public class RespBean {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String msg;

    public RespBean(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespBean(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 转化为json字符串
     */
    public String toJson() {
        return new Gson().toJson(this);
    }
}