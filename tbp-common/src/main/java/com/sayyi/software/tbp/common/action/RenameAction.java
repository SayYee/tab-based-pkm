package com.sayyi.software.tbp.common.action;

import com.sayyi.software.tbp.common.FileInfo;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 文件重命名操作
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenameAction implements Record {

    private long id;
    private FileInfo fileInfo;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
        archive.writeRecord(fileInfo);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
        fileInfo = new FileInfo();
        archive.readRecord(fileInfo);
    }
}
