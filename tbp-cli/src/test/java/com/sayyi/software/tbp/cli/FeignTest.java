package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.web.api.FeignPkmFunction;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.spring.SpringContract;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xuchuang
 * @date 2021/6/3
 */
public class FeignTest {

    @Test
    public void test() throws Exception {
        FeignPkmFunction pkmFunction = Feign.builder()
                .contract(new SpringContract())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(FeignPkmFunction.class, "http://localhost:8080");
        // feign会跳过为null的参数，不传递。RequestTemplate中会跳过对应的参数。解决版本就是为这些赋默认值
        System.out.println(pkmFunction.listByNameAndTag(Collections.singleton("test"), null));
    }
}
