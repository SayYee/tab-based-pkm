package com.sayyi.software.tbp.db.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.db.api.component.FileHelper;

import java.io.File;
import java.time.YearMonth;
import java.util.StringJoiner;

public class FileHelperImpl implements FileHelper {

    private final String fileStoreDir;

    public FileHelperImpl(String fileStoreDir) {
        this.fileStoreDir = fileStoreDir;
        File file = new File(fileStoreDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IllegalArgumentException(file.toString() + " file is missing and create failed");
            }
        }
    }

    @Override
    public File getFile(FileMetadata fileMetadata) {
        StringJoiner stringJoiner = new StringJoiner(File.separator);
        stringJoiner.add(fileStoreDir);
        for (String s : fileMetadata.getResourcePath()) {
            stringJoiner.add(s);
        }
        stringJoiner.add(fileMetadata.getFilename());
        return new File(stringJoiner.toString());
    }

    @Override
    public String[] assignPath() {
        final YearMonth now = YearMonth.now();
        File storePath = new File(fileStoreDir, now.toString());
        if (!storePath.exists()) {
            if (!storePath.mkdirs()) {
                throw new IllegalArgumentException(storePath + " file is missing and create failed");
            }
        }
        return new String[]{now.toString()};
    }
}
