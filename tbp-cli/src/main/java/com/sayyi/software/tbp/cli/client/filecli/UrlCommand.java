package com.sayyi.software.tbp.cli.client.filecli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.cli.util.PrintUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * url <url> <name> <tags>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "url", description = "保存url", mixinStandardHelpOptions = true)
public class UrlCommand implements Runnable{

    @CommandLine.Parameters(index = "0", description = "url地址")
    private String url;
    @CommandLine.Parameters(index = "1", description = "名称。不需要携带类型后缀")
    private String name;
    /**
     * 标签还是作为必需的参数吧。
     */
    @CommandLine.Parameters(index = "2", description = "标签集合，点号分割")
    private String tags;

    @SneakyThrows
    @Override
    public void run() {
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final FileMetadata fileMetadata = PkmFunctionHolder.get().url(name, this.url, tagSet);
        System.out.print(FormatUtil.format(fileMetadata));
    }
}
