package com.sayyi.software.tbp.db.api.listener;

/**
 * 支持的事件类型：直接就按照方法来不就行了？
 * - 新增、修改（名称、路径、标签、最后时间）、删除
 * - 标签重命名
 * 使用二进制，可以将多个事件整合发布（修改事件）
 */
public class TbpEventType {

    /** 元数据添加事件 */
    public static final int ADD = 1;
    public static final int MODIFY_NAME = 1 << 1;
    public static final int MODIFY_PATH = 1 << 2;
    public static final int MODIFY_TAGS = 1 << 3;
    public static final int MODIFY_UPDATE_TIME = 1 << 4;
    public static final int REMOVE = 1 << 5;

}
