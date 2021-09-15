package com.sayyi.software.tbp.db.api.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.update.DeleteOp;
import com.sayyi.software.tbp.common.model.update.UpdateTagsOp;

/**
 * 这个类，为了方便对请求进行增量持久化存储
 * - 要求方法只能有一个入参
 * - 并且入参必须是Record的子类
 * 用dsl快速生成即可
 *
 * 序列化协议：
 * - 请求id（为了反序列化时，判断是否需要应用该变更请求）
 * - 请求类型（反序列化时，确定参数类型）
 * - 请求体（参数序列化后的信息）
 */
public interface Updater {

    /**
     * 新增元数据
     * @param metadata
     * @return
     */
    @BindType(1)
    FileMetadata insert(FileMetadata metadata);

    /**
     * 修改元数据。支持名称、路径、标签、最后更新时间的调整
     * @param metadata
     * @return
     */
    @BindType(2)
    FileMetadata update(FileMetadata metadata);

    /**
     * 删除指定的元数据
     * @param deleteOp
     * @return
     */
    @BindType(3)
    FileMetadata delete(DeleteOp deleteOp);

    /**
     * 标签修改。删除、重命名等操作均可实现
     * @param updateTagsOp
     */
    @BindType(4)
    void updateTags(UpdateTagsOp updateTagsOp);

}
