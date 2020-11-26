package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.flow.Response;

/**
 * @author SayYi
 */
public interface PkmService {

    /**
     * 处理请求
     * @param requestType   请求类型
     * @param data  请求数据
     * @return  响应
     */
    Response deal(int requestType, byte[] data);
}
