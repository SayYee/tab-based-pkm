package com.sayyi.software.tbp.common.action;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 删除标签action
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTagAction implements Record {
    private String tag;
    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeString(tag);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        tag = archive.readString();
    }
}
