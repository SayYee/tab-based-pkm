package com.sayyi.software.tbp.cli.client.tagcli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.common.model.TagInfo;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ls <tags>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "ls", description = "查询标签集合关联标签", mixinStandardHelpOptions = true)
public class LsCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "标签集合，点号分割")
    private String tags;


    @SneakyThrows
    @Override
    public void run() {
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final List<TagInfo> tagInfos = PkmFunctionHolder.get().listTags(tagSet);
        for (TagInfo tagInfo : tagInfos) {
            System.out.print(FormatUtil.format(tagInfo));
        }
    }
}
