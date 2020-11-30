package com.sayyi.software.tbp.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * @author SayYi
 */
@Command(mixinStandardHelpOptions = true, name = "tag", description = "标签操作相关指令")
public class TagOpCommand {

    @ParentCommand
    CliCommand cmd;

    @Command(mixinStandardHelpOptions = true, name = "delete", description = "删除标签")
    public void delete(@Parameters(index = "0", description = "tag to delete", paramLabel = "<tag>") String tag) throws Exception {
        cmd.pkm.deleteTag(tag);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "rename", description = "重命名标签")
    public void rename(@Parameters(index = "0", description = "tag to rename", paramLabel = "<tag>") String tag,
                       @Parameters(index = "1", description = "new tag name", paramLabel = "<new-tag>") String newTag) throws Exception {
        cmd.pkm.renameTag(tag, newTag);
        cmd.out.println("success");
    }
}
