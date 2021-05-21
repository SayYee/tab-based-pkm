package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;
import com.sayyi.software.tbp.core.facade.DefaultPkmFunction;
import com.sayyi.software.tbp.core.facade.PkmFunction;
import lombok.SneakyThrows;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xuchuang
 * @date 2021/5/21
 */
@State(Scope.Benchmark)
public class PerformanceTest {

    PkmFunction pkmFunction;

    int x;

    @Setup
    public void prepare() {
        long startTime = System.currentTimeMillis();
        System.out.println("prepare 方法被调用");

        x = 0;
        String rootPath = TestConstant.ROOT_PATH;

        String storeDir = rootPath + "store";
        String snapDir = rootPath + "snap";
        int treeRetainNum = 10;

        FileManager fileManager = new FileManager(storeDir);
        MetadataFunction metadataFunction = new MetadataManager();
        TagTreeFunction tagTreeManager = new TagTreeManager(snapDir, treeRetainNum);
        DbFunction dbFunction = new FileBasedDbManager(snapDir);
        PkmService pkmService = new PkmServiceImpl(fileManager, metadataFunction, tagTreeManager, dbFunction);
        pkmFunction = new DefaultPkmFunction(pkmService);

        long endTime = System.currentTimeMillis();
        System.out.println("prepare 方法调用结束。共计用时【" + (endTime - startTime) + "】");
    }

    @TearDown
    public void finished() {
        System.out.println("finished x = " + x);
    }

    /**
     * 测试平均使用时间
     */
    @SneakyThrows
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<FileMetadata> test_list_by_tag() {
        x++;
        Set<String> set = Collections.singleton("PkmPerformanceTest-" + x);
        return pkmFunction.listByNameAndTag(set, null);
    }

    @SneakyThrows
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<FileMetadata> test_list_by_name() {
        x++;
        String fileReg = ".+test.+";
        return pkmFunction.listByNameAndTag(null, fileReg);
    }

    @SneakyThrows
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<FileMetadata> test_list_by_tag_name() {
        x++;
        String fileReg = ".+test.+";
        Set<String> set = Collections.singleton("PkmPerformanceTest-" + x);
        return pkmFunction.listByNameAndTag(set, fileReg);
    }

    @SneakyThrows
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public List<TagInfo> test_list_tags() {
        x++;
        Set<String> set = Collections.singleton("PkmPerformanceTest-" + x);
        return pkmFunction.listTags(set);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PerformanceTest.class.getSimpleName())
                .forks(1)
                .threads(1)
                .build();

        new Runner(opt).run();
    }
}
