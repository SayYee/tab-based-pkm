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
public class TagInfo implements Record {

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
