package com.sayyi.software.tbp.core.facade;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.PkmService;

/**
 * 默认的pkm功能实现。直接调用，不走tcp
 * @author SayYi
 */
public class DefaultPkmFunction extends AbstractPkmFunction {

    private final PkmService pkmService;

    public DefaultPkmFunction(PkmService pkmService) {
        this.pkmService = pkmService;
    }

    @Override
    protected Response process(Request request) {
        Response response = new Response();
        pkmService.deal(request, response);
        return response;
    }
}
