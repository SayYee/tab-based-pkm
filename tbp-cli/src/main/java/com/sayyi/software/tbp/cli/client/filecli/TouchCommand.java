package com.sayyi.software.tbp.cli.client.filecli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * touch <name> <tags>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "touch", description = "创建文件", mixinStandardHelpOptions = true)
public class TouchCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "名称。需要携带名称后缀")
    private String name;
    @CommandLine.Parameters(index = "1", description = "标签集合，点号分割")
    private String tags;

    @SneakyThrows
    @Override
    public void run() {
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final FileMetadata fileMetadata = PkmFunctionHolder.get().create(name, tagSet);
        System.out.print(FormatUtil.format(fileMetadata));
    }
}
