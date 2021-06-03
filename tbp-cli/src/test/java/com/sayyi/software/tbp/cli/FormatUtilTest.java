package com.sayyi.software.tbp.cli;

import com.sayyi.software.tbp.cli.script.TbpConfigHolder;
import com.sayyi.software.tbp.cli.util.FormatUtil;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.TbpConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class FormatUtilTest {

    @Before
    public void prepare() {
        TbpConfigHolder.set(new TbpConfig() {
            @Override
            public String getStoreDir() {
                return "C:\\Users\\71734\\Desktop\\temp\\pkm-test\\store";
            }

            @Override
            public String getSnapDir() {
                return null;
            }

            @Override
            public int treeRetainNum() {
                return 0;
            }
        });
    }

    @Test
    public void test_fileMetadata() {
        System.out.println(FormatUtil.format(gen()));
    }

    private FileMetadata gen() {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(11223421L);
        fileMetadata.setFilename("00-资料-para知识库管理方法.html");
        fileMetadata.setResourcePath(new String[]{"2021-05", "00-资料-para知识库管理方法.html"});
        fileMetadata.setResourceType(1);
        Set<String> tags = new HashSet<>();
        tags.add("pkm");
        tags.add("test");
        tags.add("知识库");
        tags.add("方法论");
        tags.add("理论");
        tags.add("note");
        tags.add("idea");
        fileMetadata.setTags(tags);
        return fileMetadata;
    }
}
