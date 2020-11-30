package com.sayyi.software.tbp.nio.client;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.MetadataManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author SayYi
 */
@Slf4j
public class PkmMain implements PkmFunction {

    private final TbpClient client;

    public PkmMain(TbpClient tbpClient) {
        this.client = tbpClient;
    }

    @Override
    public FileMetadata upload(String filename, byte[] data) throws Exception {
        FileWithData fileWithData = new FileWithData();
        fileWithData.setFilename(filename);
        fileWithData.setData(data);
        byte[] serialize = BinaryOutputArchive.serialize(fileWithData);

        Response response = process(RequestType.UPLOAD, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public FileMetadata copy(String filepath, Set<String> tags) throws Exception {
        FileWithPath fileWithPath = new FileWithPath();
        fileWithPath.setFilepath(filepath);
        fileWithPath.setTags(tags == null ? new HashSet<>() : tags);
        byte[] serialize = BinaryOutputArchive.serialize(fileWithPath);

        Response response = process(RequestType.COPY, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public FileMetadata url(String name, String url, Set<String> tags) throws Exception {
        FileBaseInfo fileBaseInfo = new FileBaseInfo();
        fileBaseInfo.setFilename(name);
        fileBaseInfo.setResourcePath(url);
        fileBaseInfo.setTags(tags == null ? new HashSet<>() : tags);

        byte[] serialize = BinaryOutputArchive.serialize(fileBaseInfo);

        Response response = process(RequestType.ADD_URL, serialize);

        FileMetadata fileMetadata = new FileMetadata();
        BinaryInputArchive.deserialize(fileMetadata, response.getResult());
        return fileMetadata;
    }

    @Override
    public void rename(long fileId, String newName) throws Exception {
        RenameRequest renameRequest = new RenameRequest();
        renameRequest.setId(fileId);
        renameRequest.setNewName(newName);
        byte[] serialize = BinaryOutputArchive.serialize(renameRequest);

        process(RequestType.RENAME, serialize);
    }

    @Override
    public void addFileTag(long fileId, Set<String> newTags) throws Exception {
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        modifyTagRequest.setId(fileId);
        modifyTagRequest.setNewTags(newTags == null ? new HashSet<>() : newTags);
        byte[] serialize = BinaryOutputArchive.serialize(modifyTagRequest);

        process(RequestType.ADD_RESOURCE_TAG, serialize);
    }

    @Override
    public void deleteFileTag(long fileId, Set<String> newTags) throws Exception {
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        modifyTagRequest.setId(fileId);
        modifyTagRequest.setNewTags(newTags == null ? new HashSet<>() : newTags);
        byte[] serialize = BinaryOutputArchive.serialize(modifyTagRequest);

        process(RequestType.DELETE_RESOURCE_TAG, serialize);
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) throws Exception{
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        modifyTagRequest.setId(fileId);
        modifyTagRequest.setNewTags(newTags == null ? new HashSet<>() : newTags);
        byte[] serialize = BinaryOutputArchive.serialize(modifyTagRequest);

        process(RequestType.MODIFY_RESOURCE_TAG, serialize);
    }

    @Override
    public void open(long fileId) throws Exception {
        OpenRequest openRequest = new OpenRequest();
        openRequest.setId(fileId);
        byte[] serialize = BinaryOutputArchive.serialize(openRequest);
        process(RequestType.OPEN, serialize);
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
        QueryFileRequest queryFileRequest = new QueryFileRequest();
        queryFileRequest.setFilenameReg(filenameReg);
        queryFileRequest.setTags(tags == null ? new HashSet<>() : tags);
        byte[] serialize = BinaryOutputArchive.serialize(queryFileRequest);

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
        RenameTagRequest renameTagRequest = new RenameTagRequest();
        renameTagRequest.setTag(tagName);
        renameTagRequest.setNewTag(newName);
        final byte[] serialize = BinaryOutputArchive.serialize(renameTagRequest);
        process(RequestType.RENAME_TAG, serialize);
    }

    @Override
    public List<TagInfo> listTags(Set<String> tags) throws Exception {
        QueryTagRequest queryTagRequest = new QueryTagRequest();
        queryTagRequest.setTags(tags == null ? new HashSet<>() : tags);
        final byte[] serialize = BinaryOutputArchive.serialize(queryTagRequest);
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

    private Response process(int requestType, byte[] data) {
        Request request = new Request();
        request.setOpType(requestType);
        request.setData(data);
        Response response = client.postRequest(request);

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
}
