package com.example.qiwei.livedatabus;

/**
 * 类名称：
 * 类功能：
 * 类作者：qiwei
 * 类日期：2019/6/20.
 **/
public class LiveDataBean<T> {
    private String code;
    private T data;

    public LiveDataBean(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
