// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Data
public class TreeIdList implements Record  {
    private List<Long> ids;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        if (ids == null) {
			archive.writeInt(-1);
		} else {
			archive.writeInt(ids.size());
			for (Long s : ids) {
				archive.writeLong(s);
			}
		}
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        int ids_size = archive.readInt();
		if (ids_size == -1) {
			ids = null;
		} else {
			ids = new LinkedList<>();
			for(int i = 0; i < ids_size; i++) {
				ids.add(archive.readLong());
			}
		}
    }
}