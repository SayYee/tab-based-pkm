package com.sayyi.software.tbp.common.constant;

/**
 * @author SayYi
 */
public class RequestType {

    /** 流上传 */
    public static final int UPLOAD = 1;
    /** 系统文件复制 */
    public static final int COPY = 2;
    /** 创建文件 */
    public static final int CREATE = 16;
    /** 添加网络资源 */
    public static final int ADD_URL = 3;

    /** 资源重命名 */
    public static final int RENAME= 4;
    /** 调整资源tag */
    public static final int MODIFY_RESOURCE_TAG = 5;
    /** 添加资源tag */
    public static final int ADD_RESOURCE_TAG = 13;
    /** 删除资源tag */
    public static final int DELETE_RESOURCE_TAG = 14;
    /** 打开资源 */
    public static final int OPEN = 6;
    /** 删除资源 */
    public static final int DELETE = 7;
    /** 通过id获取资源信息 */
    public static final int GET_BY_ID = 8;
    /** 通过参数查询资源列表 */
    public static final int LIST_RESOURCES = 9;

    /** 删除tag */
    public static final int DELETE_TAG = 10;
    /** 重命名tag */
    public static final int RENAME_TAG = 11;

    /** 查询标签列表 */
    public static final int LIST_TAGS = 12;

    /** 获取tagMap */
    public static final int TAG_MAP = 15;

    /** 获取所有的tree id */
    public static final int LIST_TREE_IDS = 17;
    /** 获取最新的tree数据 */
    public static final int GET_CURRENT_TREE = 18;
    /** 获取指定tree */
    public static final int GET_ASSIGN_TREE = 19;
    /** 保存tree */
    public static final int SAVE_TREE = 20;

    private RequestType() {}
}
