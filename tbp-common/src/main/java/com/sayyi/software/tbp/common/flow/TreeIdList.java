package com.sayyi.software.tbp.common.flow;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author SayYi
 */
@Data
public class TreeIdList implements Record {
    private List<Long> ids;
    @Override
    public void serialize(OutputArchive archive) throws IOException {
        if (ids == null || ids.isEmpty()) {
            archive.writeInt(0);
            return;
        }
        archive.writeInt(ids.size());
        for (long id : ids) {
            archive.writeLong(id);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        int size = archive.readInt();
        ids = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            ids.add(archive.readLong());
        }
    }
}
