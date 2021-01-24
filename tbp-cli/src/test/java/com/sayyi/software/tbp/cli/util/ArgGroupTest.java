package com.sayyi.software.tbp.cli.util;

import picocli.CommandLine;

import java.util.Set;

/**
 * @author SayYi
 */
@CommandLine.Command(name = "file", mixinStandardHelpOptions = true, description = "文件操作相关的指令")
public class ArgGroupTest {

    @CommandLine.Command(mixinStandardHelpOptions = true, name = "tag", description = "修改文件标签")
    public void tag(@CommandLine.ArgGroup(exclusive = true, multiplicity = "1") Exclusive exclusive,
                    @CommandLine.Parameters(index = "0", description = "id", paramLabel = "<id>") long id,
                    @CommandLine.Parameters(index = "1..*", description = "文件关联tag", paramLabel = "<tag>") Set<String> tags) throws Exception {
        System.out.println(exclusive);
        System.out.println(id);
        System.out.println(tags);
    }

    public static class Exclusive {
        @CommandLine.Option(names = {"--add"}, description = "添加标签", required = true)
        boolean add;
        @CommandLine.Option(names = {"--delete"}, description = "删除标签", required = true)
        boolean delete;

        @Override
        public String toString() {
            return "Exclusive{" +
                    "add=" + add +
                    ", delete=" + delete +
                    '}';
        }
    }

    public static void main(String[] args) {
        args = new String[]{"tag", "--add", "1", "result.tar", "file1.txt", "file2.txt"};
        ArgGroupTest argGroupTest = new ArgGroupTest();
        new CommandLine(argGroupTest).execute(args);
    }
}
