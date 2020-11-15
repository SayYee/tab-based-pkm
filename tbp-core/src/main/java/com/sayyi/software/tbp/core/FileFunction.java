package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpException;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * 文件管理相关的功能
 * @author SayYi
 */
public interface FileFunction {
    /**
     * 通过流的方式，将文件传输入pkm系统
     * @param filename  文件名称。不能与已有文件名重复
     * @param in    输入流
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata upload(String filename, InputStream in) throws TbpException;

    /**
     * 从本地文件系统，拷贝文件到pkm系统
     * @param filepath  本地文件路径
     * @param filename  文件名称。不能与已有文件名重复
     * @param tags  文件标签
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata copy(String filepath, String filename, Set<String> tags) throws TbpException;

    /**
     * 文件重命名
     * @param fileId    文件id
     * @param newName   文件名称。不能与已有文件名重复
     * @throws TbpException
     */
    void rename(long fileId, String newName) throws TbpException;

    /**
     * 修改文件标签
     * @param fileId    文件id
     * @param newTags   新的tag集合
     * @throws TbpException
     */
    void modifyTag(long fileId, Set<String> newTags) throws TbpException;

    /**
     * 调用本地关联软件，打开文件
     * @param fileId    文件id
     * @throws TbpException
     */
    void open(long fileId) throws TbpException;

    /**
     * 删除文件。文件会被直接删除，不会出现在回收站。后续也许可以维护回收站的功能
     * @param fileId    文件id
     * @throws TbpException
     */
    void delete(long fileId) throws TbpException;

    /**
     * 通过id，获取文件的元数据信息
     * @param fileId    文件id
     * @return  文件元数据
     * @throws TbpException
     */
    FileMetadata getFileById(long fileId) throws TbpException;

    /**
     * 获取最近打开的文件
     * @return  最近打开文件集合
     */
    List<FileMetadata> listRecentOpened();

    /**
     * 通过文件名称、tag集合查询
     * @param tags  tag集合
     * @param filenameReg   文件名称正则表达式
     * @return  文件集合
     */
    List<FileMetadata> listByNameAndTag(Set<String> tags, String filenameReg);

}
