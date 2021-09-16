package com.sayyi.software.tbp.db.api.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface Selector {

    void setMetadataDb(MetadataDb metadataDb);

    /**
     * 通过id获取
     * @param id
     * @return
     */
    FileMetadata get(long id);

    /**
     * 通过标签和名称查询
     * @param tags
     * @param name
     * @return
     */
    List<FileMetadata> list(Set<String> tags, String name);

    /**
     * 查询未加标签的对象
     * @return
     */
    List<FileMetadata> listUntagged();

    /**
     * 获取标签关联关系
     * @param tags
     * @return
     */
    List<TagInfo> listTags(Set<String> tags);
}
