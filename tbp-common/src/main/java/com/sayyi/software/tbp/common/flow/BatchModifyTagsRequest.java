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
public class BatchModifyTagsRequest implements Record {

    private Set<String> tags = new HashSet<>();
    private Set<String> newTags = new HashSet<>();

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeInt(tags.size());
        for (String tag : tags) {
            archive.writeString(tag);
        }
        archive.writeInt(newTags.size());
        for (String newTag : newTags) {
            archive.writeString(newTag);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        int tagSize = archive.readInt();
        for (int i = 0; i < tagSize; i++) {
            tags.add(archive.readString());
        }
        int newTagSize = archive.readInt();
        for (int i = 0; i < newTagSize; i++) {
            newTags.add(archive.readString());
        }
    }
}
