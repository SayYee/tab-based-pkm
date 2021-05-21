package com.sayyi.software.tbp.common.snap.model;

import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import com.sayyi.software.tbp.common.snap.Version;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 快照信息
 * @author SayYi
 */
@Data
public class SnapshotV00 implements Version {
    private long lastOpId;
    private long lastFileId;
    private List<Inner> innerList;

    @Override
    public int version() {
        return 0;
    }

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(lastOpId);
        archive.writeLong(lastFileId);
        int size = innerList.size();
        archive.writeInt(size);
        for (Inner inner : innerList) {
            archive.writeRecord(inner);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        lastOpId = archive.readLong();
        lastFileId = archive.readLong();

        int size = archive.readInt();
        innerList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Inner inner = new Inner();
            inner.deserialize(archive);
            innerList.add(inner);
        }
    }

    @Data
    public static class Inner implements Record {
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
        private String resourcePath;
        /**
         * 标签集合
         */
        private Set<String> tags = new HashSet<>();

        /**
         * 创建时间戳
         */
        private long createTime;
        /**
         * 最后一次打开时间戳。
         */
        private long lastOpenTime;

        @Override
        public void serialize(OutputArchive archive) throws IOException {
            archive.writeLong(id);
            archive.writeString(filename);
            archive.writeInt(resourceType);
            archive.writeString(resourcePath);

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
            resourceType = archive.readInt();
            resourcePath = archive.readString();

            int tagSize = archive.readInt();
            tags = new HashSet<>(tagSize);
            for (int i = 0; i < tagSize; i++) {
                tags.add(archive.readString());
            }

            createTime = archive.readLong();
            lastOpenTime = archive.readLong();
        }
    }
}
