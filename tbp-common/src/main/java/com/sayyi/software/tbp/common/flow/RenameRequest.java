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
public class RenameRequest implements Record {
    private long id;
    private String newName;
    /** url类型的资源可以修改定位信息 */
    private String newLocation;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
        archive.writeString(newName);
        archive.writeString(newLocation);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
        newName = archive.readString();
        newLocation = archive.readString();
    }
}
