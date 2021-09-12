module com.sayyi.software.tbp.common.model.update {
    /** 删除操作 */
    class DeleteOp {
        long id;
    }
    /** 更新标签 */
    class UpdateTagsOp {
        setstring oldTags;
        setstring newTags;
    }
}