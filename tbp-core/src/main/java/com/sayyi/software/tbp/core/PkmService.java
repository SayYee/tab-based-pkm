package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;

/**
 * @author SayYi
 */
public interface PkmService {

    /**
     * 处理请求
     * @param request   请求
     * @param response  响应
     */
    void deal(Request request, Response response);
}
