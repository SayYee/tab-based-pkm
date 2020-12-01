package com.sayyi.software.tbp.core;

import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author SayYi
 */
public class FileTest {

    @Test
    public void test() throws IOException {
        String filename = "测试文件.md";
        File file = new File(filename);
        if (!file.exists()) {
            Files.createFile(file.toPath());
        }
        Desktop.getDesktop().open(file);
    }
}
