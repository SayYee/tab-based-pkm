package com.sayyi.software.tbp.db.api.component;

public interface DbHelper {

    /**
     * 获取selector组件
     * @return
     */
    Selector getSelector();

    /**
     * 获取持久化组件
     * @return
     */
    MetadataDb getMetadata();

    /**
     * 获取文件辅助组件
     * @return
     */
    FileHelper getFileHelper();

    /**
     * 获取tree持久化组件
     * @return
     */
    TreeComponent getTreeComponent();
}
