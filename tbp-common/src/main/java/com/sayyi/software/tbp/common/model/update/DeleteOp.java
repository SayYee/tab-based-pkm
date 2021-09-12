// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model.update;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;

@Data
public class DeleteOp implements Record  {
    private long id;
    public DeleteOp(){}

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
    }
}