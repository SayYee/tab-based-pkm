package com.sayyi.software.tbp.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author SayYi
 */
@SpringBootApplication
public class TbpApplication {

    public static void main(String... args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(TbpApplication.class);
        // 使用这个，避免打开本地文件时，报 java.awt.HeadlessException 异常
        builder.headless(false).run(args);
    }
}
