package com.sayyi.software.tbp.common.snap.convert;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.snap.Convertor;
import com.sayyi.software.tbp.common.snap.Version;
import com.sayyi.software.tbp.common.snap.model.SnapshotV00;
import com.sayyi.software.tbp.common.snap.model.SnapshotV01;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 版本0到版本1的转换器实现
 * @author xuchuang
 * @date 2021/5/21
 */
public class Convertor0To1 implements Convertor {

    @Override
    public Version convert(Version source) {
        SnapshotV00 snapshotV00 = (SnapshotV00) source;
        SnapshotV01 snapshot = new SnapshotV01();
        snapshot.setLastOpId(snapshotV00.getLastOpId());
        snapshot.setLastFileId(snapshotV00.getLastFileId());

        List<SnapshotV01.Inner> fileMetadataList = new ArrayList<>(snapshotV00.getInnerList().size());
        snapshot.setInnerList(fileMetadataList);
        for (SnapshotV00.Inner inner : snapshotV00.getInnerList()) {
            SnapshotV01.Inner fileMetadata = new SnapshotV01.Inner();
            fileMetadataList.add(fileMetadata);

            fileMetadata.setId(inner.getId());
            fileMetadata.setFilename(inner.getFilename());
            fileMetadata.setResourceType(inner.getResourceType());
            String path = inner.getResourcePath();
            path = path.replaceAll("\\\\", "/");
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            fileMetadata.setResourcePath(path.split("/"));
            // 直接传递引用，也是ok的
            fileMetadata.setTags(inner.getTags());
            fileMetadata.setCreateTime(inner.getCreateTime());
            fileMetadata.setLastOpenTime(inner.getLastOpenTime());
        }
        return snapshot;
    }

    @Override
    public int[] support() {
        return new int[]{0, 1};
    }
}
