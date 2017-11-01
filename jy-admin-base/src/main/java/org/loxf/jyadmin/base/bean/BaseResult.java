package org.loxf.jyadmin.base.bean;

import java.io.Serializable;

public class BaseResult<T> implements Serializable {
    protected int code;
    protected String msg;
    protected T data;

    public BaseResult() {
        this.code = 1;
        this.msg = "操作成功!";
    }

    public BaseResult(T data) {
        this.code = 1;
        this.msg = "操作成功!";
        this.data = data;
    }

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}