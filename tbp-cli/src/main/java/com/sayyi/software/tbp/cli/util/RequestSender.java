package com.sayyi.software.tbp.cli.util;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import com.sayyi.software.tbp.nio.client.TbpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;

/**
 * @author SayYi
 */
@Slf4j
public class RequestSender {

    private TbpClient client;

    public RequestSender(TbpClient tbpClient) {
        this.client = tbpClient;
    }

    public void sendForRecord(int requestType, Record requestData, Record responseData) throws IOException, InterruptedException {
        Response response = send(requestType, requestData);
        if (responseData != null) {
            BinaryInputArchive.deserialize(responseData, response.getResult());
        }
    }

    public void sendForRecord(int requestType, long num, Record responseData) throws IOException, InterruptedException {
        Response response = send(requestType, BinaryOutputArchive.serialize(num));
        if (responseData != null) {
            BinaryInputArchive.deserialize(responseData, response.getResult());
        }
    }

    public void sendForRecord(int requestType, String str, Record responseData) throws IOException, InterruptedException {
        Response response = send(requestType, BinaryOutputArchive.serialize(str));
        if (responseData != null) {
            BinaryInputArchive.deserialize(responseData, response.getResult());
        }
    }

    public <B extends Record> void sendForCollection(int requestType, Record requestData, Class<B> clazz, Collection<B> collection) throws IOException, InterruptedException, InstantiationException, IllegalAccessException {
        Response response = send(requestType, requestData);
        BinaryInputArchive.deserialize(collection, clazz, response.getResult());
    }

    private Response send (int requestType, Record requestData) throws IOException, InterruptedException {
        return send(requestType, BinaryOutputArchive.serialize(requestData));
    }

    private Response send (int requestType, byte[] data) throws IOException, InterruptedException {
        Request request = new Request();
        request.setOpType(requestType);
        request.setData(data);
        Response response = client.postRequest(request);
        log.debug("发送请求，等待响应");
        response.waitForFinish();
        if (response.isError()) {
            throw new RuntimeException("请求失败，异常信息：" + response.getErrorMsg());
        }
        return response;
    }
}
