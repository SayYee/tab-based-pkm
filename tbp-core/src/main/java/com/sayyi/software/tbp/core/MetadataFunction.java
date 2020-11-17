package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.action.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
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
     * @param createAction
     * @return
     * @throws TbpException
     */
    FileMetadata create(CreateAction createAction) throws TbpException;

    /**
     * 重命名文件
     * @param renameAction
     * @throws TbpException
     */
    void rename(RenameAction renameAction) throws TbpException;

    /**
     * 修改文件标签
     * @param modifyTagAction
     * @throws TbpException
     */
    void modifyTag(ModifyTagAction modifyTagAction) throws TbpException;

    /**
     * 打开文件
     * @param openAction
     * @throws TbpException
     */
    void open(OpenAction openAction) throws TbpException;

    /**
     * 删除文件
     * @param deleteAction
     * @throws TbpException
     */
    void delete(DeleteAction deleteAction) throws TbpException;

    /**
     * 删除标签
     * @param deleteTagAction
     * @throws TbpException
     */
    void deleteTag(DeleteTagAction deleteTagAction) throws TbpException;

    /**
     * 重命名标签
     * @param renameTagAction
     * @throws TbpException
     */
    void renameTag(RenameTagAction renameTagAction) throws TbpException;

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
     * 获取最近被打开的文件集合
     * @return
     */
    List<FileMetadata> listRecentOpened();

    /**
     * 通过标签搜素文件列表
     * @param tags  目标标签集合
     * @param comparator    排序原则（其实没有必要，前端获取到所有数据，完全可以自己排序，毕竟本地应用，数据量不会太过头吧应该。
     *                      出问题了再说）
     * @return  目标文件集合
     */
    List<FileMetadata> listByTags(Set<String> tags, Comparator<FileMetadata> comparator);

    /**
     * 生成标签gexf文件
     * @param out
     * @throws IOException
     */
    void tagMap(OutputStream out) throws IOException;
}
