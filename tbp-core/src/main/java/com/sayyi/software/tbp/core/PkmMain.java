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
                try {
                    // 用户在实际进行某些操作时，可能输入无效的参数，但是日志依然会被记录
                    // 因此需要忽略这些异常，保证后续的行为正常提交
                    doAction(actionInfo);
                } catch (Exception e) {
                    log.warn("恢复行为时出现异常【{}】", e.getMessage());
                }
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
    private Object doAction(ActionInfo actionInfo) throws TbpException {
        log.debug("执行action【{}】，当前opId【{}】", actionInfo, nextOpId);
        // 进入这个方法，说明action已经持久化成功。
        // 此时不管执行action是否成功，opId都应该增加，避免因为执行出错，出现重复的opId
        nextOpId++;
        Object result = null;
        switch (actionInfo.getOpType()) {
            case CREATE:
                CreateAction createAction = (CreateAction) actionInfo.getAction();
                result = metadataManager.create(createAction);
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
        return result;
    }

    @Override
    public FileMetadata upload(String filename, InputStream in) throws TbpException {
        FileInfo fileInfo;
        ActionInfo actionInfo;
        try {
            fileInfo = fileManager.upload(filename, in);
            actionInfo = new ActionInfo(nextOpId, CREATE, new CreateAction(fileInfo, new HashSet<>()));
            dbFunction.storeAction(actionInfo);
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        return (FileMetadata) doAction(actionInfo);
    }

    @Override
    public FileMetadata copy(String filepath, Set<String> tags) throws TbpException {
        log.info("文件复制【filepath={}】", filepath);
        if (tags == null || tags.isEmpty()) {
            tags = new HashSet<>();
        }
        FileInfo fileInfo;
        ActionInfo actionInfo;
        try {
            fileInfo = fileManager.copy(filepath);
            actionInfo = new ActionInfo(nextOpId, CREATE, new CreateAction(fileInfo, tags));
            dbFunction.storeAction(actionInfo);
        } catch (Exception e) {
            throw new TbpException(e.getMessage(), e);
        }
        return (FileMetadata) doAction(actionInfo);
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
        ActionInfo actionInfo;
        try {
            fileInfo = fileManager.rename(fileMetadata.getRelativePath(), newName);
            actionInfo = new ActionInfo(nextOpId, RENAME, new RenameAction(fileId, fileInfo));
            dbFunction.storeAction(actionInfo);
        } catch (Exception e) {
            throw new TbpException("文件重命名失败", e);
        }
        doAction(actionInfo);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("modify tags. now: {}, target: {}", fileMetadata.getTags(), newTags);
        ActionInfo actionInfo;
        try {
            actionInfo = new ActionInfo(nextOpId, MODIFY_TAG, new ModifyTagAction(fileId, newTags));
            dbFunction.storeAction(actionInfo);
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        doAction(actionInfo);
    }

    @Override
    public void open(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        log.debug("打开文件【{}】", fileMetadata);
        long currentTime = System.currentTimeMillis();
        ActionInfo actionInfo;
        try {
            fileManager.open(fileMetadata.getRelativePath());
            actionInfo = new ActionInfo(nextOpId, OPEN, new OpenAction(fileId, currentTime));
            dbFunction.storeAction(actionInfo);
        } catch (Exception e) {
            throw new TbpException("打开文件【" + fileId + "】失败", e);
        }
        doAction(actionInfo);
    }

    @Override
    public void delete(long fileId) throws TbpException {
        FileMetadata fileMetadata = getFileById(fileId);
        ActionInfo actionInfo;
        try {
            fileManager.delete(fileMetadata.getRelativePath());
            log.info("删除文件【{}】", fileMetadata.getRelativePath());
            actionInfo = new ActionInfo(nextOpId, DELETE, new DeleteAction(fileId));
            dbFunction.storeAction(actionInfo);
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        doAction(actionInfo);
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
        ActionInfo actionInfo;
        try {
            actionInfo = new ActionInfo(nextOpId, DELETE_TAG, new DeleteTagAction(tagName));
            dbFunction.storeAction(actionInfo);
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        doAction(actionInfo);
    }

    @Override
    public void renameTag(String tagName, String newName) throws TbpException {
        ActionInfo actionInfo;
        try {
            actionInfo = new ActionInfo(nextOpId, RENAME_TAG, new RenameTagAction(tagName, newName));
            dbFunction.storeAction(actionInfo);
        } catch (IOException e) {
            throw new TbpException(e.getMessage(), e);
        }
        doAction(actionInfo);
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
