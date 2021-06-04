package com.sayyi.software.tbp.cli.script;

import com.sayyi.software.tbp.cli.script.context.PkmContext;
import com.sayyi.software.tbp.cli.script.context.WebPkmContext;
import com.sayyi.software.tbp.cli.script.tagcli.LsCommand;
import com.sayyi.software.tbp.cli.script.tagcli.MvCommand;
import com.sayyi.software.tbp.cli.script.tagcli.RmCommand;
import picocli.CommandLine;

import java.io.IOException;

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
        PkmContext pkmContext = new WebPkmContext();
        pkmContext.init();
        // 执行命令
        int exitCode = new CommandLine(new TagCommand()).execute(args);
        pkmContext.clear();
        System.exit(exitCode);
    }
}
