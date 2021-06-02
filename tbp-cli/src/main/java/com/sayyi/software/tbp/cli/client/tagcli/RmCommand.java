package com.sayyi.software.tbp.cli.client.tagcli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * rm <tags>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "rm", description = "移除标签", mixinStandardHelpOptions = true)
public class RmCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "标签集合，点号分割")
    private String tags;

    @SneakyThrows
    @Override
    public void run() {
        for (String tag : tags.split("\\.")) {
            PkmFunctionHolder.get().deleteTag(tag);
        }
        System.out.println("success");
    }
}
