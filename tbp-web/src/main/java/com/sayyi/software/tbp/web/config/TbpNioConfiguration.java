package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.core.facade.PkmFunction;
import com.sayyi.software.tbp.nio.NioPkmFunction;
import com.sayyi.software.tbp.nio.client.TbpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 基于NIO的方式调用pkm方法
 * @author SayYi
 */
@Configuration
@ConditionalOnProperty(name = "tbp.nio.enable", havingValue = "true")
public class TbpNioConfiguration {

    @Bean
    public PkmFunction pkmFunction(TbpClient tbpClient) {
        return new NioPkmFunction(tbpClient);
    }

    @Value("${tbp.nio.port}")
    private int port;

    @Bean
    public TbpClient tbpClient() throws IOException {
        TbpClient tbpClient = new TbpClient(port);
        tbpClient.start();
        return tbpClient;
    }

}
