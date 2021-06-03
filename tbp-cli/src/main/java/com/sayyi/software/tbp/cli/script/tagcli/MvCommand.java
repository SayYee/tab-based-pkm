package com.sayyi.software.tbp.cli.script.tagcli;

import com.sayyi.software.tbp.cli.script.PkmFunctionHolder;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * mv <tags> <newtags>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "mv", description = "修改标签", mixinStandardHelpOptions = true)
public class MvCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "标签集合，点号分割")
    private String tags;

    @CommandLine.Parameters(index = "1", description = "新标签集合，点号分割")
    private String newtags;

    @SneakyThrows
    @Override
    public void run() {
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final Set<String> newtagSet = Arrays.stream(newtags.split("\\.")).collect(Collectors.toSet());
        PkmFunctionHolder.get().batchModifyTags(tagSet, newtagSet);
        System.out.println("success");
    }
}
