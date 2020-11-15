package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.core.PkmFunction;
import com.sayyi.software.tbp.core.PkmMain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SayYi
 */
@Configuration
public class TbpConfiguration {

    @Bean
    public PkmFunction pkmFunction(TbpConfig tbpConfig) {
        return new PkmMain(tbpConfig);
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
