package com.sayyi.software.tbp.nio.client;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author SayYi
 */
@Slf4j
public class TbpClient extends Thread {

    private final int port;

    private final Selector selector;
    private final SocketChannel channel;
    private final SelectionKey key;
    private volatile boolean isRunning;

    public TbpClient(int port) throws IOException {
        this.port = port;
        this.channel = SocketChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.connect(new InetSocketAddress(port));
        key = channel.register(selector, SelectionKey.OP_CONNECT);
        isRunning = true;
    }

    /**
     * 关闭client
     */
    public void shutdown() {
        isRunning = false;
        this.interrupt();
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                selector.select();
                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
                    if (key.isValid()) {
                        if (key.isConnectable()) {
                            doConnect();
                        }
                        if (key.isWritable()) {
                            doWrite();
                        }
                        if (key.isReadable()) {
                            doRead();
                        }
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                log.debug("线程中断", e);
            } else {
                log.error("请求处理异常，断开连接", e);
            }
            // 这个设置成false，不再继续接收请求了
            isRunning = false;
        }
        // 关闭连接
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException ignored) {
            }
        }

        // 处理响应信息
        if (response != null) {
            response.setError(true);
            response.setErrorMsg("连接关闭");
            response.markFinished();
        }
        Object[] poll = requestPairQueue.poll();
        while (poll != null) {
            Response response = (Response) poll[1];
            response.setError(true);
            response.setErrorMsg("连接关闭");
            response.markFinished();
            poll = requestPairQueue.poll();
        }
    }

    private void doConnect() throws IOException {
        if (channel.isConnectionPending()) {
            if (channel.finishConnect()) {
                key.interestOps(SelectionKey.OP_WRITE);
                log.debug("连接建立成功");
            }
        }
    }

    /**
     * 发送请求
     * @param request   请求
     * @return 响应。
     */
    public Response postRequest(Request request) {
        if (!isRunning) {
            throw new RuntimeException("client 已关闭");
        }
        log.debug("存入请求【{}】", request.getOpType());
        Response response = new Response();
        requestPairQueue.offer(new Object[]{request, response});
        return response;
    }

    private final LinkedBlockingQueue<Object[]> requestPairQueue = new LinkedBlockingQueue<>();
    private Response response;
    private ByteBuffer writeBuffer;
    private final ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    private ByteBuffer readBuffer = lengthBuffer;

    private void doWrite() throws InterruptedException, IOException {
        if (writeBuffer == null) {
            Object[] take = requestPairQueue.take();
            Request request = (Request) take[0];
            response = (Response) take[1];
            byte[] serialize = BinaryOutputArchive.serialize(request);
            log.debug("写入请求，长度为【{}】，response为【{}】", serialize.length, response);
            writeBuffer = ByteBuffer.allocate(4 + serialize.length);
            writeBuffer.putInt(serialize.length);
            writeBuffer.put(serialize);
            writeBuffer.rewind();
            log.debug("请求数据为：{}", writeBuffer.array());
        }
        channel.write(writeBuffer);
        if (writeBuffer.hasRemaining()) {
            return;
        }
        log.debug("请求写入完成");
        writeBuffer = null;

        key.interestOps(SelectionKey.OP_READ);
    }

    private void doRead() throws IOException {
        int rc = channel.read(readBuffer);
        if (rc < 0) {
            throw new IOException("连接断开");
        }
        if (readBuffer.hasRemaining()) {
            return;
        }
        if (readBuffer == lengthBuffer) {
            lengthBuffer.rewind();
            int size = lengthBuffer.getInt();
            lengthBuffer.clear();
            readBuffer = ByteBuffer.allocate(size);
            doRead();
            return;
        }
        log.debug("response info:{}", response);
        BinaryInputArchive.deserialize(response, readBuffer.array());
        response.markFinished();
        response = null;

        readBuffer = lengthBuffer;
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
