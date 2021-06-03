package com.sayyi.software.tbp.cli.util;

import com.sayyi.software.tbp.cli.script.TbpConfigHolder;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;

import java.io.File;

/**
 * @author xuchuang
 * @date 2021/6/1
 */
public class FormatUtil {

    private static final String FILE_TABLE_CONTENT_FORMAT = "%-5d\t%-30s\t%-40s\t%-50s\n";

    private static final String TAG_INFO_FORMAT = "%-30s\t%-5d\n";

    /**
     * 格式化输出 fileMetadata 字符串
     * @param fileMetadata
     * @return
     */
    public static String format(FileMetadata fileMetadata) {
        // 在命令行展示，我觉得只保留必要信息就可以。
        // 需要文件的真实路径，这样和其他软件联动变成可能。
        Object[] params = new Object[4];
        params[0] = fileMetadata.getId();
        params[1] = fileMetadata.getFilename();
        params[2] = String.join(".", fileMetadata.getTags());
        params[3] = TbpConfigHolder.get().getStoreDir() + File.separator + String.join(File.separator, fileMetadata.getResourcePath());
        return String.format(FILE_TABLE_CONTENT_FORMAT, params);
    }

    public static String format(TagInfo tagInfo) {
        Object[] params = new Object[2];
        params[0] = tagInfo.getTag();
        params[1] = tagInfo.getFileNum();
        return String.format(TAG_INFO_FORMAT, params);
    }
}
