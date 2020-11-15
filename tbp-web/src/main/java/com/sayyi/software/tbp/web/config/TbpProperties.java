package com.sayyi.software.tbp.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * tbp参数类
 * @author SayYi
 */
@Component
@ConfigurationProperties(TbpProperties.PREFIX)
@Data
public class TbpProperties {
    static final String PREFIX = "tbp";

    /**
     * 文件存储路径
     */
    private String storeDir;

    /**
     * 快照存储路径
     */
    private String snapDir;
}
