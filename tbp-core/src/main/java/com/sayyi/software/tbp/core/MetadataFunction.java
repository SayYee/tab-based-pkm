package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.model.TagInfo;
import com.sayyi.software.tbp.common.TbpException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * 元数据操作
 * @author SayYi
 */
public interface MetadataFunction {

    /**
     * 元数据恢复
     * @param snapshot
     */
    void recovery(Snapshot snapshot);

    /**
     * 创建文件
     * @param resourceType
     * @param fileBaseInfo
     * @return
     */
    FileMetadata create(int resourceType, FileMetadata fileBaseInfo);

    /**
     * 重命名文件
     * @param fileBaseInfo
     */
    void rename(FileMetadata fileBaseInfo);

    /**
     * 修改文件标签。基于添加、删除标签的方法的组合实现
     * @param fileId
     * @param newTags
     */
    void modifyTag(long fileId, Set<String> newTags);

    /**
     * 添加文件标签
     * @param fileId
     * @param addTags
     */
    void addFileTag(long fileId, Set<String> addTags);

    /**
     * 删除文件标签
     * @param fileId
     * @param deleteTags
     */
    void deleteFileTag(long fileId, Set<String> deleteTags);

    /**
     * 打开文件
     * @param fileId
     * @param openTime
     */
    void open(long fileId, long openTime);

    /**
     * 删除文件
     * @param fileId
     */
    void delete(long fileId);


    /**
     * 删除标签
     * @param tag
     */
    void deleteTag(String tag);

    /**
     * 标签重命名
     * @param tag
     * @param newTag
     */
    void renameTag(String tag, String newTag);

    /**
     * 批量修改文件标签
     * @param tags  文件标签
     * @param newTags   新的文件标签
     */
    void batchModifyTags(Set<String> tags, Set<String> newTags);
    /**
     * 通过id，获取文件的元数据信息
     * @param fileId    文件id
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata getFileById(long fileId) throws TbpException;

    /**
     * 获取所有文件
     * @return
     */
    List<FileMetadata> listAllFile();

    /**
     * 获取下一个文件id
     * @return
     */
    long getNextFileId();

    /**
     * 查询资源列表
     * @param filenameReg   资源名称匹配表达式
     * @param tags  标签集合
     * @return
     */
    List<FileMetadata> listResources(String filenameReg, Set<String> tags);

    /**
     * 获取与传入tags关联的标签信息
     * @param tags
     * @return
     */
    List<TagInfo> listTags(Set<String> tags);
    /**
     * 生成标签gexf文件
     * @param out
     * @throws IOException
     */
    void tagMap(OutputStream out) throws IOException;

    /**
     * 生成tagMap
     * @return
     */
    byte[] tagMap();
}
