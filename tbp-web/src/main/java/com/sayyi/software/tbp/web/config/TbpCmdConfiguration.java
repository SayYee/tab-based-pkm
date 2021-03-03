package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.cli.CmdExecutor;
import com.sayyi.software.tbp.cli.PkmCommand;
import com.sayyi.software.tbp.cli.decorator.CmdPkmFunction;
import com.sayyi.software.tbp.cli.decorator.ResultHolder;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;

/**
 * cli组件配置
 * @author SayYi
 */
@Configuration
public class TbpCmdConfiguration {

    /**
     * 命令调用工具
     * @param pkmFunction
     * @return
     */
    @Bean
    public CmdExecutor cmdExecutor(PkmFunction pkmFunction) {
        // 使用resultHolder暂存处理结果
        return new CmdExecutor(pkmFunction);
    }
}
