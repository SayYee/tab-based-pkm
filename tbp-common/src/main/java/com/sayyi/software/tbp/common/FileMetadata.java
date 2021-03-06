package com.sayyi.software.tbp.common;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
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
     * 资源类型，会影响资源的操作方式。
     */
    private int resourceType = 1;
    /**
     * 资源路径。如果是本地文件，则是相对路径；如果是网络资源，则是链接地址
     */
    private String[] resourcePath;
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
        archive.writeInt(resourceType);
        if (resourcePath == null) {
            archive.writeInt(-1);
        } else {
            archive.writeInt(resourcePath.length);
            for (String s : resourcePath) {
                archive.writeString(s);
            }
        }

        if (tags == null) {
            archive.writeInt(-1);
        } else {
            archive.writeInt(tags.size());
            for (String tag : tags) {
                archive.writeString(tag);
            }
        }

        archive.writeLong(createTime);
        archive.writeLong(lastOpenTime);
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        id = archive.readLong();
        filename = archive.readString();
        resourceType = archive.readInt();
        int length = archive.readInt();
        if (length == -1) {
            resourcePath = null;
        } else {
            resourcePath = new String[length];
            for (int i = 0; i < length; i++) {
                resourcePath[i] = archive.readString();
            }
        }
        int tagSize = archive.readInt();
        if (tagSize == -1) {
            tags = null;
        } else {
            tags = new HashSet<>(tagSize);
            for (int i = 0; i < tagSize; i++) {
                tags.add(archive.readString());
            }
        }
        createTime = archive.readLong();
        lastOpenTime = archive.readLong();
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", resourceType=" + resourceType +
                ", resourcePath=" + Arrays.toString(resourcePath) +
                ", tags=" + tags +
                ", createTime=" + createTime +
                ", lastOpenTime=" + lastOpenTime +
                '}';
    }
}
