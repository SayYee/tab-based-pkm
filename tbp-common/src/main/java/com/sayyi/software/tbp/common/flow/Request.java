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
public class Request implements Record {

    /**
     * 操作id
     */
    private long opId;
    /**
     * 操作类型
     */
    private int opType;
    /**
     * 数据
     */
    private byte[] data;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(opId);
        archive.writeInt(opType);
        archive.writeBuffer(data);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        opId = archive.readLong();
        opType = archive.readInt();
        data = archive.readBuffer();
    }
}
