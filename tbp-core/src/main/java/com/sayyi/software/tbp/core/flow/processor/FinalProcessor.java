package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.model.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.MetadataFunction;
import com.sayyi.software.tbp.core.TagTreeFunction;
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

    private final TagTreeFunction tagTreeFunction;

    public FinalProcessor(MetadataFunction metadataFunction, TagTreeFunction tagTreeFunction) {
        this.metadataFunction = metadataFunction;
        this.tagTreeFunction = tagTreeFunction;
    }

    @Override
    public boolean upload(Request request, Response response) {
        try {
            FileMetadata fileBaseInfo = parseFileBaseInfo(request);
            log.debug("上传文件【{}】", fileBaseInfo);
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
            FileMetadata fileBaseInfo = parseFileBaseInfo(request);
            log.debug("复制文件【{}】", fileBaseInfo);
            FileMetadata fileMetadata = metadataFunction.create(ResourceType.LOCAL, fileBaseInfo);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean create(Request request, Response response) {
        try {
            FileMetadata fileBaseInfo = parseFileBaseInfo(request);
            log.debug("创建文件【{}】", fileBaseInfo);
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
            FileMetadata fileBaseInfo = parseFileBaseInfo(request);
            log.debug("保存url【{}】", fileBaseInfo);
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
            FileMetadata fileBaseInfo = parseFileBaseInfo(request);
            log.debug("重命名文件【{}】", fileBaseInfo);
            metadataFunction.rename(fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    private FileMetadata parseFileBaseInfo(Request request) throws IOException {
        FileMetadata fileBaseInfo = new FileMetadata();
        BinaryInputArchive.deserialize(fileBaseInfo, request.getData());
        return fileBaseInfo;
    }

    @Override
    public boolean modifyResourceTag(Request request, Response response) {
        FileModifyTags fileModifyTags = new FileModifyTags();
        try {
            BinaryInputArchive.deserialize(fileModifyTags, request.getData());
            log.debug("调整文件标签【{}】", fileModifyTags);
            metadataFunction.modifyTag(fileModifyTags.getFileId(),
                    fileModifyTags.getTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean addResourceTag(Request request, Response response) {
        FileModifyTags fileModifyTags = new FileModifyTags();
        try {
            BinaryInputArchive.deserialize(fileModifyTags, request.getData());
            log.debug("添加文件标签【{}】", fileModifyTags);
            metadataFunction.addFileTag(fileModifyTags.getFileId(),
                    fileModifyTags.getTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean deleteResourceTag(Request request, Response response) {
        FileModifyTags fileModifyTags = new FileModifyTags();
        try {
            BinaryInputArchive.deserialize(fileModifyTags, request.getData());
            log.debug("删除文件标签【{}】", fileModifyTags);
            metadataFunction.deleteFileTag(fileModifyTags.getFileId(),
                    fileModifyTags.getTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean open(Request request, Response response) {
        FileOperate fileOperate = new FileOperate();
        try {
            BinaryInputArchive.deserialize(fileOperate, request.getData());
            log.debug("打开文件【{}】", fileOperate);
            metadataFunction.open(fileOperate.getFileId(), fileOperate.getTime());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean select(Request request, Response response) {
        log.debug("在目录中展示文件");
        return open(request, response);
    }

    @Override
    public boolean delete(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            long id = archive.readLong();
            log.debug("删除文件【{}】", id);
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
            log.debug("通过id获取文件【{}】", id);
            FileMetadata fileMetadata = metadataFunction.getFileById(id);
            response.setResult(BinaryOutputArchive.serialize(fileMetadata));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean listResources(Request request, Response response) {
        QueryFile queryFile = new QueryFile();
        try {
            BinaryInputArchive.deserialize(queryFile, request.getData());
            log.debug("查询文件【{}】", queryFile);
            List<FileMetadata> fileMetadata = metadataFunction.listResources(queryFile.getFilenameReg(),
                    queryFile.getTags());
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
            log.debug("删除标签【{}】", tag);
            metadataFunction.deleteTag(tag);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean renameTag(Request request, Response response) {
        TagRename tagRename = new TagRename();
        try {
            BinaryInputArchive.deserialize(tagRename, request.getData());
            log.debug("重命名标签【{}】", tagRename);
            metadataFunction.renameTag(tagRename.getTag(),
                    tagRename.getNewTag());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean batchModifyTags(Request request, Response response) {
        TagBatchModify tagBatchModify = new TagBatchModify();
        try {
            BinaryInputArchive.deserialize(tagBatchModify, request.getData());
            log.debug("批量修改标签【{}】", tagBatchModify);
            metadataFunction.batchModifyTags(tagBatchModify.getTags(),
                    tagBatchModify.getNewTags());
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean listTags(Request request, Response response) {
        QueryTag queryTag = new QueryTag();
        try {
            BinaryInputArchive.deserialize(queryTag, request.getData());
            log.debug("查询标签【{}】", queryTag);
            List<TagInfo> tagInfos = metadataFunction.listTags(queryTag.getTags());
            response.setResult(BinaryOutputArchive.serialize(tagInfos));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean tagMap(Request request, Response response) {
        log.debug("查询tagmap");
        byte[] bytes = metadataFunction.tagMap();
        response.setResult(bytes);
        return false;
    }

    @Override
    public boolean listTreeIds(Request request, Response response) {
        log.debug("获取tree id 列表");
        try {
            List<Long> ids = tagTreeFunction.listIds();
            TreeIdList treeIdList = new TreeIdList();
            treeIdList.setIds(ids);
            response.setResult(BinaryOutputArchive.serialize(treeIdList));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean getCurrentTree(Request request, Response response) {
        log.debug("获取当前树数据");
        try {
            final String currentTree = tagTreeFunction.getCurrentTree();
            response.setResult(BinaryOutputArchive.serialize(currentTree));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean getAssignTree(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            int id = archive.readInt();
            log.debug("获取指定树信息【{}】", id);
            final String tree = tagTreeFunction.getTree(id);
            response.setResult(BinaryOutputArchive.serialize(tree));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean saveTree(Request request, Response response) {
        log.debug("保存树信息");
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            String treeStr = archive.readString();
            long maxId = tagTreeFunction.setTree(treeStr);
            response.setResult(BinaryOutputArchive.serialize(maxId));
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }
}
