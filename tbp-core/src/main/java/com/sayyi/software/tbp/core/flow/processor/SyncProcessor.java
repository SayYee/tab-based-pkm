package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.DbFunction;

import java.io.IOException;

/**
 * 负责请求的持久化
 * @author SayYi
 */
public class SyncProcessor implements Processor {

    private final DbFunction dbFunction;

    public SyncProcessor(DbFunction dbFunction) {
        this.dbFunction = dbFunction;
    }

    @Override
    public boolean upload(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean copy(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean addUrl(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean rename(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean modifyResourceTag(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean addResourceTag(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean deleteResourceTag(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean open(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean delete(Request request, Response response) {
        store(request);
        return true;
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
        store(request);
        return true;
    }

    @Override
    public boolean renameTag(Request request, Response response) {
        store(request);
        return true;
    }

    @Override
    public boolean listTags(Request request, Response response) {
        return true;
    }

    private void store(Request request) {
        try {
            dbFunction.storeRequest(request);
        } catch (IOException e) {
            throw new TbpException("持久化请求失败【" + request.getOpType() + "-" + request.getOpId() + "】", e);
        }
    }
}
