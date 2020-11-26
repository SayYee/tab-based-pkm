package com.sayyi.software.tbp.common.flow;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SayYi
 */
@Data
public class QueryFileRequest implements Record {

    /** 文件名表达式 */
    private String filenameReg;
    /** 标签 */
    private Set<String> tags;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(filenameReg);
        archive.writeInt(tags.size());
        for (String tag : tags) {
            archive.writeString(tag);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        filenameReg = archive.readString();
        int size = archive.readInt();
        tags = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            tags.add(archive.readString());
        }
    }
}
