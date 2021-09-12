package com.sayyi.software.tbp.db.persistence;

import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.snap.Version;

import java.io.IOException;
import java.util.Iterator;

/**
 * 持久化组件
 * @author SayYi
 */
public interface PersistenceComponent {

    /**
     * 快照信息持久化
     * @param version  快照信息
     * @throws IOException
     */
    void storeSnap(long lastOpId, Version version) throws IOException;

    /**
     * 请求持久化
     * @param request
     * @throws IOException
     */
    void storeRequest(Request request) throws IOException;

    /**
     * 快照信息加载
     * @return  快照信息
     * @throws IOException
     */
    Version loadSnap() throws IOException;

    /**
     * 操作信息迭代器获取
     * @param lastOpId 用于过滤request文件，减少迭代次数，但是不会迭代到执行的action，仍然需要用户自己迭代
     * @return  操作信息迭代器
     * @throws IOException
     */
    Iterator<Request> requestIterator(long lastOpId) throws IOException;

    /**
     * 清理过期的数据文件。opId之前的文件将会被删除
     * @param opId  操作id（清理数据不包含该记录）
     * @throws IOException
     */
    void cleanOutOfDateFile(long opId) throws IOException;
}
