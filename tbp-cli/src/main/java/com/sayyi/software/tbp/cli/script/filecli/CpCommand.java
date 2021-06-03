package com.sayyi.software.tbp.cli.script.filecli;

import com.sayyi.software.tbp.cli.script.PkmFunctionHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * cp <path> <tags>
 * @author xuchuang
 * @date 2021/6/1
 */
@Slf4j
@CommandLine.Command(name = "cp", description = "复制本地文件", mixinStandardHelpOptions = true)
public class CpCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "文件路径")
    private String path;
    @CommandLine.Parameters(index = "1", description = "标签集合，点号分割")
    private String tags;


    @SneakyThrows
    @Override
    public void run() {
        String basedir = System.getProperty("basedir");
        File file;
        if (!isAbsolutePath(path)) {
            file = new File(basedir, path);
        } else {
            file = new File(path);
        }
        final Set<String> tagSet = Arrays.stream(tags.split("\\.")).collect(Collectors.toSet());
        final FileMetadata fileMetadata = PkmFunctionHolder.get().copy(file.getAbsolutePath(), tagSet);
        System.out.print(FormatUtil.format(fileMetadata));
    }

    private boolean isAbsolutePath(String path) {
        return path.startsWith("/")
                || path.contains(":");
    }
}
