package com.sayyi.software.tbp.common.snap.model;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.snap.Version;
import com.sayyi.software.tbp.common.store.InputArchive;
import com.sayyi.software.tbp.common.store.OutputArchive;
import com.sayyi.software.tbp.common.store.Record;
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
public class SnapshotV01 implements Version {
    private long lastOpId;
    private long lastFileId;
    private List<Inner> innerList;

    @Override
    public int version() {
        return 1;
    }

    @Override
    public void serialize(OutputArchive archive) throws IOException {
        archive.writeLong(lastOpId);
        archive.writeLong(lastFileId);
        int size = innerList.size();
        archive.writeInt(size);
        for (Inner fileMetadata : innerList) {
            archive.writeRecord(fileMetadata);
        }
    }

    @Override
    public void deserialize(InputArchive archive) throws IOException {
        lastOpId = archive.readLong();
        lastFileId = archive.readLong();

        int size = archive.readInt();
        innerList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Inner fileMetadata = new Inner();
            fileMetadata.deserialize(archive);
            innerList.add(fileMetadata);
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
        private String[] resourcePath;
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
            if (resourcePath == null) {
                archive.writeInt(-1);
            } else {
                archive.writeInt(resourcePath.length);
                for (String s : resourcePath) {
                    archive.writeString(s);
                }
            }

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
            tags = new HashSet<>(tagSize);
            for (int i = 0; i < tagSize; i++) {
                tags.add(archive.readString());
            }

            createTime = archive.readLong();
            lastOpenTime = archive.readLong();
        }
    }
}
