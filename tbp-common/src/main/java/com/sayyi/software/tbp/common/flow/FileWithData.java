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
public class FileWithData implements Record {
    private String filename;
    private byte[] data;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(filename);
        archive.writeBuffer(data);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        filename = archive.readString();
        data = archive.readBuffer();
    }
}
