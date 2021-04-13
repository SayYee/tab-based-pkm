package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.core.*;
import com.sayyi.software.tbp.core.facade.DefaultPkmFunction;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 直接通过程序调用的方式，调用pkm方法
 * @author SayYi
 */
@Configuration
@ConditionalOnProperty(name = "tbp.nio.enable", havingValue = "false", matchIfMissing = true)
public class TbpDefaultConfiguration {

    @Value("${tbp.snap-dir}")
    private String snapDir;

    @Value("${tbp.store-dir}")
    private String storeDir;

    @Value("${tbp.tree-retain-num}")
    private int treeRetainNum;

    @Bean
    public PkmFunction pkmFunction() {
        FileManager fileManager = new FileManager(storeDir);
        MetadataFunction metadataFunction = new MetadataManager();
        TagTreeFunction tagTreeManager = new TagTreeManager(snapDir, treeRetainNum);
        DbFunction dbFunction = new FileBasedDbManager(snapDir);
        PkmService pkmService = new PkmServiceImpl(fileManager, metadataFunction, tagTreeManager, dbFunction);
        return new DefaultPkmFunction(pkmService);
    }
}
