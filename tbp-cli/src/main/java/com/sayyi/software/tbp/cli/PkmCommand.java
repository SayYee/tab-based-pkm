package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.decorator.CmdPkmFunction;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.HashSet;
import java.util.Set;

/**
 * 这个类只负责将传入的命令行参数，转换为pkm的调用参数，同时调用pkm方法。
 * 为了实现终端调用和方法调用不同的处理方式，选择对传入的pkmFunction进行特殊的处理。
 * 具体参见{@link com.sayyi.software.tbp.cli.decorator.TerminalPkmFunction}
 * 和 {@link CmdPkmFunction}
 * @author SayYi
 */
@Command(name = "pkm", description = "pkm命令行")
public class PkmCommand {

    private final PkmFunction pkmFunction;

    public PkmCommand(PkmFunction pkmFunction) {
        this.pkmFunction = pkmFunction;
    }

    @Command(name = "deleteTag", description = "删除标签")
    public void deleteTag(@Parameters(index = "0", description = "tag to delete", paramLabel = "<tag>") String tag) throws Exception {
        pkmFunction.deleteTag(tag);
    }

    @Command(name = "renameTag", description = "重命名标签")
    public void renameTag(@Parameters(index = "0", description = "tag to rename", paramLabel = "<tag>") String tag,
                       @Parameters(index = "1", description = "new tag name", paramLabel = "<new-tag>") String newTag) throws Exception {
        pkmFunction.renameTag(tag, newTag);
    }

    @Command(name = "batchModifyTags", description = "批量修改文件标签")
    public void batchModifyTags(@Parameters(index = "0", description = "tagStr", paramLabel = "<tag，点号分隔>") String tagStr,
                          @Parameters(index = "1", description = "newTagStr", paramLabel = "<new-tag，点号分隔>") String newTagStr) throws Exception {
        Set<String> tags = str2Set(tagStr);
        Set<String> newTags = str2Set(newTagStr);
        pkmFunction.batchModifyTags(tags, newTags);
    }

    private Set<String> str2Set(String str) {
        final String[] split = str.split("\\.");
        Set<String> set = new HashSet<>();
        for (String s : split) {
            if (s == null || "".equals(s)) {
                continue;
            }
            set.add(s);
        }
        return set;
    }
}
