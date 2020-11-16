package com.sayyi.software.tbp.common.action;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 删除文件操作
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAction implements Record {
    private long id;
    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
    }
}
