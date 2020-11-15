package com.sayyi.software.tbp.common;

import lombok.Data;

import java.util.Set;

/**
 * 文件元数据
 * @author SayYi
 */
@Data
public class FileMetadata implements Comparable<FileMetadata> {

    /**
     * 文件id
     */
    private long id;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 文件相对路径
     */
    private String relativePath;
    /**
     * 标签集合
     */
    private Set<String> tags;

    /**
     * 创建时间戳
     */
    private long createTime;
    /**
     * 最后一次打开时间戳。
     */
    private long lastOpenTime;

    @Override
    public int compareTo(FileMetadata o) {
        return Long.compare(id, o.id);
    }
}
