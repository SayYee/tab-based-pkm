package com.sayyi.software.tbp.core.flow;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.flow.processor.Processor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.util.*;

/**
 * 将多个处理器组合成管道处理请求。不保证并发安全
 * @author SayYi
 */
@Slf4j
public class ProcessorPipeline {

    private final Processor proxyProcessor;

    private final ProcessorHandler processorHandler;

    /** 数据恢复的时候，需要获取最新的opId。这个功能在这里做比较合适 */
    private long lastProcessedOpId = -1;

    private final Map<Integer, Method> methodMap = new HashMap<>();

    public ProcessorPipeline() {
        // 初始化路由
        for (Method declaredMethod : Processor.class.getDeclaredMethods()) {
            final BindType annotation = declaredMethod.getAnnotation(BindType.class);
            int type = annotation.value();
            final Method put = methodMap.put(type, declaredMethod);
            if (put != null) {
                throw new IllegalArgumentException("type【" + type + "】 bound more than one method 【" + put +"， " + declaredMethod + "】");
            }
        }

        // 初始化代理对象
        processorHandler = new ProcessorHandler();
        proxyProcessor = (Processor) Proxy.newProxyInstance(ProcessorPipeline.class.getClassLoader(),
                new Class[]{Processor.class},
                processorHandler);
    }

    public void addFirst(Processor processor) {
        processorHandler.addFirst(processor);
    }

    public void addLast(Processor processor) {
        processorHandler.addLast(processor);
    }

    /**
     * 处理请求
     * @param request   请求
     * @param response  响应
     */
    public void deal(Request request, Response response) {
        log.debug("处理请求【opId={}, opType={}】", request.getOpId(), request.getOpType());
        lastProcessedOpId = request.getOpId();
        int opType = request.getOpType();
        Method method = methodMap.get(opType);
        if (method == null) {
            throw new IllegalArgumentException("未知的请求类型【" + opType + "】");
        }
        try {
            method.invoke(proxyProcessor, request, response);
        } catch (IllegalAccessException e) {
            log.error("方法调用异常", e);
            response.setError(true);
            response.setErrorMsg("方法调用异常");
        } catch (InvocationTargetException e) {
            final Throwable sourceCause = e.getCause();
            log.error("方法处理失败", sourceCause);
            response.setError(true);
            response.setErrorMsg(sourceCause.getMessage());
        }
    }

    public long getLastProcessedOpId() {
        return lastProcessedOpId;
    }

    /**
     * processor代理组件。
     * 对于Processor接口方法的调用，会使用内部的处理链条依次进行处理。
     * 如果调用方法返回false，则不继续向下传递。
     * 没有进行并发的保证
     */
    private static class ProcessorHandler implements InvocationHandler {

        private final LinkedList<Processor> processors = new LinkedList<>();

        private void addFirst(Processor processor) {
            processors.addFirst(processor);
        }

        private void addLast(Processor processor) {
            processors.addLast(processor);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals":
                    return equals(args[0]);
                case "hashCode":
                    return hashCode();
                case "toString":
                    return toString();
                default:
            }
            try {
                long startTime = System.currentTimeMillis();
                final Iterator<Processor> iterator = processors.iterator();
                boolean processResult = true;
                while (iterator.hasNext()) {
                    final Processor next = iterator.next();
                    // 为了外侧能够正常捕获异常，需要在这里调用是，捕获这个invoke抛出的异常
                    processResult = (boolean) method.invoke(next, args);
                    if (!processResult) {
                        break;
                    }
                }
                log.debug("{} method use time {}ms", method.getName(), System.currentTimeMillis() - startTime);
                return processResult;
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProcessorHandler that = (ProcessorHandler) o;
            return Objects.equals(processors, that.processors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(processors);
        }

        @Override
        public String toString() {
            return "ProcessorHandler{" +
                    "processors=" + processors +
                    '}';
        }
    }

}
