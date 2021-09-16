package com.sayyi.software.tbp.common.snap.model;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.snap.Version;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 快照信息
 * @author SayYi
 */
@Data
public class CurrentSnapshot implements Version {
    private long lastOpId;
    private long lastFileId;
    private List<FileMetadata> fileMetadataList;

    @Override
    public int version() {
        return 2;
    }

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(lastOpId);
        archive.writeLong(lastFileId);
        int size = fileMetadataList.size();
        archive.writeInt(size);
        for (FileMetadata fileMetadata : fileMetadataList) {
            archive.writeRecord(fileMetadata);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        lastOpId = archive.readLong();
        lastFileId = archive.readLong();

        int size = archive.readInt();
        fileMetadataList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.deserialize(archive);
            fileMetadataList.add(fileMetadata);
        }
    }
}
