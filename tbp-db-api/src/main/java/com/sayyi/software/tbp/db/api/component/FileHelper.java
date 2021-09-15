package com.sayyi.software.tbp.db.api.component;

import com.sayyi.software.tbp.common.FileMetadata;

import java.io.File;

public interface FileHelper {

    /**
     * 通过元数据获取对应的file
     * @param fileMetadata  filename、path字段不能为空
     * @return
     */
    File getFile(FileMetadata fileMetadata);

    /**
     * 请求分配一个默认的存储路径
     * @return
     */
    String[] assignPath();
}
