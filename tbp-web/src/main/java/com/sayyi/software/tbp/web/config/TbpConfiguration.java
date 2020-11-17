package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SayYi
 */
@Configuration
public class TbpConfiguration {

    @Bean
    public PkmFunction pkmFunction(FileManager fileManager, MetadataFunction metadataFunction, DbFunction dbFunction) {
        return new PkmMain(fileManager, metadataFunction, dbFunction);
    }

    /**
     * 这个，也可以根据需要，替换成基于数据库或者其他持久化组件实现
     * @param tbpProperties
     * @return
     */
    @Bean
    public DbFunction dbFunction(TbpProperties tbpProperties) {
        return new FileBasedDbManager(tbpProperties.getSnapDir());
    }

    @Bean
    public MetadataFunction metadataFunction() {
        return new MetadataManager();
    }

    @Bean
    public FileManager fileManager(TbpProperties tbpProperties) {
        return new FileManager(tbpProperties.getStoreDir());
    }

    @Bean
    public TbpConfig tbpConfig(TbpProperties tbpProperties) {
        return new TbpConfigImpl(tbpProperties);
    }

    private static class TbpConfigImpl implements TbpConfig {
        TbpProperties tbpProperties;
        TbpConfigImpl(TbpProperties tbpProperties) {
            this.tbpProperties = tbpProperties;
        }

        @Override
        public String getStoreDir() {
            return tbpProperties.getStoreDir();
        }

        @Override
        public String getSnapDir() {
            return tbpProperties.getSnapDir();
        }
    }
}
