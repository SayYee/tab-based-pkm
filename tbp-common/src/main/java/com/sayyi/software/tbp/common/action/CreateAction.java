package com.sayyi.software.tbp.common.action;

import com.sayyi.software.tbp.common.FileInfo;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件创建操作
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAction implements Record {

    FileInfo fileInfo;
    Set<String> tags;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeRecord(fileInfo);

        int size = tags.size();
        archive.writeInt(size);
        for (String tag : tags) {
            archive.writeString(tag);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        fileInfo = new FileInfo();
        fileInfo.deserialize(archive);

        int size = archive.readInt();
        tags = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            tags.add(archive.readString());
        }

    }
}
