package com.sayyi.software.tbp.web.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author SayYi
 */
@Getter
public class ResultBean<T> implements Serializable {

    private int code;
    private String message;
    private T result;

    private static final int SUCCESS_CODE = 1;
    private static final int ERROR_CODE = -1;

    public static <B> ResultBean<B> ok(B result) {
        ResultBean<B> resultBean = new ResultBean<>();
        resultBean.code = SUCCESS_CODE;
        resultBean.result = result;
        return resultBean;
    }

    public static <B> ResultBean<B> error(String message) {
        ResultBean<B> resultBean = new ResultBean<>();
        resultBean.code = ERROR_CODE;
        resultBean.message = message;
        return resultBean;
    }
}
