package org.loxf.jyadmin.base.bean;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
    protected int code;
    protected String msg;
    protected List<T> data;
    protected int totalPage;
    protected int currentPage;
    protected int total;

    public PageResult() {
        this.code = 1;
        this.msg = "操作成功!";
    }

    public PageResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public PageResult(int totalPage, int currentPage, int total, List<T> data) {
        this.code = 1;
        this.msg = "操作成功!";
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.total = total;
        this.data = data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}