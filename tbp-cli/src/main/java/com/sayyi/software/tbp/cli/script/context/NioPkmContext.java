package com.sayyi.software.tbp.cli.script.context;

import com.sayyi.software.tbp.cli.script.FileCommand;
import com.sayyi.software.tbp.cli.script.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.script.TbpConfigHolder;
import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import com.sayyi.software.tbp.nio.NioPkmFunction;
import com.sayyi.software.tbp.nio.client.TbpClient;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class NioPkmContext extends PkmContext{

    TbpClient tbpClient;

    @Override
    public void init() throws IOException {
        // 解析配置文件
        TbpConfig tbpConfig = new TbpConfigParse(
                Objects.requireNonNull(FileCommand.class.getClassLoader().getResource("tbp.cfg")).getPath());
        TbpConfigHolder.set(tbpConfig);
        // 启动nio client
        tbpClient = new TbpClient(tbpConfig.getPort());
        tbpClient.start();
        // 创建 pkm function 实例
        PkmFunction pkmFunction = new NioPkmFunction(tbpClient);
        PkmFunctionHolder.set(pkmFunction);
    }

    @Override
    public void clear() {
        tbpClient.shutdown();
    }
}
