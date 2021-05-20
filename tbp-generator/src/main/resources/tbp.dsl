module com.sayyi.software.tbp.common.model {

    /** 文件上传 */
    class UploadFile {
        string filename;
        bytes data;
    }

    /** 文件复制 */
    class CopyFile {
        string filepath;
        setstring tags;
    }

    /** 文件创建 */
    class CreateFile {
        string filename;
        setstring tags;
    }

    /** url创建 */
    class CreateUrl {
        string name;
        string url;
        setstring tags;
    }

    /** 文件重命名 */
    class FileRename {
        long fileId;
        string newName;
    }

    /** 文件 标签调整 */
    class FileModifyTags {
        long fileId;
        setstring tags;
    }
    /** 文件操作（打开 这种需要修改最后打开时间的操作） */
    class FileOperate {
        long fileId;
        long time;
    }
    /** 文件查询 */
    class QueryFile {
        string filenameReg;
        setstring tags;
    }
    /** 标签重命名 */
    class TagRename {
        string tag;
        string newTag;
    }
    /** 标签批量修改 */
    class TagBatchModify {
        setstring tags;
        setstring newTags;
    }
    /** 标签绑定文件数量 */
    class TagInfo {
        string tag;
        int fileNum;
    }
    /** 标签查询 */
    class QueryTag {
        setstring tags;
    }
    /** 树id列表 */
    class TreeIdList {
        listlong ids;
    }

}