// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;

@Data
public class FileOperate implements Record  {
    private long fileId;
    private long time;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(fileId);
        archive.writeLong(time);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        fileId = archive.readLong();
        time = archive.readLong();
    }
}