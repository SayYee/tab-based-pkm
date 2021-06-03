package com.sayyi.software.tbp.cli.script.filecli;

import com.sayyi.software.tbp.cli.script.PkmFunctionHolder;
import lombok.SneakyThrows;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * mv <id> [-f newname] [-t newtags]
 * @author xuchuang
 * @date 2021/6/1
 */
@CommandLine.Command(name = "mv", description = "资源重命名", mixinStandardHelpOptions = true)
public class MvCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "资源id")
    private long id;

    @CommandLine.Option(names = {"-f", "--file"}, description = "新的文件名（带后缀）")
    private String newname;

    @CommandLine.Option(names = {"-t", "--tags"}, description = "新的标签集合（点号分割）")
    private String newtags;

    @SneakyThrows
    @Override
    public void run() {
        boolean modifyName = newname != null && !"".equals(newname);
        boolean modifyTags = newtags != null && !"".equals(newtags);
        if (!modifyName && !modifyTags) {
            System.out.println("未进行修改");
            return;
        }
        if (modifyName) {
            PkmFunctionHolder.get().rename(id, newname);
        }
        if (modifyTags) {
            Set<String> tagSet = Arrays.stream(newtags.split("\\.")).collect(Collectors.toSet());
            PkmFunctionHolder.get().modifyTag(id, tagSet);
        }

    }
}
