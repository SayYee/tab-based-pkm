package com.sayyi.software.tbp.cli.client.filecli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import lombok.SneakyThrows;
import picocli.CommandLine;

/**
 * open <id>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "open", description = "打开文件", mixinStandardHelpOptions = true)
public class OpenCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "资源id")
    private long id;

    @SneakyThrows
    @Override
    public void run() {
        PkmFunctionHolder.get().open(id);
        System.out.println("success");
    }
}
