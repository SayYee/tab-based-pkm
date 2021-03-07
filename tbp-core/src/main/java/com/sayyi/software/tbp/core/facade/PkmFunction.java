package com.sayyi.software.tbp.core.facade;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.TbpException;

import java.util.List;
import java.util.Set;

/**
 * pkm系统的基本功能（起名鬼才）
 * @author SayYi
 */
public interface PkmFunction {

    /**
     * 通过流的方式，将文件传输入pkm系统
     * @param filename  文件名称。不能与已有文件名重复
     * @param data    数据内容
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata upload(String filename, byte[] data) throws Exception;

    /**
     * 从本地文件系统，拷贝文件到pkm系统
     * @param filepath  本地文件路径
     * @param tags  文件标签
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata copy(String filepath, Set<String> tags) throws Exception;

    /**
     * 创建文件
     * @param filename  文件名称
     * @param tags  标签
     * @return
     * @throws Exception
     */
    FileMetadata create(String filename, Set<String> tags) throws Exception;

    /**
     * 将url纳入管理
     * @param name
     * @param url
     * @param tags
     * @return
     * @throws Exception
     */
    FileMetadata url(String name, String url, Set<String> tags) throws Exception;

    /**
     * 文件重命名
     * @param fileId    文件id
     * @param newName   文件名称。不能与已有文件名重复
     * @throws TbpException
     */
    void rename(long fileId, String newName) throws Exception;

    /**
     * 添加文件标签
     * @param fileId
     * @param newTags
     * @throws Exception
     */
    void addFileTag(long fileId, Set<String> newTags) throws Exception;

    /**
     * 删除文件标签
     * @param fileId
     * @param newTags
     * @throws Exception
     */
    void deleteFileTag(long fileId, Set<String> newTags) throws Exception;

    /**
     * 修改文件标签
     * @param fileId    文件id
     * @param newTags   新的tag集合
     * @throws TbpException
     */
    void modifyTag(long fileId, Set<String> newTags) throws Exception;

    /**
     * 调用本地关联软件，打开文件
     * @param fileId    文件id
     * @throws TbpException
     */
    void open(long fileId) throws Exception;

    /**
     * 打开文件所在文件夹，并选中文件
     * @param fileId    文件id
     * @throws Exception
     */
    void select(long fileId) throws Exception;

    /**
     * 删除文件。文件会被直接删除，不会出现在回收站。后续也许可以维护回收站的功能
     * @param fileId    文件id
     * @throws TbpException
     */
    void delete(long fileId) throws Exception;

    /**
     * 通过id，获取文件的元数据信息
     * @param fileId    文件id
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata getFileById(long fileId) throws Exception;

    /**
     * 获取最近打开的文件
     * @return  最近打开文件集合
     */
    List<FileMetadata> listRecentOpened() throws Exception;

    /**
     * 通过文件名称、tag集合查询
     * @param tags  tag集合
     * @param filenameReg   文件名称正则表达式
     * @return  文件集合
     */
    List<FileMetadata> listByNameAndTag(Set<String> tags, String filenameReg) throws Exception;

    /**
     * 删除标签
     * @param tagName   标签名称
     * @throws TbpException
     */
    void deleteTag(String tagName) throws Exception;

    /**
     * 重命名标签
     * @param tagName   标签名称
     * @param newName   新的标签名称
     * @throws TbpException
     */
    void renameTag(String tagName, String newName) throws Exception;

    /**
     * 批量修改标签
     * @param tags 原标签集合
     * @param newTags   新标签集合
     * @throws Exception
     */
    void batchModifyTags(Set<String> tags, Set<String> newTags) throws Exception;

    /**
     * 查询标签
     * @param tags
     * @return
     * @throws Exception
     */
    List<TagInfo> listTags(Set<String> tags) throws Exception;

    /**
     * 获取标签图信息
     * @return
     * @throws Exception
     */
    byte[] tagMap() throws Exception;

    /**
     * 获取所有tree id
     * @return
     * @throws Exception
     */
    List<Long> listTreeIds() throws Exception;

    /**
     * 获取当前tree数据
     * @return
     * @throws Exception
     */
    String getCurrentTree() throws Exception;

    /**
     * 获取指定tree数据
     * @param id
     * @return
     * @throws Exception
     */
    String getAssignTree(long id) throws Exception;

    /**
     * 设置tree
     * @throws Exception
     */
    long setTree(String treeStr) throws Exception;
}
