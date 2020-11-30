package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author SayYi
 */
@Slf4j
public class PkmMain implements PkmFunction {

    private final MetadataFunction metadataManager;
    private final PkmService pkmService;

    public PkmMain(MetadataFunction metadataManager, PkmService pkmService) {
        this.metadataManager = metadataManager;
        this.pkmService = pkmService;
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
        fileWithPath.setTags(tags);
        byte[] serialize = BinaryOutputArchive.serialize(fileWithPath);

        Response response = process(RequestType.COPY, serialize);

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
    public void modifyTag(long fileId, Set<String> newTags) throws Exception{
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        modifyTagRequest.setId(fileId);
        modifyTagRequest.setNewTags(newTags);
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
        queryFileRequest.setTags(tags);
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
    public void tagMap(OutputStream out) throws Exception {
        try {
            metadataManager.tagMap(out);
        } catch (IOException e) {
            throw new TbpException("生成tagMap失败", e);
        }
    }

    private Response process(int requestType, byte[] data) {
        Request request = new Request();
        request.setOpType(requestType);
        request.setData(data);
        Response response = new Response();
        pkmService.deal(request, response);

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
