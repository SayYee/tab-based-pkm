package com.sayyi.software.tbp.core.tool;

import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.*;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.core.flow.processor.Processor;

import java.io.IOException;

/**
 * 请求内容打印处理器
 */
public class PrintProcessor implements Processor {

    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    @Override
    public boolean upload(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            System.out.println(getMethodName() + ":" + fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean copy(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            System.out.println(getMethodName() + ":" + fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean create(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            System.out.println(getMethodName() + ":" + fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean addUrl(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            System.out.println(getMethodName() + ":" + fileBaseInfo);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean rename(Request request, Response response) {
        try {
            FileBaseInfo fileBaseInfo = parseFileBaseInfo(request);
            System.out.println(getMethodName() + ":" + fileBaseInfo);
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
            System.out.println(getMethodName() + ":" + modifyTagRequest);
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
            System.out.println(getMethodName() + ":" + modifyTagRequest);
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
            System.out.println(getMethodName() + ":" + modifyTagRequest);
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
            System.out.println(getMethodName() + ":" + openRequest);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean select(Request request, Response response) {
        return open(request, response);
    }

    @Override
    public boolean delete(Request request, Response response) {
        try {
            final BinaryInputArchive archive = BinaryInputArchive.getArchive(request.getData());
            long id = archive.readLong();
            System.out.println(getMethodName() + ":" + id);
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
            System.out.println(getMethodName() + ":" + id);
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
            System.out.println(getMethodName() + ":" + queryFileRequest);
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
            System.out.println(getMethodName() + ":" + tag);
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
            System.out.println(getMethodName() + ":" + renameTagRequest);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean batchModifyTags(Request request, Response response) {
        BatchModifyTagsRequest tagsRequest = new BatchModifyTagsRequest();
        try {
            BinaryInputArchive.deserialize(tagsRequest, request.getData());
            System.out.println(getMethodName() + ":" + tagsRequest);
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
            System.out.println(getMethodName() + ":" + queryTagRequest);
        } catch (IOException e) {
            throw new TbpException(e);
        }
        return false;
    }

    @Override
    public boolean tagMap(Request request, Response response) {
        System.out.println(getMethodName());
        return false;
    }

    @Override
    public boolean listTreeIds(Request request, Response response) {
        System.out.println(getMethodName());
        return false;
    }

    @Override
    public boolean getCurrentTree(Request request, Response response) {
        System.out.println(getMethodName());
        return false;
    }

    @Override
    public boolean getAssignTree(Request request, Response response) {
        System.out.println(getMethodName());
        return false;
    }

    @Override
    public boolean saveTree(Request request, Response response) {
        System.out.println(getMethodName());
        return false;
    }
}
