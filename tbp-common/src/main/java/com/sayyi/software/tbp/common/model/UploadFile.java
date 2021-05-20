// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;

@Data
public class UploadFile implements Record  {
    private String filename;
    private byte[] data;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(filename);
        archive.writeBuffer(data);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        filename = archive.readString();
        data = archive.readBuffer();
    }
}