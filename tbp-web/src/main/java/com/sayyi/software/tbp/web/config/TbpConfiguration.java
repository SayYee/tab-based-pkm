package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.nio.client.PkmFunction;
import com.sayyi.software.tbp.nio.client.PkmMain;
import com.sayyi.software.tbp.nio.client.TbpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author SayYi
 */
@Configuration
public class TbpConfiguration {

    @Bean
    public PkmFunction pkmFunction(TbpClient tbpClient) {
        return new PkmMain(tbpClient);
    }

    @Value("${tbp.port}")
    private int port;

    @Bean
    public TbpClient tbpClient() throws IOException {
        TbpClient tbpClient = new TbpClient(port);
        tbpClient.start();
        return tbpClient;
    }

}
