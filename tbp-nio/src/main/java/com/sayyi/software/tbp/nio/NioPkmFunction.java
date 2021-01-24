package com.sayyi.software.tbp.nio;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.facade.AbstractPkmFunction;
import com.sayyi.software.tbp.nio.client.TbpClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于NIO传输数据的pkm function实现
 * @author SayYi
 */
@Slf4j
public class NioPkmFunction extends AbstractPkmFunction {

    private final TbpClient client;

    public NioPkmFunction(TbpClient tbpClient) {
        this.client = tbpClient;
    }

    @Override
    protected Response process(Request request) {
        return client.postRequest(request);
    }
}
