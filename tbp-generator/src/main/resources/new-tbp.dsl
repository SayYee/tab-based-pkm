module com.sayyi.software.tbp.common.model {
    /** 删除操作 */
    class DeleteOp {
        long id;
    }
    /** 更新标签 */
    class UpdateTagsOp {
        setstring oldTags;
        setstring newTags;
    }
    /** 标签绑定文件数量 */
    class TagInfo {
        string tag;
        int fileNum;
    }
}