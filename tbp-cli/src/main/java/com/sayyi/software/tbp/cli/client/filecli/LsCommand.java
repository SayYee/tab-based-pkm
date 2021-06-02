package com.sayyi.software.tbp.cli.client.filecli;

import com.sayyi.software.tbp.cli.client.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * ls <[-i id] | [-f filereg] [-t tags]>
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "ls", description = "资源查询", mixinStandardHelpOptions = true)
public class LsCommand implements Runnable {

    @CommandLine.Option(names = {"-i", "--id"}, description = "文件id。指定id查询，其他参数不会生效")
    private Long id;

    @CommandLine.Option(names = {"-f", "--filereg"}, description = "文件名称正则表达式")
    private String filereg;
    @CommandLine.Option(names = {"-t", "--tags"}, description = "tag集合（点号分割）")
    private String tags;

    @SneakyThrows
    @Override
    public void run() {
        if (id != null) {
            final FileMetadata fileMetadata = PkmFunctionHolder.get().getFileById(id);
            System.out.print(FormatUtil.format(fileMetadata));
            return;
        }
        boolean isEmptyCondition = (filereg == null || "".equals(filereg))
                && (tags == null || "".equals(tags));
        if (isEmptyCondition) {
            System.out.println("success");
            return;
        }
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final List<FileMetadata> fileMetadataList = PkmFunctionHolder.get().listByNameAndTag(tagSet, filereg);
        for (FileMetadata fileMetadata : fileMetadataList) {
            System.out.print(FormatUtil.format(fileMetadata));
        }
    }
}
