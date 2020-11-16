package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileInfo;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.TbpException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 元数据管理组件
 * @author SayYi
 */
@Slf4j
public class MetadataManager {

    /**
     * 所有的文件元数据信息
     */
    private final List<FileMetadata> fileMetadataList = new ArrayList<>();

    /**
     * id-文件缓存
     */
    private final Map<Long, FileMetadata> id2FileMap = new HashMap<>();

    /**
     * 下一个文件id
     */
    private long nextFileId = 1;

    public void recovery(Snapshot snapshot) {
        if (snapshot.getLastOpId() == -1) {
            return;
        }
        nextFileId = snapshot.getLastFileId();
        fileMetadataList.addAll(snapshot.getFileMetadataList());
        for (FileMetadata fileMetadata : fileMetadataList) {
            id2FileMap.put(fileMetadata.getId(), fileMetadata);
        }
    }

    public void delete(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        fileMetadataList.remove(fileMetadata);
        id2FileMap.remove(fileId);
        log.info("删除文件关联tag【{}】", fileMetadata.getRelativePath());
    }

    /**
     * 文件重命名
     * @param fileId    文件id
     * @param fileInfo   重命名后的文件信息
     * @throws TbpException
     */
    public void rename(long fileId, FileInfo fileInfo) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        fileMetadata.setRelativePath(fileInfo.getRelativePath());
        fileMetadata.setFilename(fileInfo.getFilename());
    }

    /**
     * 初始化并保存文件元数据
     * @param fileInfo  文件基本信息
     * @param tags  文件关联标签
     */
    public FileMetadata initAndSaveFileMetadata(FileInfo fileInfo, Set<String> tags) {
        log.debug("文件存储成功【{}】", fileInfo);
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(nextFileId++);
        fileMetadata.setFilename(fileInfo.getFilename());
        fileMetadata.setRelativePath(fileInfo.getRelativePath());
        fileMetadata.setTags(tags);
        fileMetadata.setCreateTime(System.currentTimeMillis());
        fileMetadata.setLastOpenTime(System.currentTimeMillis());
        id2FileMap.put(fileMetadata.getId(), fileMetadata);
        fileMetadataList.add(fileMetadata);
        log.debug("添加文件【{}】", fileMetadata);
        return fileMetadata;
    }

    public FileMetadata getFileById(long fileId) throws TbpException {
        final FileMetadata fileMetadata = id2FileMap.get(fileId);
        if (fileMetadata == null) {
            throw new TbpException("未找到文件信息");
        }
        return fileMetadata;
    }

    public List<FileMetadata> listAllFile() {
        return fileMetadataList;
    }

    public long getNextFileId() {
        return nextFileId;
    }
}
