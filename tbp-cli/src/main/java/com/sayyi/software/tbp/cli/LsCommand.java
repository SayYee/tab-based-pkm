package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.util.PrintUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.QueryFileRequest;
import com.sayyi.software.tbp.common.flow.QueryTagRequest;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author SayYi
 */
@Command(mixinStandardHelpOptions = true, name = "ls", description = "查询相关操作")
public class LsCommand {

    @ParentCommand
    CliCommand cmd;

    @Command(mixinStandardHelpOptions = true, name = "file", description = "文件查询")
    public void lsFile(@Option(names = {"--name"}, description = "文件名称匹配表达式", paramLabel = "<name-reg>") String filenameReg,
                       @Option(names = {"--tags"}, arity = "*", description = "标签组", paramLabel = "<tag>") Set<String> tags) throws InterruptedException, InstantiationException, IllegalAccessException, IOException {
        QueryFileRequest queryFileRequest = new QueryFileRequest();
        queryFileRequest.setFilenameReg(filenameReg);
        queryFileRequest.setTags(tags == null ? new HashSet<>() : tags);
        cmd.out.println(queryFileRequest);

        List<FileMetadata> fileMetadataList = new LinkedList<>();
        cmd.sender.sendForCollection(RequestType.LIST_RESOURCES, queryFileRequest, FileMetadata.class, fileMetadataList);
        PrintUtil.printFileList(cmd.out, fileMetadataList.toArray(new FileMetadata[0]));
    }

    @Command(mixinStandardHelpOptions = true, name = "tag", description = "标签查询")
    public void lsTag(@Parameters(description = "标签组", paramLabel = "<tag>") Set<String> tags) throws InterruptedException, InstantiationException, IllegalAccessException, IOException {
        QueryTagRequest queryTagRequest = new QueryTagRequest();
        queryTagRequest.setTags(tags == null ? new HashSet<>() : tags);
        cmd.out.println(queryTagRequest);

        List<TagInfo> tagInfos = new LinkedList<>();
        cmd.sender.sendForCollection(RequestType.LIST_TAGS, queryTagRequest, TagInfo.class, tagInfos);
        PrintUtil.printTagList(cmd.out, tagInfos.toArray(new TagInfo[0]));
    }
}
