// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model.update;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateTagsOp implements Record  {
    private Set<String> oldTags;
    private Set<String> newTags;
    public UpdateTagsOp(){}

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        if (oldTags == null) {
			archive.writeInt(-1);
		} else {
			archive.writeInt(oldTags.size());
			for (String s : oldTags) {
				archive.writeString(s);
			}
		}
        if (newTags == null) {
			archive.writeInt(-1);
		} else {
			archive.writeInt(newTags.size());
			for (String s : newTags) {
				archive.writeString(s);
			}
		}
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        int oldTags_size = archive.readInt();
		if (oldTags_size == -1) {
			oldTags = null;
		} else {
			oldTags = new HashSet<>(oldTags_size);
			for(int i = 0; i < oldTags_size; i++) {
				oldTags.add(archive.readString());
			}
		}
        int newTags_size = archive.readInt();
		if (newTags_size == -1) {
			newTags = null;
		} else {
			newTags = new HashSet<>(newTags_size);
			for(int i = 0; i < newTags_size; i++) {
				newTags.add(archive.readString());
			}
		}
    }
}