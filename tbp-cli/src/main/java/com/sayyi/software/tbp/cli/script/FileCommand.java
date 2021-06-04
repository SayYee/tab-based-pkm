package com.sayyi.software.tbp.cli.script;

import com.sayyi.software.tbp.cli.script.context.PkmContext;
import com.sayyi.software.tbp.cli.script.context.WebPkmContext;
import com.sayyi.software.tbp.cli.script.filecli.*;
import picocli.CommandLine;

import java.io.IOException;

/**
 * 文件操作相关的命令
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "tbp-file", description = "TBP系统文件操作命令", mixinStandardHelpOptions = true, subcommands = {
        UrlCommand.class,
        TouchCommand.class,
        CpCommand.class,
        RmCommand.class,
        MvCommand.class,
        LsCommand.class,
        OpenCommand.class
})
public class FileCommand {

    public static void main(String[] args) throws IOException {
        PkmContext pkmContext = new WebPkmContext();
        pkmContext.init();
        // 执行命令
        int exitCode = new CommandLine(new FileCommand()).execute(args);
        pkmContext.clear();
        System.exit(exitCode);
    }
}
