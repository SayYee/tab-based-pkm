package com.sayyi.software.tbp.common.snap.convert;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.snap.Convertor;
import com.sayyi.software.tbp.common.snap.Version;
import com.sayyi.software.tbp.common.snap.model.CurrentSnapshot;
import com.sayyi.software.tbp.common.snap.model.SnapshotV01;

import java.util.ArrayList;
import java.util.List;

public class Convertor1To2 implements Convertor {
    @Override
    public Version convert(Version source) {
        SnapshotV01 snapshotV01 = (SnapshotV01) source;
        CurrentSnapshot snapshot = new CurrentSnapshot();
        snapshot.setLastOpId(snapshotV01.getLastOpId());
        snapshot.setLastFileId(snapshotV01.getLastFileId());

        List<FileMetadata> fileMetadataList = new ArrayList<>(snapshotV01.getInnerList().size());
        snapshot.setFileMetadataList(fileMetadataList);
        for (SnapshotV01.Inner inner : snapshotV01.getInnerList()) {
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadataList.add(fileMetadata);

            fileMetadata.setId(inner.getId());
            fileMetadata.setFilename(inner.getFilename());
            fileMetadata.setResourceType(inner.getResourceType());
            // 路径中的最后一位是文件名，现在不在这里存储文件名了，直接使用filename就行了，节省空间，改起来也容易。
            String[] path = new String[inner.getResourcePath().length - 1];
            for (int i = 0; i < path.length; i++) {
                path[i] = inner.getResourcePath()[i];
            }
            fileMetadata.setResourcePath(path);
            fileMetadata.setTags(inner.getTags());
            fileMetadata.setCreateTime(inner.getCreateTime());
            fileMetadata.setLastOpenTime(inner.getLastOpenTime());
        }
        return snapshot;
    }

    @Override
    public int[] support() {
        return new int[]{1, 2};
    }
}
