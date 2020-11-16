package com.sayyi.software.tbp.common;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件元数据
 * @author SayYi
 */
@Data
public class FileMetadata implements Comparable<FileMetadata>, Record {

    /**
     * 文件id
     */
    private long id;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 文件相对路径
     */
    private String relativePath;
    /**
     * 标签集合
     */
    private Set<String> tags;

    /**
     * 创建时间戳
     */
    private long createTime;
    /**
     * 最后一次打开时间戳。
     */
    private long lastOpenTime;

    @Override
    public int compareTo(FileMetadata o) {
        return Long.compare(id, o.id);
    }

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(id);
        archive.writeString(filename);
        archive.writeString(relativePath);

        archive.writeInt(tags.size());
        for (String tag : tags) {
            archive.writeString(tag);
        }

        archive.writeLong(createTime);
        archive.writeLong(lastOpenTime);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
        filename = archive.readString();
        relativePath = archive.readString();

        int tagSize = archive.readInt();
        tags = new HashSet<>(tagSize);
        for (int i = 0; i < tagSize; i++) {
            tags.add(archive.readString());
        }

        createTime = archive.readLong();
        lastOpenTime = archive.readLong();
    }
}
