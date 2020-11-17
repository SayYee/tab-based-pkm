package com.sayyi.software.tbp.common.action;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 打开文件操作
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAction implements Record {
    private long id;
    private long openTime;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
        archive.writeLong(openTime);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
        openTime = archive.readLong();
    }
}
