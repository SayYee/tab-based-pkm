package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.flow.FileBaseInfo;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.common.flow.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.FileManager;
import com.sayyi.software.tbp.core.MetadataFunction;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 前置处理器，主要功能：参数校验、系统资源操作
 * @author SayYi
 */
@Slf4j
public class PrepProcessor implements Processor {

    private final FileManager fileManager;

    private final MetadataFunction metadataFunction;

    public PrepProcessor(FileManager fileManager, MetadataFunction metadataFunction) {
        this.fileManager = fileManager;
        this.metadataFunction = metadataFunction;
    }

    @Override
    public boolean upload(Request request, Response response) {
        FileWithData fileWithData = new FileWithData();
        try {
            // 这么序列化、反序列化，我只能说，太快乐了……
            BinaryInputArchive.deserialize(fileWithData, request.getData());

            FileBaseInfo upload = fileManager.upload(fileWithData.getFilename(), fileWithData.getData());

            byte[] serialize = BinaryOutputArchive.serialize(upload);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean copy(Request request, Response response) {
        FileWithPath fileWithPath = new FileWithPath();
        try {
            BinaryInputArchive.deserialize(fileWithPath, request.getData());
            FileBaseInfo copy = fileManager.copy(fileWithPath.getFilepath());
            copy.setTags(fileWithPath.getTags());

            byte[] serialize = BinaryOutputArchive.serialize(copy);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean create(Request request, Response response) {
        FileWithPath fileWithPath = new FileWithPath();
        try {
            BinaryInputArchive.deserialize(fileWithPath, request.getData());
            // 这名字……凑活着来吧
            FileBaseInfo create = fileManager.create(fileWithPath.getFilename());
            create.setTags(fileWithPath.getTags());

            byte[] serialize = BinaryOutputArchive.serialize(create);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean addUrl(Request request, Response response) {
        FileBaseInfo fileBaseInfo = new FileBaseInfo();
        try {
            BinaryInputArchive.deserialize(fileBaseInfo, request.getData());
            // 需要记录一下时间信息
            fileBaseInfo.setModifyTime(System.currentTimeMillis());
            request.setData(BinaryOutputArchive.serialize(fileBaseInfo));
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean rename(Request request, Response response) {
        RenameRequest renameRequest = new RenameRequest();
        try {
            BinaryInputArchive.deserialize(renameRequest, request.getData());

            long id = renameRequest.getId();
            String newName = renameRequest.getNewName();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);

            log.debug("rename from {} to {}", fileMetadata.getFilename(), newName);
            if (fileMetadata.getFilename().equals(newName)) {
                log.info("文件名没有发生变化，不做修改");
                return false;
            }
            FileBaseInfo fileBaseInfo;
            if (ResourceType.LOCAL == fileMetadata.getResourceType()) {
                fileBaseInfo = fileManager.rename(fileMetadata.getResourcePath(), newName);
            } else {
                // 网络资源，只修改名称，不会修改地址
                fileBaseInfo = new FileBaseInfo();
                fileBaseInfo.setFilename(newName);
                fileBaseInfo.setResourcePath(fileMetadata.getResourcePath());
                // 重命名不更新时间
//                fileBaseInfo.setModifyTime(System.currentTimeMillis());
            }
            fileBaseInfo.setFileId(id);
            byte[] serialize = BinaryOutputArchive.serialize(fileBaseInfo);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean modifyResourceTag(Request request, Response response) {
        return true;
    }

    @Override
    public boolean addResourceTag(Request request, Response response) {
        return true;
    }

    @Override
    public boolean deleteResourceTag(Request request, Response response) {
        return true;
    }

    @Override
    public boolean open(Request request, Response response) {
        OpenRequest openRequest = new OpenRequest();
        try {
            BinaryInputArchive.deserialize(openRequest, request.getData());

            long id = openRequest.getId();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            if (ResourceType.LOCAL == fileMetadata.getResourceType()) {
                fileManager.open(fileMetadata.getResourcePath());
            } else {
                fileManager.browse(fileMetadata.getResourcePath());
            }

            openRequest.setOpenTime(System.currentTimeMillis());
            request.setData(BinaryOutputArchive.serialize(openRequest));
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean delete(Request request, Response response) {
        final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
        try {
            long id = archive.readLong();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            if (ResourceType.LOCAL == fileMetadata.getResourceType()) {
                fileManager.delete(fileMetadata.getResourcePath());
            }
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean getById(Request request, Response response) {
        return true;
    }

    @Override
    public boolean listResources(Request request, Response response) {
        return true;
    }

    @Override
    public boolean deleteTag(Request request, Response response) {
        return true;
    }

    @Override
    public boolean renameTag(Request request, Response response) {
        return true;
    }

    @Override
    public boolean listTags(Request request, Response response) {
        return true;
    }

    @Override
    public boolean tagMap(Request request, Response response) {
        return true;
    }
}
