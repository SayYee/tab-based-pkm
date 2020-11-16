package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.*;
import com.sayyi.software.tbp.common.action.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sayyi.software.tbp.common.constant.OpType.*;

/**
 * @author SayYi
 */
@Slf4j
public class PkmMain implements PkmFunction {
    private TbpConfig tbpConfig;
    private FileManager fileManager;
    private TagManager tagManager;
    private MetadataManager metadataManager;
    private DbFunction dbFunction;

    private long nextOpId = 1;

    public PkmMain(TbpConfig tbpConfig) {
        this.tbpConfig = tbpConfig;
        fileManager = new FileManager(tbpConfig.getStoreDir());
        tagManager = new TagManager();
        metadataManager = new MetadataManager();
        dbFunction = new FileBasedDbManager(tbpConfig.getSnapDir());
        try {
            recovery();
        } catch (TbpException e) {
            log.error(e.getMessage(), e);
        }
    }

    public MetadataManager getMetadataManager() {
        return metadataManager;
    }

    @Override
    public void recovery() throws TbpException {
        log.info("从本地恢复数据");
        try {
            Snapshot snapshot = dbFunction.loadSnap();
            if (snapshot.getLastOpId() != -1) {
                nextOpId = snapshot.getLastOpId();
                metadataManager.recovery(snapshot);
                tagManager.recovery(snapshot);
            }
            Iterator<ActionInfo> actionInfoIterator = dbFunction.actionIterator();
            while (actionInfoIterator.hasNext()) {
                ActionInfo actionInfo = actionInfoIterator.next();
                if (actionInfo.getOpId() < nextOpId) {
                    continue;
                }
                doAction(actionInfo);
            }

            Snapshot currentSnap = new Snapshot();
            currentSnap.setLastOpId(nextOpId);
            currentSnap.setLastFileId(metadataManager.getNextFileId());
            currentSnap.setFileMetadataList(metadataManager.listAllFile());
            dbFunction.storeSnap(currentSnap);

            dbFunction.cleanOutOfDateFile(nextOpId);
        } catch (IOException e) {
            throw new TbpException(e.getMessage());
        }
        log.info("数据恢复完成");
    }

    /**
     *
     * @param actionInfo
     */
    private void doAction(ActionInfo actionInfo) throws TbpException {
        log.debug("恢复action【{}】", actionInfo);
        // TODO 创建、打开，时间相关的参数，需要处理
        switch (actionInfo.getOpType()) {
            case CREATE:
                CreateAction createAction = (CreateAction) actionInfo.getAction();
                final FileMetadata fileMetadata = metadataManager.initAndSaveFileMetadata(
                        createAction.getFileInfo(), createAction.getTags());
                tagManager.createFile(fileMetadata);
                break;
            case RENAME:
                RenameAction renameAction = (RenameAction) actionInfo.getAction();
                metadataManager.rename(renameAction.getId(), renameAction.getFileInfo());
                break;
            case MODIFY_TAG:
                ModifyTagAction modifyTagAction = (ModifyTagAction) actionInfo.getAction();
                tagManager.modifyTag(getFileById(modifyTagAction.getId()), modifyTagAction.getNewTags());
                break;
            case OPEN:
                OpenAction openAction = (OpenAction) actionInfo.getAction();
                tagManager.openFile(getFileById(openAction.getId()));
                break;
            case DELETE:
                DeleteAction deleteAction = (DeleteAction) actionInfo.getAction();
                tagManager.deleteFile(getFileById(deleteAction.getId()));
                metadataManager.delete(deleteAction.getId());
                break;
            case DELETE_TAG:
                DeleteTagAction deleteTagAction = (DeleteTagAction) actionInfo.getAction();
                tagManager.deleteTag(deleteTagAction.getTag());
                break;
            case RENAME_TAG:
                RenameTagAction renameTagAction = (RenameTagAction) actionInfo.getAction();
                tagManager.renameTag(renameTagAction.getTag(), renameTagAction.getNewTag());
                break;
            default:
                throw new IllegalArgumentException("找不到对应的操作实体类");
        }
        nextOpId++;
    }

    @Override
    public FileMetadata upload(String filename, InputStream in) throws TbpException {
        FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.upload(filename, in);
            dbFunction.storeAction(new ActionInfo(nextOpId, CREATE, new CreateAction(fileInfo, new HashSet<>())));
            nextOpId++;
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
        FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.copy(filepath);
            dbFunction.storeAction(new ActionInfo(nextOpId, CREATE, new CreateAction(fileInfo, tags)));
            nextOpId++;
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
        FileInfo fileInfo = null;
        try {
            fileInfo = fileManager.rename(fileMetadata.getRelativePath(), newName);
            dbFunction.storeAction(new ActionInfo(nextOpId, RENAME, new RenameAction(fileId, fileInfo)));
            nextOpId++;
        } catch (Exception e) {
            throw new TbpException("文件重命名失败", e);
        }
        metadataManager.rename(fileId, fileInfo);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("modify tags. now: {}, target: {}", fileMetadata.getTags(), newTags);
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, MODIFY_TAG, new ModifyTagAction(fileId, newTags)));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
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
            dbFunction.storeAction(new ActionInfo(nextOpId, OPEN, new OpenAction(fileId)));
            nextOpId++;
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
            dbFunction.storeAction(new ActionInfo(nextOpId, DELETE, new DeleteAction(fileId)));
            nextOpId++;
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
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, DELETE_TAG, new DeleteTagAction(tagName)));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        tagManager.deleteTag(tagName);
    }

    @Override
    public void renameTag(String tagName, String newName) throws TbpException {
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, RENAME_TAG, new RenameTagAction(tagName, newName)));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
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
