package com.sayyi.software.tbp.common;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    /**
     * 复制文件夹及其子文件
     * @param sourceDir
     * @param targetDir
     * @throws IOException
     */
    public static void copyDir(File sourceDir, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        final File[] files = sourceDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                copyDir(file, new File(targetDir, file.getName()));
            } else {
                File targetFile = new File(targetDir, file.getName());
                copyFile(file, targetFile);
            }
        }
    }

    /**
     * 复制文件。零拷贝
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        try (FileInputStream input = new FileInputStream(sourceFile);
             FileChannel inputChannel = input.getChannel();
             FileOutputStream output = new FileOutputStream(targetFile);
             FileChannel outputChannel = output.getChannel()) {
            inputChannel.transferTo(0, sourceFile.length(), outputChannel);
        }
    }

    /**
     * 文件重命名
     * @param file  文件路径
     * @param newName   新的文件名
     * @return  新的文件信息
     */
    public static void rename(File file, String newName) throws IOException {
        Path source = file.toPath();
        Files.move(source, source.resolveSibling(newName));
    }

    /**
     * 删除文件。可以删除文件夹
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        if (!file.isDirectory()) {
            Files.delete(file.toPath());
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                delete(subFile);
            }
        }
        Files.delete(file.toPath());
    }

    /**
     * 打开文件所在文件夹，并选中文件
     * @param file
     * @throws IOException
     */
    public static void select(File file) throws IOException {
        String realPath = file.getPath();
        if (isWin()) {
            Runtime.getRuntime().exec("explorer /select, " + realPath);
        } else if (isMacOS()) {
            Runtime.getRuntime().exec("open -R " + realPath);
        } else {
            throw new IOException("未支持的平台");
        }
    }

    /**
     * 使用关联软件打开文件
     * @param file
     * @throws IOException
     */
    public static void open(File file) throws IOException {
        Desktop.getDesktop().open(file);
    }

    private final static String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWin() {
        return OS.startsWith("win");
    }

    private static boolean isMacOS() {
        return OS.startsWith("mac");
    }
}
