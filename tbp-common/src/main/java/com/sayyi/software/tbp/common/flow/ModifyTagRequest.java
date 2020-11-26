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
public class ModifyTagRequest implements Record {

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
