package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.flow.FileBaseInfo;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;

/**
 * 存储文件管理器
 * @author SayYi
 */
@Slf4j
public class FileManager {

    /**
     * 文件存储路径
     */
    private final String fileStoreDir;
    private final String absolutePathStoreDir;

    public FileManager(String fileStoreDir) {
        this.fileStoreDir = fileStoreDir;
        File file = new File(fileStoreDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalArgumentException(file.toString() + " file is missing and create failed");
            }
        }
        absolutePathStoreDir = file.getAbsolutePath();
    }

    /**
     * 使用关联软件打开文件
     * @param filePath  文件相对地址
     * @throws IOException
     */
    public void open(String filePath) throws IOException {
        Desktop.getDesktop().open(new File(fileStoreDir, filePath));
    }

    /**
     * 使用默认浏览器打开网页
     * @param url
     * @throws IOException
     */
    public void browse(String url) throws IOException {
        Desktop.getDesktop().browse(URI.create(url));
    }

    /**
     * 删除文件
     * @param filePath  目标文件相对地址
     */
    public void delete(String filePath) throws IOException {
        File file = new File(fileStoreDir, filePath);
        Files.delete(file.toPath());
    }

    /**
     * 文件重命名
     * @param filePath  文件相对地址
     * @param newName   新的文件名
     * @return  新的文件信息
     */
    public FileBaseInfo rename(String filePath, String newName) throws IOException {
        File file = new File(fileStoreDir, filePath);
        Path source = file.toPath();
        Files.move(source, source.resolveSibling(newName));
        return createFromFile(new File(file.getParent(), newName));
    }

    /**
     * 通过流的方式上传文件（js无法获取文件绝对路径，暂时只能这么搞了，反正都上http通信了，不在乎了）
     * 仅支持文件上传，不支持文件夹传输
     * @param filename  文件名称
     * @param in    输入流
     * @return
     * @throws IOException
     */
    public FileBaseInfo upload(String filename, InputStream in) throws IOException {
        File storePath = getStorePath();
        File target = new File(storePath, filename);
        // TODO-在这里处理文件名重复的问题，挺憨憨的。毕竟会分文件夹。
        //  除非仅仅是顾虑在用户不知情的情况下覆盖。不过，感觉还是禁止重名比较好。
        if (target.exists()) {
            throw new IOException("file already exists");
        }
        Files.copy(in, target.toPath());
        return createFromFile(target);
    }

    /**
     * @param filename
     * @param data
     * @return
     * @throws IOException
     */
    public FileBaseInfo upload(String filename, byte[] data) throws IOException {
        File storePath = getStorePath();
        File target = new File(storePath, filename);
        if (target.exists()) {
            throw new IOException("file already exists");
        }
        try (FileOutputStream output = new FileOutputStream(target);
             FileChannel outputChannel = output.getChannel()) {
            outputChannel.write(ByteBuffer.wrap(data));
        }
        return createFromFile(target);
    }

    /**
     * 将传入目标文件拷贝入文件存储系统中。按照月份，自动创建文件夹存储数据
     * 支持文件夹复制
     * @param sourceFile    目标文件绝对地址
     */
    public FileBaseInfo copy(String sourceFile) throws IOException {
        File source = new File(sourceFile);

        String filename = source.getName();
        File storePath = getStorePath();
        File target = new File(storePath, filename);
        if (target.exists()) {
            throw new IOException("file already exists");
        }
        if (source.isDirectory()) {
            copyDir(source, target);
        } else {
            copyFile(source, target);
        }
        return createFromFile(target);
    }

    /**
     * 复制文件
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    private void copyFile(File sourceFile, File targetFile) throws IOException {
        try (FileInputStream input = new FileInputStream(sourceFile);
             FileChannel inputChannel = input.getChannel();
             FileOutputStream output = new FileOutputStream(targetFile);
             FileChannel outputChannel = output.getChannel()) {
            inputChannel.transferTo(0, sourceFile.length(), outputChannel);
        }
    }

    /**
     * 复制文件夹及其子文件
     * @param sourceDir
     * @param targetDir
     * @throws IOException
     */
    private void copyDir(File sourceDir, File targetDir) throws IOException {
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
     * 获取文件存储位置。根据时间，创建文件夹存储数据。每月一个新的文件夹。比如2020-01
     * @return  文件所属文件夹
     */
    private File getStorePath() {
        final YearMonth now = YearMonth.now();
        File storePath = new File(fileStoreDir, now.toString());
        if (!storePath.exists()) {
            if (!storePath.mkdirs()) {
                throw new IllegalArgumentException(storePath.toString() + " file is missing and create failed");
            }
        }
        return storePath;
    }

    private FileBaseInfo createFromFile(File file) {
        return new FileBaseInfo(file.getAbsolutePath().substring(absolutePathStoreDir.length()), file.getName(), System.currentTimeMillis());
    }

}
