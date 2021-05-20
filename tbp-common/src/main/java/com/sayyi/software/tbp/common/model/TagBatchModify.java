// 使用模版生成，请不要手动修改
package com.sayyi.software.tbp.common.model;

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
public class TagBatchModify implements Record  {
    private Set<String> tags;
    private Set<String> newTags;

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        if (tags == null) {
			archive.writeInt(-1);
		} else {
			archive.writeInt(tags.size());
			for (String s : tags) {
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
        int tags_size = archive.readInt();
		if (tags_size == -1) {
			tags = null;
		} else {
			tags = new HashSet<>(tags_size);
			for(int i = 0; i < tags_size; i++) {
				tags.add(archive.readString());
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