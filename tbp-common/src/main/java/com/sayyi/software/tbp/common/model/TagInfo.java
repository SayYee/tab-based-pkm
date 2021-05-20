// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;

@Data
public class TagInfo implements Record  {
    private String tag;
    private int fileNum;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(tag);
        archive.writeInt(fileNum);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        tag = archive.readString();
        fileNum = archive.readInt();
    }
}