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
    private final FileManager fileManager;
    private final MetadataFunction metadataManager;
    private final DbFunction dbFunction;

    private long nextOpId = 1;

    public PkmMain(FileManager fileManager, MetadataFunction metadataManager, DbFunction dbFunction) {
        this.fileManager = fileManager;
        this.metadataManager = metadataManager;
        this.dbFunction = dbFunction;
        try {
            recovery();
        } catch (TbpException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void recovery() throws TbpException {
        log.info("从本地恢复数据");
        long currentTime = System.currentTimeMillis();
        try {
            Snapshot snapshot = dbFunction.loadSnap();
            if (snapshot.getLastOpId() != -1) {
                nextOpId = snapshot.getLastOpId();
                metadataManager.recovery(snapshot);
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
        log.info("数据恢复完成，耗时【{}ms】", System.currentTimeMillis() - currentTime);
    }

    /**
     *
     * @param actionInfo
     */
    private void doAction(ActionInfo actionInfo) throws TbpException {
        log.debug("恢复action【{}】，当前opId【{}】", actionInfo, nextOpId);
        // TODO 创建、打开，时间相关的参数，需要处理
        switch (actionInfo.getOpType()) {
            case CREATE:
                CreateAction createAction = (CreateAction) actionInfo.getAction();
                metadataManager.create(createAction);
                break;
            case RENAME:
                RenameAction renameAction = (RenameAction) actionInfo.getAction();
                metadataManager.rename(renameAction);
                break;
            case MODIFY_TAG:
                ModifyTagAction modifyTagAction = (ModifyTagAction) actionInfo.getAction();
                metadataManager.modifyTag(modifyTagAction);
                break;
            case OPEN:
                OpenAction openAction = (OpenAction) actionInfo.getAction();
                metadataManager.open(openAction);
                break;
            case DELETE:
                DeleteAction deleteAction = (DeleteAction) actionInfo.getAction();
                metadataManager.delete(deleteAction);
                break;
            case DELETE_TAG:
                DeleteTagAction deleteTagAction = (DeleteTagAction) actionInfo.getAction();
                metadataManager.deleteTag(deleteTagAction);
                break;
            case RENAME_TAG:
                RenameTagAction renameTagAction = (RenameTagAction) actionInfo.getAction();
                metadataManager.renameTag(renameTagAction);
                break;
            default:
                throw new IllegalArgumentException("找不到对应的操作实体类");
        }
        nextOpId++;
    }

    @Override
    public FileMetadata upload(String filename, InputStream in) throws TbpException {
        FileInfo fileInfo;
        CreateAction createAction;
        try {
            fileInfo = fileManager.upload(filename, in);
            createAction = new CreateAction(fileInfo, new HashSet<>());
            dbFunction.storeAction(new ActionInfo(nextOpId, CREATE, createAction));
            nextOpId++;
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        return metadataManager.create(createAction);
    }

    @Override
    public FileMetadata copy(String filepath, String filename, Set<String> tags) throws TbpException {
        if (tags == null || tags.isEmpty()) {
            tags = new HashSet<>();
        }
        FileInfo fileInfo;
        CreateAction createAction;
        try {
            fileInfo = fileManager.copy(filepath);
            createAction = new CreateAction(fileInfo, tags);
            dbFunction.storeAction(new ActionInfo(nextOpId, CREATE, createAction));
            nextOpId++;
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        return metadataManager.create(createAction);
    }

    @Override
    public void rename(long fileId, String newName) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("rename from {} to {}", fileMetadata.getFilename(), newName);
        if (fileMetadata.getFilename().equals(newName)) {
            log.info("文件名没有发生变化，不做修改");
            return;
        }
        FileInfo fileInfo;
        RenameAction renameAction;
        try {
            fileInfo = fileManager.rename(fileMetadata.getRelativePath(), newName);
            renameAction = new RenameAction(fileId, fileInfo);
            dbFunction.storeAction(new ActionInfo(nextOpId, RENAME, renameAction));
            nextOpId++;
        } catch (Exception e) {
            throw new TbpException("文件重命名失败", e);
        }
        metadataManager.rename(renameAction);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("modify tags. now: {}, target: {}", fileMetadata.getTags(), newTags);
        ModifyTagAction modifyTagAction = new ModifyTagAction(fileId, newTags);
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, MODIFY_TAG, modifyTagAction));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        metadataManager.modifyTag(modifyTagAction);
    }

    @Override
    public void open(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("打开文件【{}】", fileMetadata);
        long currentTime = System.currentTimeMillis();
        OpenAction openAction = new OpenAction(fileId, currentTime);
        try {
            fileManager.open(fileMetadata.getRelativePath());
            dbFunction.storeAction(new ActionInfo(nextOpId, OPEN, openAction));
            nextOpId++;
        } catch (Exception e) {
            throw new TbpException("打开文件【" + fileId + "】失败", e);
        }
        metadataManager.open(openAction);
    }

    @Override
    public void delete(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        DeleteAction deleteAction = new DeleteAction(fileId);
        try {
            fileManager.delete(fileMetadata.getRelativePath());
            log.info("删除文件【{}】", fileMetadata.getRelativePath());
            dbFunction.storeAction(new ActionInfo(nextOpId, DELETE, deleteAction));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        metadataManager.delete(deleteAction);
    }

    @Override
    public FileMetadata getFileById(long fileId) throws TbpException {
        return metadataManager.getFileById(fileId);
    }

    @Override
    public List<FileMetadata> listRecentOpened() {
        return metadataManager.listRecentOpened();
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
            fileList = metadataManager.listByTags(tags, defaultComparator);
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
        DeleteTagAction deleteTagAction = new DeleteTagAction(tagName);
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, DELETE_TAG, deleteTagAction));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        metadataManager.deleteTag(deleteTagAction);
    }

    @Override
    public void renameTag(String tagName, String newName) throws TbpException {
        RenameTagAction renameTagAction = new RenameTagAction(tagName, newName);
        try {
            dbFunction.storeAction(new ActionInfo(nextOpId, RENAME_TAG, renameTagAction));
            nextOpId++;
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        metadataManager.renameTag(renameTagAction);
    }

    @Override
    public void tagMap(OutputStream out) throws TbpException {
        try {
            metadataManager.tagMap(out);
        } catch (IOException e) {
            throw new TbpException("生成tagMap失败", e);
        }
    }
}
