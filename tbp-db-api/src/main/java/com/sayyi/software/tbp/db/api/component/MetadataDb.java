package com.sayyi.software.tbp.db.api.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.db.api.listener.TbpListener;

import java.util.List;

public interface MetadataDb extends Updater {

    void setSelector(Selector selector);
    Selector getSelector();

    /**
     * 获取所有存储的数据。这里返回的直接就是对象的引用。因此要避免手动修改这些对象
     * @return
     */
    List<FileMetadata> listAll();

    long getNextFileId();
    /**
     * 可以为各种事件注册监听
     * @param tbpListener
     */
    void addListener(TbpListener tbpListener);

    void removeListener(TbpListener tbpListener);


}
