package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.model.*;
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
        UploadFile uploadFile = new UploadFile();
        try {
            // 这么序列化、反序列化，我只能说，太快乐了……
            BinaryInputArchive.deserialize(uploadFile, request.getData());

            FileMetadata upload = fileManager.upload(uploadFile.getFilename(),
                    uploadFile.getData());

            byte[] serialize = BinaryOutputArchive.serialize(upload);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean copy(Request request, Response response) {
        CopyFile copyFile = new CopyFile();
        try {
            BinaryInputArchive.deserialize(copyFile, request.getData());
            FileMetadata copy = fileManager.copy(copyFile.getFilepath());
            copy.setTags(copyFile.getTags());

            byte[] serialize = BinaryOutputArchive.serialize(copy);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean create(Request request, Response response) {
        CreateFile createFile = new CreateFile();
        try {
            BinaryInputArchive.deserialize(createFile, request.getData());
            // 这名字……凑活着来吧
            FileMetadata create = fileManager.create(createFile.getFilename());
            create.setTags(createFile.getTags());

            byte[] serialize = BinaryOutputArchive.serialize(create);
            request.setData(serialize);
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean addUrl(Request request, Response response) {
        CreateUrl createUrl = new CreateUrl();
        try {
            BinaryInputArchive.deserialize(createUrl, request.getData());
            FileMetadata urlFileInfo = fileManager.createUrlFile(createUrl.getName(),
                    createUrl.getUrl());
            urlFileInfo.setTags(createUrl.getTags());

            request.setData(BinaryOutputArchive.serialize(urlFileInfo));
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean rename(Request request, Response response) {
        FileRename fileRename = new FileRename();
        try {
            BinaryInputArchive.deserialize(fileRename, request.getData());

            long id = fileRename.getFileId();
            String newName = fileRename.getNewName();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);

            log.debug("rename from {} to {}", fileMetadata.getFilename(), newName);
            FileMetadata fileBaseInfo;
            if (fileMetadata.getFilename().equals(newName)) {
                log.info("文件名没有发生变化，不做修改");
                return false;
            }
            fileBaseInfo = fileManager.rename(fileMetadata.getResourcePath(), newName);
            fileBaseInfo.setId(id);
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
        FileOperate fileOperate = new FileOperate();
        try {
            BinaryInputArchive.deserialize(fileOperate, request.getData());

            long id = fileOperate.getFileId();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            fileManager.open(fileMetadata.getResourcePath());

            request.setData(BinaryOutputArchive.serialize(fileOperate));
            return true;
        } catch (IOException e) {
            throw new TbpException(e);
        }
    }

    @Override
    public boolean select(Request request, Response response) {
        FileOperate fileOperate = new FileOperate();
        try {
            BinaryInputArchive.deserialize(fileOperate, request.getData());

            long id = fileOperate.getFileId();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            fileManager.select(fileMetadata.getResourcePath());

            request.setData(BinaryOutputArchive.serialize(fileOperate));
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
            fileManager.delete(fileMetadata.getResourcePath());
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
    public boolean batchModifyTags(Request request, Response response) {
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
