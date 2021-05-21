package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.core.facade.DefaultPkmFunction;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

/**
 * @author xuchuang
 * @date 2021/5/21
 */
public class PkmPerformanceTest {

    /**
     * 生成微基准测试需要的数据
     * @throws Exception
     */
    @Test
    public void test_generate() throws Exception {
        long startTime = System.currentTimeMillis();

        String rootPath = TestConstant.ROOT_PATH;

        String storeDir = rootPath + "store";
        String snapDir = rootPath + "snap";
        int treeRetainNum = 10;

        FileManager fileManager = new FileManager(storeDir);
        MetadataFunction metadataFunction = new MetadataManager();
        TagTreeFunction tagTreeManager = new TagTreeManager(snapDir, treeRetainNum);
        DbFunction dbFunction = new FileBasedDbManager(snapDir);
        PkmService pkmService = new PkmServiceImpl(fileManager, metadataFunction, tagTreeManager, dbFunction);
        PkmFunction pkmFunction = new DefaultPkmFunction(pkmService);

        for (int i = 0; i < 1000000; i++) {
            String filename = "PkmPerformanceTest-test-" + i + ".md";
            Set<String> set = Collections.singleton("PkmPerformanceTest-" + i);
            pkmFunction.create(filename, set);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("time cost: " + (endTime - startTime));
    }
}
