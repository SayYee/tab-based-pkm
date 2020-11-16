package com.sayyi.software.tbp.common.action;

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
 * 修改文件标签操作
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyTagAction implements Record {
    private long id;
    private Set<String> newTags;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
        archive.writeInt(newTags.size());
        for (String newTag : newTags) {
            archive.writeString(newTag);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();

        int tagSize = archive.readInt();
        newTags = new HashSet<>(tagSize);
        for (int i = 0; i < tagSize; i++) {
            newTags.add(archive.readString());
        }
    }
}
