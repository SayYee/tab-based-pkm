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
public class Response implements Record {
    /**
     * 是否异常
     */
    private boolean isError;
    /**
     * 异常信息
     */
    private String errorMsg;
    /**
     * 响应结果
     */
    private byte[] result;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeBool(isError);
        archive.writeString(errorMsg);
        archive.writeBuffer(result);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        isError = archive.readBool();
        errorMsg = archive.readString();
        result = archive.readBuffer();
    }
}
