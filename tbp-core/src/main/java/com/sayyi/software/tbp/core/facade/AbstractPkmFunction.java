package com.sayyi.software.tbp.core.facade;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.model.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.MetadataManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author SayYi
 */
@Slf4j
public abstract class AbstractPkmFunction implements PkmFunction {

    @Override
    public FileMetadata upload(String filename, byte[] data) throws Exception {
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFilename(filename);
        uploadFile.setData(data);
        byte[] serialize = BinaryOutputArchive.serialize(uploadFile);

        Response response = process(RequestType.UPLOAD, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public FileMetadata copy(String filepath, Set<String> tags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        CopyFile copyFile = new CopyFile();
        copyFile.setFilepath(filepath);
        copyFile.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(copyFile);

        Response response = process(RequestType.COPY, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public FileMetadata create(String filename, Set<String> tags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        CreateFile createFile = new CreateFile();
        createFile.setFilename(filename);
        createFile.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(createFile);

        Response response = process(RequestType.CREATE, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public FileMetadata url(String name, String url, Set<String> tags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        CreateUrl createUrl = new CreateUrl();
        createUrl.setName(name);
        createUrl.setUrl(url);
        createUrl.setTags(tags);

        byte[] serialize = BinaryOutputArchive.serialize(createUrl);

        Response response = process(RequestType.ADD_URL, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public void rename(long fileId, String newName) throws Exception {
        FileRename fileRename = new FileRename();
        fileRename.setFileId(fileId);
        fileRename.setNewName(newName);
        byte[] serialize = BinaryOutputArchive.serialize(fileRename);

        process(RequestType.RENAME, serialize);
    }

    @Override
    public void addFileTag(long fileId, Set<String> tags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        FileModifyTags fileModifyTags = new FileModifyTags();
        fileModifyTags.setFileId(fileId);
        fileModifyTags.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(fileModifyTags);

        process(RequestType.ADD_RESOURCE_TAG, serialize);
    }

    @Override
    public void deleteFileTag(long fileId, Set<String> tags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        FileModifyTags fileModifyTags = new FileModifyTags();
        fileModifyTags.setFileId(fileId);
        fileModifyTags.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(fileModifyTags);

        process(RequestType.DELETE_RESOURCE_TAG, serialize);
    }

    @Override
    public void modifyTag(long fileId, Set<String> tags) throws Exception{
        tags = tags == null ? new HashSet<>() : tags;
        FileModifyTags fileModifyTags = new FileModifyTags();
        fileModifyTags.setFileId(fileId);
        fileModifyTags.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(fileModifyTags);

        process(RequestType.MODIFY_RESOURCE_TAG, serialize);
    }

    @Override
    public void open(long fileId) throws Exception {
        FileOperate fileOperate = new FileOperate();
        fileOperate.setFileId(fileId);
        fileOperate.setTime(System.currentTimeMillis());
        byte[] serialize = BinaryOutputArchive.serialize(fileOperate);
        process(RequestType.OPEN, serialize);
    }

    @Override
    public void select(long fileId) throws Exception {
        FileOperate fileOperate = new FileOperate();
        fileOperate.setFileId(fileId);
        fileOperate.setTime(System.currentTimeMillis());
        byte[] serialize = BinaryOutputArchive.serialize(fileOperate);
        process(RequestType.SELECT, serialize);
    }

    @Override
    public void delete(long fileId) throws Exception {
        byte[] serialize = BinaryOutputArchive.serialize(fileId);
        process(RequestType.DELETE, serialize);
    }

    @Override
    public FileMetadata getFileById(long fileId) throws Exception{
        byte[] serialize = BinaryOutputArchive.serialize(fileId);
        Response response = process(RequestType.GET_BY_ID, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public List<FileMetadata> listRecentOpened() throws Exception {
        Set<String> singleton = Collections.singleton(MetadataManager.RECENT_OPENED_TAG);
        return listByNameAndTag(singleton, null);
    }

    @Override
    public List<FileMetadata> listByNameAndTag(Set<String> tags, String filenameReg) throws Exception {
        QueryFile queryFile = new QueryFile();
        queryFile.setFilenameReg(filenameReg);
        queryFile.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(queryFile);

        Response response = process(RequestType.LIST_RESOURCES, serialize);
        List<FileMetadata> fileMetadataList = new LinkedList<>();
        BinaryInputArchive.deserialize(fileMetadataList, FileMetadata.class, response.getResult());
        return fileMetadataList;
    }

    @Override
    public void deleteTag(String tagName) throws Exception{
        final byte[] serialize = BinaryOutputArchive.serialize(tagName);
        process(RequestType.DELETE_TAG, serialize);
    }

    @Override
    public void renameTag(String tagName, String newName) throws Exception{
        TagRename tagRename = new TagRename();
        tagRename.setTag(tagName);
        tagRename.setNewTag(newName);
        final byte[] serialize = BinaryOutputArchive.serialize(tagRename);
        process(RequestType.RENAME_TAG, serialize);
    }

    @Override
    public void batchModifyTags(Set<String> tags, Set<String> newTags) throws Exception {
        tags = tags == null ? new HashSet<>() : tags;
        newTags = newTags == null ? new HashSet<>() : newTags;

        TagBatchModify tagBatchModify = new TagBatchModify();
        tagBatchModify.setTags(tags);
        tagBatchModify.setNewTags(newTags);
        final byte[] serialize = BinaryOutputArchive.serialize(tagBatchModify);
        process(RequestType.BATCH_MODIFY_TAGS, serialize);
    }

    @Override
    public List<TagInfo> listTags(Set<String> tags) throws Exception {
        QueryTag queryTag = new QueryTag();
        queryTag.setTags(tags);
        final byte[] serialize = BinaryOutputArchive.serialize(queryTag);
        Response response = process(RequestType.LIST_TAGS, serialize);

        List<TagInfo> tagInfos = new LinkedList<>();
        BinaryInputArchive.deserialize(tagInfos, TagInfo.class, response.getResult());
        return tagInfos;
    }

    @Override
    public byte[] tagMap() throws Exception {
        Response response = process(RequestType.TAG_MAP, new byte[0]);
        return response.getResult();
    }

    @Override
    public List<Long> listTreeIds() throws Exception {
        Response response = process(RequestType.LIST_TREE_IDS, new byte[0]);
        TreeIdList treeIdList = new TreeIdList();
        BinaryInputArchive.deserialize(treeIdList, response.getResult());
        return treeIdList.getIds();
    }

    @Override
    public String getCurrentTree() throws Exception {
        Response response = process(RequestType.GET_CURRENT_TREE, new byte[0]);
        final BinaryInputArchive archive = BinaryInputArchive.getArchive(response.getResult());
        return archive.readString();
    }

    @Override
    public String getAssignTree(long id) throws Exception {
        final byte[] bytes = BinaryOutputArchive.serialize(id);
        final Response response = process(RequestType.GET_ASSIGN_TREE, bytes);
        final BinaryInputArchive archive = BinaryInputArchive.getArchive(response.getResult());
        return archive.readString();
    }

    @Override
    public long setTree(String treeStr) throws Exception {
        final byte[] bytes = BinaryOutputArchive.serialize(treeStr);
        final Response response = process(RequestType.SAVE_TREE, bytes);
        final BinaryInputArchive archive = BinaryInputArchive.getArchive(response.getResult());
        return archive.readLong();
    }

    /**
     * 阻塞等待响应返回
     * @param requestType
     * @param data
     * @return
     */
    private Response process(int requestType, byte[] data) {
        Request request = new Request();
        request.setOpType(requestType);
        request.setData(data);
        Response response = process(request);

        try {
            response.waitForFinish();
        } catch (InterruptedException e) {
            log.warn("等待处理结果异常");
            throw new TbpException(e);
        }

        if(response.isError()) {
            throw new TbpException(response.getErrorMsg());
        }
        return response;
    }

    /**
     * 发送请求，异步获取响应对象
     * @param request
     * @return  响应对象
     */
    protected abstract Response process(Request request);
}
