package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.util.PrintUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import picocli.CommandLine.*;

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
                     @Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws Exception {
        FileMetadata fileMetadata = cmd.pkm.copy(path, tags);
        PrintUtil.printFileList(cmd.out, fileMetadata);
    }

    @Command(mixinStandardHelpOptions = true, name = "create", description = "创建文件加入PKM")
    public void create(@Parameters(index = "0", description = "文件名称，需要带后缀", paramLabel = "<name>") String name,
                       @Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags,
                       @Option(names = {"--open"}, description = "是否需要打开文件") boolean needOpen) throws Exception {
        FileMetadata fileMetadata = cmd.pkm.create(name, tags);
        PrintUtil.printFileList(cmd.out, fileMetadata);
        if (needOpen) {
            cmd.pkm.open(fileMetadata.getId());
        }
    }

    @Command(mixinStandardHelpOptions = true, name = "url", description = "将url加入PKM")
    public void url(@Parameters(index = "0", description = "url名称", paramLabel = "<name>") String name,
                    @Parameters(index = "1", description = "url", paramLabel = "<url>") String url,
                    @Parameters(index = "2..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws Exception {
        FileMetadata fileMetadata = cmd.pkm.url(name, url, tags);
        PrintUtil.printFileList(cmd.out, fileMetadata);
    }

    @Command(mixinStandardHelpOptions = true, name = "rename", description = "重命名")
    public void rename(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id,
                       @Parameters(index = "1", description = "新名称", paramLabel = "<new-name>") String newName) throws Exception {
        cmd.pkm.rename(id, newName);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "delete", description = "删除文件")
    public void delete(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id) throws Exception {
        cmd.pkm.delete(id);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "open", description = "打开文件")
    public void open(@Parameters(index = "0", description = "id", paramLabel = "<id>") long id) throws Exception {
        cmd.pkm.open(id);
        cmd.out.println("success");
    }

    @Command(mixinStandardHelpOptions = true, name = "tag", description = "修改文件标签")
    public void tag(@ArgGroup(exclusive = true, multiplicity = "1") Exclusive exclusive,
                    @Parameters(index = "0", description = "id", paramLabel = "<id>") long id,
                    @Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws Exception {
        if (exclusive.add) {
            cmd.pkm.addFileTag(id, tags);
        } else {
            cmd.pkm.deleteFileTag(id, tags);
        }
        cmd.out.println("success");
    }

    public static class Exclusive {
        @Option(names = {"--add"}, description = "添加标签", required = true)
        public boolean add;
        @Option(names = {"--delete"}, description = "删除标签", required = true)
        public boolean delete;
    }
}
