package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.TbpConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

/**
 * @author SayYi
 */
public class FileManagerTest {

    private FileManager fileManager;
    String sourcePath;

    @Before
    public void prepare() throws IOException {
        sourcePath = Objects.requireNonNull(FileManagerTest.class.getClassLoader().getResource("test.md")).getPath();

        final String path = Objects.requireNonNull(FileManagerTest.class.getClassLoader().getResource("config.properties")).getPath();
        TbpConfig tbpConfig = new TbpConfig() {
            @Override
            public String getStoreDir() {
                return "C:\\Users\\SayYi\\Desktop\\temp\\pkmstore";
            }

            @Override
            public String getSnapDir() {
                return "C:\\Users\\SayYi\\Desktop\\temp\\pkmsnap";
            }
        };
        fileManager = new FileManager(tbpConfig.getStoreDir());
    }

    @Test
    public void test() throws IOException {
        System.out.println("sourceFile: " + sourcePath);
        FileManager.FileInfo copy = fileManager.copy(sourcePath);
        System.out.println("targetFile: " + copy);
        FileManager.FileInfo renameFile = fileManager.rename(copy.getRelativePath(), "new-设计文档.md");
        System.out.println("renameFile: " + renameFile);

        fileManager.open(renameFile.getRelativePath());
        System.out.println("openFile: " + renameFile);

//        fileManager.delete(renameFile.getRelativePath());
//        System.out.println("delete success");
    }

}
