package com.sayyi.software.tbp.common;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo implements Record {
    private String relativePath;
    private String filename;
    private long modifyTime;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(relativePath);
        archive.writeString(filename);
        archive.writeLong(modifyTime);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        relativePath = archive.readString();
        filename = archive.readString();
        modifyTime = archive.readLong();
    }
}
