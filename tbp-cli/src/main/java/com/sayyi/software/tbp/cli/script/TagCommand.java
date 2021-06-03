package com.sayyi.software.tbp.cli.script;

import com.sayyi.software.tbp.cli.script.tagcli.LsCommand;
import com.sayyi.software.tbp.cli.script.tagcli.MvCommand;
import com.sayyi.software.tbp.cli.script.tagcli.RmCommand;
import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import com.sayyi.software.tbp.nio.NioPkmFunction;
import com.sayyi.software.tbp.nio.client.TbpClient;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Objects;

/**
 * 标签操作命令
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "tbp-tag", description = "TBP系统标签操作命令", mixinStandardHelpOptions = true, subcommands = {
        LsCommand.class,
        MvCommand.class,
        RmCommand.class
})
public class TagCommand {

    public static void main(String[] args) throws IOException {
        // 解析配置文件
        TbpConfig tbpConfig = new TbpConfigParse(
                Objects.requireNonNull(TagCommand.class.getClassLoader().getResource("tbp.cfg")).getPath());
        TbpConfigHolder.set(tbpConfig);
        // 启动 nio 客户端
        TbpClient tbpClient = new TbpClient(tbpConfig.getPort());
        tbpClient.start();
        // 创建 pkm function 实例
        PkmFunction pkmFunction = new NioPkmFunction(tbpClient);
        PkmFunctionHolder.set(pkmFunction);
        // 执行命令
        int exitCode = new CommandLine(new TagCommand()).execute(args);
        // 关闭 nio 连接
        tbpClient.shutdown();
        System.exit(exitCode);
    }
}
