package com.sayyi.software.tbp.nio.server;

import com.sayyi.software.tbp.core.PkmService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 请求处理线程
 * @author SayYi
 */
@Slf4j
public class SelectorThread extends Thread {

    /** 等待进行注册的SokcetChannel */
    private final Queue<SocketChannel> toRegisterChannel = new LinkedBlockingQueue<>();

    /** pkm核心组件 */
    private final PkmService pkmService;

    private final Selector selector;

    public SelectorThread(PkmService pkmService) throws IOException {
        selector = Selector.open();
        this.pkmService = pkmService;
    }

    public void register(SocketChannel socketChannel) {
        toRegisterChannel.offer(socketChannel);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    select();
                    processRegister();
                } catch (Exception e) {
                    log.warn("Ignoring unexpected exception", e);
                }
            }
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                log.warn("ignored exception during selector close.", e);
            }
        }
    }

    private void select() {
        try {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isValid()
                        && (key.isWritable() || key.isReadable())) {
                    NioServerCnxn cnxn = (NioServerCnxn) key.attachment();
                    cnxn.doIo();
                }
                keyIterator.remove();
            }
        } catch (IOException e) {
            log.warn("Ignoring IOException while selecting");
        }
    }

    private void processRegister() {
        SocketChannel socketChannel;
        while ((socketChannel = toRegisterChannel.poll()) != null) {
            SelectionKey key = null;
            try {
                key = socketChannel.register(selector, SelectionKey.OP_READ);
                NioServerCnxn nioServerCnxn = new NioServerCnxn(key, socketChannel, pkmService);
                key.attach(nioServerCnxn);
            } catch (IOException e) {
                log.warn("连接注册异常", e);
                if (key != null) {
                    key.channel();
                }
                if (socketChannel != null) {
                    try {
                        socketChannel.close();
                    } catch (IOException exception) {
                        log.warn("断开连接异常", exception);
                    }
                }
            }
        }

    }
}
