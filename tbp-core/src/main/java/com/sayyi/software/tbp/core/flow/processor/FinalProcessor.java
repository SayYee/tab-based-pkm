package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.common.flow.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.MetadataFunction;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * 操作元数据，并组装响应的处理器
 * @author SayYi
 */
@Slf4j
public class FinalProcessor implements Processor {

    private final MetadataFunction metadataFunction;

    public FinalProcessor(MetadataFunction metadataFunction) {
        this.metadataFunction = metadataFunction;
    }

    @Override
    public boolean upload(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            FileMetadata fileMetadata = metadataFunction.create(ResourceType.LOCAL, fileBaseInfo);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean copy(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            FileMetadata fileMetadata = metadataFunction.create(ResourceType.LOCAL, fileBaseInfo);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean addUrl(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            FileMetadata fileMetadata = metadataFunction.create(ResourceType.NET, fileBaseInfo);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean rename(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            metadataFunction.rename(fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    private FileBaseInfo parseFileBaseInfo(Request request) throws IOException {
        FileBaseInfo fileBaseInfo = new FileBaseInfo();
        BinaryInputArchive.deserialize(fileBaseInfo, request.getData());
        return fileBaseInfo;
    }

    @Override
    public boolean modifyResourceTag(Request request, Response response) {
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        try {
            BinaryInputArchive.deserialize(modifyTagRequest, request.getData());
            metadataFunction.modifyTag(modifyTagRequest.getId(), modifyTagRequest.getNewTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean addResourceTag(Request request, Response response) {
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        try {
            BinaryInputArchive.deserialize(modifyTagRequest, request.getData());
            metadataFunction.addFileTag(modifyTagRequest.getId(), modifyTagRequest.getNewTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean deleteResourceTag(Request request, Response response) {
        ModifyTagRequest modifyTagRequest = new ModifyTagRequest();
        try {
            BinaryInputArchive.deserialize(modifyTagRequest, request.getData());
            metadataFunction.deleteFileTag(modifyTagRequest.getId(), modifyTagRequest.getNewTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean open(Request request, Response response) {
        OpenRequest openRequest = new OpenRequest();
        try {
            BinaryInputArchive.deserialize(openRequest, request.getData());
            metadataFunction.open(openRequest.getId(), openRequest.getOpenTime());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean delete(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            long id = archive.readLong();
            metadataFunction.delete(id);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean getById(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            long id = archive.readLong();
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean listResources(Request request, Response response) {
        QueryFileRequest queryFileRequest = new QueryFileRequest();
        try {
            BinaryInputArchive.deserialize(queryFileRequest, request.getData());
            List<FileMetadata> fileMetadata = metadataFunction.listResources(queryFileRequest.getFilenameReg(), queryFileRequest.getTags());
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean deleteTag(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            String tag = archive.readString();
            metadataFunction.deleteTag(tag);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean renameTag(Request request, Response response) {
        RenameTagRequest renameTagRequest = new RenameTagRequest();
        try {
            BinaryInputArchive.deserialize(renameTagRequest, request.getData());
            metadataFunction.renameTag(renameTagRequest.getTag(), renameTagRequest.getNewTag());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean listTags(Request request, Response response) {
        QueryTagRequest queryTagRequest = new QueryTagRequest();
        try {
            BinaryInputArchive.deserialize(queryTagRequest, request.getData());
            List<TagInfo> tagInfos = metadataFunction.listTags(queryTagRequest.getTags());
            log.debug("标签查询结果：{}", tagInfos);
            response.setResult(BinaryOutputArchive.serialize(tagInfos));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }
}
