package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.RenameTagRequest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;

/**
 * @author SayYi
 */
@Command(mixinStandardHelpOptions = true, name = "tag", description = "标签操作相关指令")
public class TagOpCommand {

    @ParentCommand
    CliCommand cmd;

    @Command(mixinStandardHelpOptions = true, name = "delete", description = "删除标签")
    public void delete(@Parameters(index = "0", description = "tag to delete", paramLabel = "<tag>") String tag) throws IOException, InterruptedException {
        cmd.sender.sendForRecord(RequestType.DELETE_TAG, tag, null);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "rename", description = "重命名标签")
    public void rename(@Parameters(index = "0", description = "tag to rename", paramLabel = "<tag>") String tag,
                       @Parameters(index = "1", description = "new tag name", paramLabel = "<new-tag>") String newTag) throws IOException, InterruptedException {
        RenameTagRequest renameTagRequest = new RenameTagRequest();
        renameTagRequest.setTag(tag);
        renameTagRequest.setNewTag(newTag);
        cmd.sender.sendForRecord(RequestType.RENAME_TAG, renameTagRequest, null);
        cmd.out.println("success");
    }
}
