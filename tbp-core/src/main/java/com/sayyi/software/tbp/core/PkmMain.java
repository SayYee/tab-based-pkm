package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author SayYi
 */
@Slf4j
public class PkmMain implements PkmFunction {
    private TbpConfig tbpConfig;
    private FileManager fileManager;
    private TagManager tagManager;
    private MetadataManager metadataManager;

    public PkmMain(TbpConfig tbpConfig) {
        this.tbpConfig = tbpConfig;
        fileManager = new FileManager(tbpConfig.getStoreDir());
        tagManager = new TagManager();
        metadataManager = new MetadataManager();
    }

    public MetadataManager getMetadataManager() {
        return metadataManager;
    }

    @Override
    public FileMetadata upload(String filename, InputStream in) throws TbpException {
        FileManager.FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.upload(filename, in);
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        FileMetadata fileMetadata = metadataManager.initAndSaveFileMetadata(fileInfo, new HashSet<>());
        tagManager.createFile(fileMetadata);
        return fileMetadata;
    }

    @Override
    public FileMetadata copy(String filepath, String filename, Set<String> tags) throws TbpException {
        if (tags == null || tags.isEmpty()) {
            tags = new HashSet<>();
        }
        FileManager.FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.copy(filepath);
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        FileMetadata fileMetadata = metadataManager.initAndSaveFileMetadata(fileInfo, tags);
        tagManager.createFile(fileMetadata);
        return fileMetadata;
    }

    @Override
    public void rename(long fileId, String newName) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("rename from {} to {}", fileMetadata.getFilename(), newName);
        if (fileMetadata.getFilename().equals(newName)) {
            log.info("文件名没有发生变化，不做修改");
            return;
        }
        FileManager.FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.rename(fileMetadata.getRelativePath(), newName);
        } catch (Exception e) {
            throw new TbpException("文件重命名失败", e);
        }
        metadataManager.rename(fileId, fileInfo);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("modify tags. now: {}, target: {}", fileMetadata.getTags(), newTags);
        tagManager.modifyTag(fileMetadata, newTags);
    }

    @Override
    public void open(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("打开文件【{}】", fileMetadata);
        tagManager.openFile(fileMetadata);
        fileMetadata.setLastOpenTime(System.currentTimeMillis());
        try {
            fileManager.open(fileMetadata.getRelativePath());
        } catch (Exception e) {
            throw new TbpException("打开文件【" + fileId + "】失败", e);
        }
    }

    @Override
    public void delete(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        try {
            fileManager.delete(fileMetadata.getRelativePath());
            log.info("删除文件【{}】", fileMetadata.getRelativePath());
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        tagManager.deleteFile(fileMetadata);
        metadataManager.delete(fileId);
    }

    @Override
    public FileMetadata getFileById(long fileId) throws TbpException {
        return metadataManager.getFileById(fileId);
    }

    @Override
    public List<FileMetadata> listRecentOpened() {
        return tagManager.listRecentOpened();
    }

    private final Comparator<FileMetadata> defaultComparator = Comparator.comparingLong(FileMetadata::getId);

    @Override
    public List<FileMetadata> listByNameAndTag(Set<String> tags, String filenameReg) {
        boolean noTags = tags == null || tags.isEmpty();
        boolean noFilename = filenameReg == null || "".equals(filenameReg.trim());

        List<FileMetadata> fileList = null;
        if (noTags) {
            fileList = metadataManager.listAllFile();
        } else {
            fileList = tagManager.listByTags(tags, defaultComparator);
        }
        if (noFilename) {
            return fileList;
        }
        return fileList.stream()
                .filter(file -> Pattern.matches(filenameReg, file.getFilename()))
                .sorted(defaultComparator)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTag(String tagName) throws TbpException {
        tagManager.deleteTag(tagName);
    }

    @Override
    public void renameTag(String tagName, String newName) throws TbpException {
        tagManager.renameTag(tagName, newName);
    }

    @Override
    public void tagMap(OutputStream out) throws TbpException {
        try {
            tagManager.tagMap(out);
        } catch (IOException e) {
            throw new TbpException("生成tagMap失败", e);
        }
    }
}
