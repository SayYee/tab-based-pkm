package com.sayyi.software.tbp.cli.client.filecli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import lombok.SneakyThrows;
import picocli.CommandLine;

/**
 * rm <id>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "rm", description = "移除资源", mixinStandardHelpOptions = true)
public class RmCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "资源id")
    private long id;

    @SneakyThrows
    @Override
    public void run() {
        PkmFunctionHolder.get().delete(id);
        System.out.println("success");
    }
}
