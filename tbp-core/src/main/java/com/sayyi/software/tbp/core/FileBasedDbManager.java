package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;

/**
 * 基于文件的 元数据持久化组件
 * @author SayYi
 */
@Slf4j
public class FileBasedDbManager implements DbFunction {

    private static final String SNAPSHOT_FILE_PREFIX = "snapshot-";
    private static final String REQUEST_FILE_PREFIX = "request-";

    private final String snapDir;
    private final File snapDirFile;

    private File requestFile;
    private FileOutputStream requestFileOut;

    public FileBasedDbManager(String snapDir) {
        this.snapDir = snapDir;
        snapDirFile = new File(snapDir);
        if (!snapDirFile.exists()) {
            if (!snapDirFile.mkdirs()) {
                throw new IllegalArgumentException(snapDirFile.toString() + " file is missing and create failed");
            }
        }
    }

    @Override
    public void storeSnap(Snapshot snapshot) throws IOException {
        long opId = snapshot.getLastOpId();
        File snapshotFile = new File(snapDir, SNAPSHOT_FILE_PREFIX + opId);
        try (FileOutputStream fileOutputStream = new FileOutputStream(snapshotFile);
             CheckedOutputStream checkedOutputStream = new CheckedOutputStream(new BufferedOutputStream(fileOutputStream), new Adler32())) {
            // 写数据
            OutputArchive outputArchive = BinaryOutputArchive.getArchive(checkedOutputStream);
            outputArchive.writeRecord(snapshot);
            // 写校验和
            Checksum checksum = checkedOutputStream.getChecksum();
            outputArchive.writeLong(checksum.getValue());
            log.debug("写入本地文件【{}】成功，校验和为【{}】", snapshotFile, checksum.getValue());
            checkedOutputStream.flush();
        }
    }

    @Override
    public void storeRequest(Request request) throws IOException {
        log.debug("store request【opId={}, opType={}】", request.getOpId(), request.getOpType());
        if (requestFile == null) {
            final long opId = request.getOpId();
            requestFile = new File(snapDir, REQUEST_FILE_PREFIX + opId);
            requestFileOut = new FileOutputStream(requestFile);
        }
        OutputArchive outputArchive = BinaryOutputArchive.getArchive(requestFileOut);
        outputArchive.writeRecord(request);
        requestFileOut.flush();
    }

    @Override
    public Snapshot loadSnap() throws IOException {
        Snapshot snapshot = new Snapshot();
        final File[] files = listSnapShotFile();
        if (files == null || files.length == 0) {
            snapshot.setLastOpId(-1);
            log.info("未找到snapshot文件");
            return snapshot;
        }

        File latestFile = files[0];
        long lastOpId = getFileOpId(latestFile);
        for (int i = 1; i < files.length; i++) {
            final long fileOpId = getFileOpId(files[i]);
            if (fileOpId > lastOpId) {
                latestFile = files[i];
                lastOpId = fileOpId;
            }
        }
        log.info("获取snapshot文件成功【{}】", latestFile);

        try (final FileInputStream fileInputStream = new FileInputStream(latestFile);
        final CheckedInputStream checkedInputStream = new CheckedInputStream(new BufferedInputStream(fileInputStream), new Adler32())) {
            InputArchive inputArchive = BinaryInputArchive.getArchive(checkedInputStream);
            inputArchive.readRecord(snapshot);

            final long value = checkedInputStream.getChecksum().getValue();
            log.debug("读取校验和为【{}】", value);
            final long checkValue = inputArchive.readLong();
            log.debug("实际校验和为【{}】", checkValue);
            if (value != checkValue) {
                throw new IllegalArgumentException("snapshot文件校验失败");
            }
        }
        return snapshot;
    }

    @Override
    public Iterator<Request> requestIterator(long lastOpId) throws IOException {
        return new RequestFileIterator(lastOpId);
    }

    @Override
    public void cleanOutOfDateFile(long opId) throws IOException {
        log.debug("开始清理数据【opId={}】", opId);
        List<File> toDeleteFiles = new LinkedList<>();

        log.debug("获取需要清理的snapshot文件");
        final File[] snapShotFiles = listSnapShotFile();
        for (File snapShotFile : snapShotFiles) {
            final long fileOpId = getFileOpId(snapShotFile);
            if (fileOpId < opId) {
                toDeleteFiles.add(snapShotFile);
                log.debug("添加待处理快照文件【{}】", snapShotFile);
            }
        }
        log.debug("snapshot文件处理完毕");

        log.debug("获取需要清理的request文件");
        final File[] requestFiles = listRequestFile();
        File file = null;
        long nearestOpId = -1;
        for (File requestFile : requestFiles) {
            final long fileOpId = getFileOpId(requestFile);
            log.debug("读取本地request文件，fileOpId={}", fileOpId);
            if (fileOpId > opId) {
                log.debug("fileOpId-{} > opId", fileOpId);
                continue;
            }
            if (fileOpId > nearestOpId) {
                if (file != null) {
                    toDeleteFiles.add(file);
                    log.debug("添加待处理request文件【{}】", file);
                }
                nearestOpId = fileOpId;
                file = requestFile;
                log.debug("更新 nearestOpId={}", nearestOpId);
            } else {
                toDeleteFiles.add(requestFile);
                log.debug("添加待处理request文件【{}】", requestFile);
            }
        }
        log.debug("request文件处理完毕，开始清理文件");

        for (File toDeleteFile : toDeleteFiles) {
            final boolean delete = toDeleteFile.delete();
            log.info("清理过期的数据文件【{}】，清理结果【{}】", toDeleteFile, delete);
        }
    }

    private File[] listSnapShotFile() {
        return snapDirFile.listFiles((dir, name) -> name.startsWith(SNAPSHOT_FILE_PREFIX));
    }

    private File[] listRequestFile() {
        return snapDirFile.listFiles((dir, name) -> name.startsWith(REQUEST_FILE_PREFIX));
    }

    /**
     * 从文件名中获取对应的opId。对于snapshot文件，获取到的是文件对应的opId。对于request，获取的是起始opId
     * @param file
     * @return
     */
    private long getFileOpId(File file) {
        final String name = file.getName();
        final String[] split = name.split("-");
        return Long.parseLong(split[1]);
    }

    private class RequestFileIterator implements Iterator<Request> {

        private Iterator<File> fileIterator;
        private ByteArrayInputStream byteArrayInputStream;
        private InputArchive archive;

        private boolean initSuccess = false;

        RequestFileIterator(long lastOpId) throws IOException {
            init(lastOpId);
        }

        /**
         * 加载文件，寻找对应的操作
         * @throws IOException
         */
        private void init(long lastOpId) throws IOException {
            File[] files = listRequestFile();
            if (files == null || files.length == 0) {
                log.info("未找到request文件");
                return;
            }
            Iterator<File> reversedIterator = Arrays.stream(files)
                    .sorted(Comparator.comparingLong(FileBasedDbManager.this::getFileOpId)
                                    .reversed())
                    .iterator();
            LinkedList<File> fileList = new LinkedList<>();
            while (reversedIterator.hasNext()) {
                final File next = reversedIterator.next();
                // TODO opId溢出问题
                if (getFileOpId(next) > lastOpId) {
                    fileList.push(next);
                } else {
                    fileList.push(next);
                    break;
                }
            }

            fileIterator = fileList.iterator();
            while (fileIterator.hasNext()) {
                loadFromNextFile();
                if (byteArrayInputStream.available() > 0) {
                    initSuccess = true;
                    return;
                }
            }
            log.info("未找到对应的操作信息");
        }

        @Override
        public boolean hasNext() {
            return initSuccess
                    && (fileIterator.hasNext() || byteArrayInputStream.available() > 0);
        }

        @Override
        public Request next() {
            try {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Request request = new Request();
                if (byteArrayInputStream.available() <= 0) {
                    loadFromNextFile();
                }
                archive.readRecord(request);
                return request;
            } catch (IOException e) {
                log.error("unknown error", e);
                return null;
            }
        }

        /**
         * 从下一个文件中加载数据。
         * @throws IOException
         */
        private void loadFromNextFile() throws IOException {
            final File next = fileIterator.next();
            final byte[] bytes = Files.readAllBytes(next.toPath());
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            archive = BinaryInputArchive.getArchive(byteArrayInputStream);
        }
    }

}
