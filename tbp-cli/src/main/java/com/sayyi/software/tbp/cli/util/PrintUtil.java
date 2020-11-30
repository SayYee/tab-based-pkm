package com.sayyi.software.tbp.cli.util;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TagInfo;

import java.io.PrintWriter;
import java.util.Date;

/**
 * @author SayYi
 */
public class PrintUtil {

    private static final String FILE_TABLE_TITLE_FORMAT = "%-5s\t%-30s\t%-40s\t%-50s\t%-20s\t%-20s\n";
    private static final Object[] FILE_TABLE_TITLE = {"id", "name", "path", "tags", "create-time", "last-open-time"};
    private static final String FILE_TABLE_CONTENT_FORMAT = "%-5d\t%-30s\t%-40s\t%-50s\t%-11tF%<-9tR\t%-11tF%<-9tR\n";

    /**
     * 打印文件列表
     * @param out
     * @param fileMetadatas
     */
    public static void printFileList(PrintWriter out, FileMetadata... fileMetadatas) {
        out.printf(FILE_TABLE_TITLE_FORMAT, FILE_TABLE_TITLE);

        Object[] params = new Object[6];
        for (FileMetadata fileMetadata : fileMetadatas) {
            params[0] = fileMetadata.getId();
            params[1] = fileMetadata.getFilename();
            params[2] = fileMetadata.getResourcePath();
            params[3] = fileMetadata.getTags();
            params[4] = new Date(fileMetadata.getCreateTime());
            params[5] = new Date(fileMetadata.getLastOpenTime());
            out.printf(FILE_TABLE_CONTENT_FORMAT, params);
        }
    }

    public static void printTagList(PrintWriter out, TagInfo... tagInfos) {
        for (TagInfo tagInfo : tagInfos) {
            out.println(tagInfo.getTag() + "(" + tagInfo.getFileNum() + ")");
        }
    }
}
