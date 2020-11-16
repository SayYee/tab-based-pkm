package com.sayyi.software.tbp.common;

import com.sayyi.software.tbp.common.action.*;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static com.sayyi.software.tbp.common.constant.OpType.*;

/**
 * 操作信息
 * @author SayYi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionInfo implements Record {

    private long opId;
    private int opType;
    private Record action;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(opId);
        archive.writeInt(opType);
        archive.writeRecord(action);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        opId = archive.readLong();
        opType = archive.readInt();
        initActionByType();
        action.deserialize(archive);
    }

    private void initActionByType() {
        switch (opType) {
            case CREATE:
                action = new CreateAction();
                break;
            case RENAME:
                action = new RenameAction();
                break;
            case MODIFY_TAG:
                action = new ModifyTagAction();
                break;
            case OPEN:
                action = new OpenAction();
                break;
            case DELETE:
                action = new DeleteAction();
                break;
            case DELETE_TAG:
                action = new DeleteTagAction();
                break;
            case RENAME_TAG:
                action = new RenameTagAction();
                break;
            default:
                throw new IllegalArgumentException("找不到对应的操作实体类");
        }
    }
}
