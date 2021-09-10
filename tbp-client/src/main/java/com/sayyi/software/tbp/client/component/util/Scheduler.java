package com.sayyi.software.tbp.client.component.util;

import javafx.concurrent.Task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 只有一个工作线程。主要用于可编辑单元格的双击和单击两次事件的区分处理
 */
public class Scheduler {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static ScheduledExecutorService get() {
        return executorService;
    }

    /**
     * 使用Task，延时执行UI操作
     * @param runnable
     * @param delayMills
     * @return
     */
    public static ScheduledFuture<?> delayUIop(Runnable runnable, long delayMills) {
        return get().schedule(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
            @Override
            protected void succeeded() {
                runnable.run();
            }
        }, delayMills, TimeUnit.MILLISECONDS);
    }
}
