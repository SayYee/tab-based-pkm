package com.sayyi.software.tbp.core;

import java.util.List;

public interface TagTreeFunction {

    /**
     * 获取所有的树文件id
     * @return
     */
    List<Long> listIds();

    /**
     * 获取当前树数据
     * @return
     */
    String getCurrentTree();

    /**
     * 获取id对应的tree数据
     * @param id
     * @return
     * @throws TbpException id对应的tree不存在
     */
    String getTree(long id);

    /**
     * 设置树数据
     * @param treeStr
     * @return
     */
    long setTree(String treeStr);
}
