package com.sayyi.software.tbp.common.flow;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;

/**
 * @author SayYi
 */
@Data
public class RenameTagRequest implements Record {
    private String tag;
    private String newTag;


    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(tag);
        archive.writeString(newTag);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        tag = archive.readString();
        newTag = archive.readString();
    }
}
