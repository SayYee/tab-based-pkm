package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.util.PrintUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.*;
import picocli.CommandLine.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SayYi
 */
@Command(name = "file", mixinStandardHelpOptions = true, description = "文件操作相关的指令")
public class FileOpCommand {

    @ParentCommand
    CliCommand cmd;

    @Command(mixinStandardHelpOptions = true, name = "copy", description = "将本地文件加入PKM")
    public void copy(@Parameters(index = "0", description = "本地文件路径", paramLabel = "<path>") String path,
                     @Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws IOException, InterruptedException {
        FileWithPath fileWithPath = new FileWithPath();
        fileWithPath.setFilepath(path);
        tags = tags == null ? new HashSet<>() : tags;
        fileWithPath.setTags(tags);
        cmd.out.println(fileWithPath);

        FileMetadata fileMetadata = new FileMetadata();
        cmd.sender.sendForRecord(RequestType.COPY, fileWithPath, fileMetadata);

        PrintUtil.printFileList(cmd.out, fileMetadata);
    }

    @Command(mixinStandardHelpOptions = true, name = "url", description = "将url加入PKM")
    public void url(@Parameters(index = "0", description = "url名称", paramLabel = "<name>") String name,
                    @Parameters(index = "1", description = "url", paramLabel = "<url>") String url,
                    @Parameters(index = "2..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws IOException, InterruptedException {
        FileBaseInfo fileBaseInfo = new FileBaseInfo();
        fileBaseInfo.setFilename(name);
        fileBaseInfo.setResourcePath(url);
        fileBaseInfo.setTags(tags == null ? new HashSet<>() : tags);
        cmd.out.println(fileBaseInfo);

        FileMetadata fileMetadata = new FileMetadata();
        cmd.sender.sendForRecord(RequestType.ADD_URL, fileBaseInfo, fileMetadata);

        PrintUtil.printFileList(cmd.out, fileMetadata);
    }

    @Command(mixinStandardHelpOptions = true, name = "rename", description = "重命名")
    public void rename(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id,
                       @Parameters(index = "1", description = "新名称", paramLabel = "<new-name>") String newName) throws IOException, InterruptedException {
        RenameRequest renameRequest = new RenameRequest();
        renameRequest.setId(id);
        renameRequest.setNewName(newName);
        cmd.out.println(renameRequest);
        cmd.sender.sendForRecord(RequestType.RENAME, renameRequest, null);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "delete", description = "删除文件")
    public void delete(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id) throws IOException, InterruptedException {
        cmd.sender.sendForRecord(RequestType.DELETE, id, null);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "open", description = "打开文件")
    public void open(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id) throws IOException, InterruptedException {
        OpenRequest openRequest = new OpenRequest();
        openRequest.setId(id);
        cmd.sender.sendForRecord(RequestType.OPEN, openRequest, null);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "tag", description = "修改文件标签")
    public void tag(@ArgGroup(exclusive = true, multiplicity = "1") Exclusive exclusive,
                    @Parameters(index = "0", description = "id", paramLabel = "<id>") long id,
                    @Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws IOException, InterruptedException {
        int requestType;
        if (exclusive.add) {
            requestType = RequestType.ADD_RESOURCE_TAG;
        } else {
            requestType = RequestType.DELETE_RESOURCE_TAG;
        }
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        modifyTagRequest.setId(id);
        modifyTagRequest.setNewTags(tags);

        cmd.sender.sendForRecord(requestType, modifyTagRequest, null);
        cmd.out.println("success");
    }

    static class Exclusive {
        @Option(names = {"--add"}, description = "添加标签", required = true)
        boolean add;
        @Option(names = {"--delete"}, description = "删除标签", required = true)
        boolean delete;
    }
}
