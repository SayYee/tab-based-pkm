package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.ActionInfo;
import com.sayyi.software.tbp.common.Snapshot;
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
    private static final String ACTION_FILE_PREFIX = "action-";

    private final String snapDir;
    private final File snapDirFile;

    private File actionFile;
    private FileOutputStream actionFileOut;

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
            log.debug("写入本地文件成功，校验和为【{}】", checksum.getValue());
            checkedOutputStream.flush();
        }
    }


    @Override
    public void storeAction(ActionInfo actionInfo) throws IOException {
        if (actionFile == null) {
            final long opId = actionInfo.getOpId();
            actionFile = new File(snapDir, ACTION_FILE_PREFIX + opId);
            actionFileOut = new FileOutputStream(actionFile);
        }
        OutputArchive outputArchive = BinaryOutputArchive.getArchive(actionFileOut);
        outputArchive.writeRecord(actionInfo);
        actionFileOut.flush();
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
    public Iterator<ActionInfo> actionIterator() throws IOException {
        return new ActionFileIterator();
    }

    @Override
    public void cleanOutOfDateFile(long opId) throws IOException {
        List<File> toDeleteFiles = new LinkedList<>();

        final File[] snapShotFiles = listSnapShotFile();
        for (File snapShotFile : snapShotFiles) {
            final long fileOpId = getFileOpId(snapShotFile);
            if (fileOpId < opId) {
                toDeleteFiles.add(snapShotFile);
            }
        }

        final File[] actionFiles = listActionFile();
        File file = null;
        long nearestOpId = -1;
        for (File actionFile : actionFiles) {
            final long fileOpId = getFileOpId(actionFile);
            if (fileOpId > opId) {
                continue;
            }
            if (fileOpId > nearestOpId) {
                if (file != null) {
                    toDeleteFiles.add(actionFile);
                }
                nearestOpId = fileOpId;
                file = actionFile;
            } else {
                toDeleteFiles.add(actionFile);
            }
        }

        for (File toDeleteFile : toDeleteFiles) {
            final boolean delete = toDeleteFile.delete();
            log.info("清理过期的数据文件【{}】，清理结果【{}】", toDeleteFile, delete);
        }
    }

    private File[] listSnapShotFile() {
        return snapDirFile.listFiles((dir, name) -> name.startsWith(SNAPSHOT_FILE_PREFIX));
    }

    private File[] listActionFile() {
        return snapDirFile.listFiles((dir, name) -> name.startsWith(ACTION_FILE_PREFIX));
    }

    /**
     * 从文件名中获取对应的opId。对于snapshot文件，获取到的是文件对应的opId。对于action，获取的是起始opId
     * @param file
     * @return
     */
    private long getFileOpId(File file) {
        final String name = file.getName();
        final String[] split = name.split("-");
        return Long.parseLong(split[1]);
    }

    private class ActionFileIterator implements Iterator<ActionInfo> {

        private Iterator<File> fileIterator;
        private ByteArrayInputStream byteArrayInputStream;
        private InputArchive archive;

        private boolean initSuccess = false;

        ActionFileIterator() throws IOException {
            init();
        }

        /**
         * 加载文件，寻找对应的操作
         * @throws IOException
         */
        private void init() throws IOException {
            File[] files = listActionFile();
            if (files == null || files.length == 0) {
                log.info("未找到action文件");
                return;
            }
            fileIterator = Arrays.stream(files)
                    .sorted(Comparator.comparingLong(FileBasedDbManager.this::getFileOpId))
                    .iterator();
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
        public ActionInfo next() {
            try {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                ActionInfo actionInfo = new ActionInfo();
                if (byteArrayInputStream.available() <= 0) {
                    loadFromNextFile();
                }
                archive.readRecord(actionInfo);
                return actionInfo;
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
