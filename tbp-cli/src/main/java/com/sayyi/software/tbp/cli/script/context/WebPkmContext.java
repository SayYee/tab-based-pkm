package com.sayyi.software.tbp.cli.script.context;

import com.sayyi.software.tbp.cli.script.FileCommand;
import com.sayyi.software.tbp.cli.script.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.script.TbpConfigHolder;
import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.web.api.FeignPkmFunction;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.spring.SpringContract;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class WebPkmContext extends PkmContext{
    @Override
    public void init() throws IOException {
        // 解析配置文件
        TbpConfig tbpConfig = new TbpConfigParse(
                Objects.requireNonNull(FileCommand.class.getClassLoader().getResource("application.yml")).getPath());
        TbpConfigHolder.set(tbpConfig);
        // 创建 pkm function 实例
        FeignPkmFunction pkmFunction = Feign.builder()
                .contract(new SpringContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(FeignPkmFunction.class, "http://localhost:" + tbpConfig.getPort());
        PkmFunctionHolder.set(pkmFunction);
    }

    @Override
    public void clear() {

    }
}
