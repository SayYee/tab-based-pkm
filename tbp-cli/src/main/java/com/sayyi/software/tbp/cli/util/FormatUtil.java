package com.sayyi.software.tbp.cli.util;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;

import java.io.File;
import java.util.Date;

/**
 * @author xuchuang
 * @date 2021/6/1
 */
public class FormatUtil {

    private static final String FILE_TABLE_CONTENT_FORMAT = "%-5d\t%-30s\t%-40s\t%-50s\t%-11tF%<-9tR\t%-11tF%<-9tR\n";

    private static final String TAG_INFO_FORMAT = "%-30s\t%-5d\n";

    /**
     * 格式化输出 fileMetadata 字符串
     * @param fileMetadata
     * @return
     */
    public static String format(FileMetadata fileMetadata) {
        Object[] params = new Object[6];
        params[0] = fileMetadata.getId();
        params[1] = fileMetadata.getFilename();
        params[2] = String.join(File.separator, fileMetadata.getResourcePath());
        params[3] = String.join(".", fileMetadata.getTags());
        params[4] = new Date(fileMetadata.getCreateTime());
        params[5] = new Date(fileMetadata.getLastOpenTime());
        return String.format(FILE_TABLE_CONTENT_FORMAT, params);
    }

    public static String format(TagInfo tagInfo) {
        Object[] params = new Object[2];
        params[0] = tagInfo.getTag();
        params[1] = tagInfo.getFileNum();
        return String.format(TAG_INFO_FORMAT, params);
    }
}
