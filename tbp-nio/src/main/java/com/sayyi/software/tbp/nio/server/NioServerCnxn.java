package com.sayyi.software.tbp.nio.server;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.core.PkmService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author SayYi
 */
@Slf4j
public class NioServerCnxn {

    private final PkmService pkmService;

    private final SelectionKey key;
    private final SocketChannel channel;

    private final ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    private ByteBuffer dataBuffer = lengthBuffer;
    private ByteBuffer responseBuffer;

    public NioServerCnxn(SelectionKey key, SocketChannel channel, PkmService pkmService) {
        this.key = key;
        this.channel = channel;
        this.pkmService = pkmService;
    }

    public void doIo() {
        try {
            if (key.isReadable()) {
                doRead();
            }
            if (key.isWritable()) {
                doWrite();
            }
        } catch (Exception e) {
            log.warn("doIO 异常", e);
            key.channel();
            try {
                channel.close();
            } catch (IOException exception) {
                log.warn("关闭异常连接失败", e);
            }
        }
    }

    private void doRead() throws IOException, InterruptedException {
        log.debug("do read 被触发");
        int rc = channel.read(dataBuffer);
        // 处理客户端主动断开连接的情况
        if (rc < 0) {
            throw new IOException("连接断开");
        }
        if (dataBuffer.hasRemaining()) {
            return;
        }
        if (dataBuffer == lengthBuffer) {
            lengthBuffer.rewind();
            int dataSize = lengthBuffer.getInt();
            log.debug("收到请求，长度为【{}】", dataSize);
            lengthBuffer.clear();
            dataBuffer = ByteBuffer.allocate(dataSize);
            doRead();
            return;
        }
        processRequest(dataBuffer.array());

        dataBuffer = lengthBuffer;
        log.debug("请求处理完成，重新赋值");
    }

    private void processRequest(byte[] data) throws IOException, InterruptedException {
        log.debug("receive data, data = {}", data);
        if (data == null || data.length == 0) {
            return;
        }
        Request request = new Request();
        BinaryInputArchive.deserialize(request, data);
        log.debug("收到请求，类型为【{}】", request.getOpType());

        Response response = new Response();
        pkmService.deal(request, response);
        response.waitForFinish();

        byte[] bytes = BinaryOutputArchive.serialize(response);
        responseBuffer = ByteBuffer.allocate(4 + bytes.length);
        responseBuffer.putInt(bytes.length);
        responseBuffer.put(bytes);
        responseBuffer.rewind();

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void doWrite() throws IOException {
        channel.write(responseBuffer);
        if (responseBuffer.hasRemaining()) {
            return;
        }
        key.interestOps(SelectionKey.OP_READ);
    }
}
