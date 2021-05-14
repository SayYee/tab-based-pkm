package com.sayyi.software.tbp.core;

import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    @Test
    public void test_convert_file_path() throws Exception {
        String filepath = "/user/local/bin";
        String filename = "\\2021-03\\hello.md";
        File file = new File(filepath, filename);
        System.out.println(file.getAbsoluteFile());
    }

    @Test
    public void test_location() throws Exception {
        String filename = "C:\\Users\\SayYi\\Desktop\\temp\\测试文件.html";
//        File file = new File(filename);
//        Desktop.getDesktop().open(file.getParentFile());

//        Runtime.getRuntime().exec("explorer /select, " + filename);
//        System.out.println(System.getProperty("os.name"));
        File file = new File(filename);
        Files.createFile(file.toPath());
        Files.write(file.toPath(), generateHtml("https://github.com/dendronhq/dendron").getBytes(UTF_8));
    }

    @Test
    public void test_load_default_tree() throws Exception {
        File file = new File(this.getClass().getResource("/default.tree").toURI());
        byte[] content = Files.readAllBytes(file.toPath());
        System.out.println(new String(content, UTF_8));
    }

    private String generateHtml(String url) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>url中转页面</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>页面跳转中</h1>\n" +
                "</body>\n" +
                "<script>\n" +
                "    var targetUrl = \"" + url + "\";\n" +
                "    document.write(\"<a href='\" + targetUrl + \"'>\" + targetUrl + \"</a>\");\n" +
                "    window.location.href = targetUrl;\n" +
                "</script>\n" +
                "</html>";
    }


}
