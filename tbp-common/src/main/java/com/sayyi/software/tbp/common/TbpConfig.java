package com.sayyi.software.tbp.common;

/**
 * @author SayYi
 */
public interface TbpConfig {

    /**
     * 获取文件存储路径
     * @return  文件存储路径
     */
    String getStoreDir();

    /**
     * 获取快照存储路径
     * @return  快照存储路径
     */
    String getSnapDir();
}
