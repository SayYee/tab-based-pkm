package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.util.PrintUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

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
                       @Option(names = {"--tags"}, arity = "*", description = "标签组", paramLabel = "<tag>") Set<String> tags) throws Exception {
        List<FileMetadata> fileMetadataList = cmd.pkm.listByNameAndTag(tags, filenameReg);
        PrintUtil.printFileList(cmd.out, fileMetadataList.toArray(new FileMetadata[0]));
    }

    @Command(mixinStandardHelpOptions = true, name = "tag", description = "标签查询")
    public void lsTag(@Parameters(description = "标签组", paramLabel = "<tag>") Set<String> tags) throws Exception {
        List<TagInfo> tagInfos = cmd.pkm.listTags(tags);
        PrintUtil.printTagList(cmd.out, tagInfos.toArray(new TagInfo[0]));
    }
}
