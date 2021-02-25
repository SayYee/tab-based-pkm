package com.sayyi.software.tbp.common.flow;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author SayYi
 */
@Data
public class FileBaseInfo implements Record {
    public FileBaseInfo() {
    }

    public FileBaseInfo(String resourcePath, String filename, long modifyTime) {
        this.resourcePath = resourcePath;
        this.filename = filename;
        this.modifyTime = modifyTime;
    }

    /** 重命名操作，也用这个对象。所以把id也放进来了 */
    private long fileId;
    private String resourcePath;
    private String filename;
    /** 回放创建文件请求的时候，需要有时间信息 */
    private long modifyTime;
    private Set<String> tags = new HashSet<>();

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(fileId);
        archive.writeString(resourcePath);
        archive.writeString(filename);
        archive.writeLong(modifyTime);
        archive.writeInt(tags.size());
        for (String tag : tags) {
            archive.writeString(tag);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        fileId = archive.readLong();
        resourcePath = archive.readString();
        filename = archive.readString();
        modifyTime = archive.readLong();
        int size = archive.readInt();
        tags = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            tags.add(archive.readString());
        }
    }
}
