package com.sayyi.software.tbp.nio.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 建立连接专用线程。感觉没有必要。但是没有关系
 * @author SayYi
 */
@Slf4j
public class AcceptorThread extends Thread {

    private final SelectorThread selectorThread;
    private final int port;

    public AcceptorThread(int port, SelectorThread selectorThread) {
        this.port = port;
        this.selectorThread = selectorThread;
    }

    @Override
    public void run() {
        ServerSocketChannel serverChannel = null;
        Selector selector = null;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(port), 100);
            log.info("在端口【{}】启动监听", port);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                final Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isValid() && key.isAcceptable()) {
                        doAccept(key);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            log.error("accept 线程异常", e);
            if (serverChannel != null) {
                try {
                    serverChannel.close();
                } catch (IOException exception) {
                    log.error("关闭 serverChannel 异常", exception);
                }
            }
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException exception) {
                    log.error("关闭 selector 异常", exception);
                }
            }
        }
    }

    private void doAccept(SelectionKey key) {
        log.debug("收到连接请求");
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel accept = null;
        try {
            accept = channel.accept();
            accept.configureBlocking(false);
            selectorThread.register(accept);
        } catch (IOException e) {
            log.error("连接建立失败", e);
            if (accept != null) {
                try {
                    accept.close();
                } catch (IOException exception) {
                    log.error("关闭异常的客户端连接失败", exception);
                }
            }
        }
    }
}
