package com.sayyi.software.tbp.common.snap;

import cn.hutool.core.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author xuchuang
 * @date 2021/5/21
 */
@Slf4j
public class SnapshotUtil {

    private int lastVersion;
    private final Map<Integer, Version> versionMap = new HashMap<>();
    private final Map<String, Convertor> convertorMap = new HashMap<>();

    public SnapshotUtil() {
        try {
            initVersionMap();
            initConvertorMap();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initVersionMap() throws InstantiationException, IllegalAccessException {
        Set<Class<?>> versionSet = ClassUtil.scanPackage("com.sayyi.software.tbp.common.snap.model",
                Version.class::isAssignableFrom);
        for (Class<?> aClass : versionSet) {
            if (aClass.isInterface()) {
                continue;
            }
            log.debug("加载version类【{}】", aClass);
            Version version = (Version) aClass.newInstance();
            if (versionMap.put(version.version(), version) != null) {
                throw new RuntimeException("已经加载了相同版本的数据【" + version + "】");
            }
            lastVersion = Math.max(lastVersion, version.version());
        }
    }

    private void initConvertorMap() throws InstantiationException, IllegalAccessException {
        Set<Class<?>> convertorSet = ClassUtil.scanPackage("com.sayyi.software.tbp.common.snap.convert",
                Convertor.class::isAssignableFrom);
        for (Class<?> aClass : convertorSet) {
            if (aClass.isInterface()) {
                continue;
            }
            log.debug("加载转换器【{}】", aClass);
            Convertor convertor = (Convertor) aClass.newInstance();
            convertorMap.put(convertorKey(convertor.support()), convertor);
        }
    }

    /**
     * 根据convertor支持的版本，生成对应的key
     * @param support
     * @return
     */
    private String convertorKey(int... support) {
        return support[0] + "-" + support[1];
    }

    /**
     * 获取最新的版本信息
     * @return
     */
    public int getLastVersion() {
        return lastVersion;
    }

    /**
     * 获取特定版本的version对象。
     * @param version
     * @return
     */
    public Version get(int version) {
        return versionMap.get(version);
    }

    /**
     * 将特定的version转换成目标版本
     * @param source
     * @param targetVersion
     * @return
     */
    public Version convert(Version source, int targetVersion) {
        int sourceVersion = source.version();
        List<Convertor> convertors = new LinkedList<>();
        for (int i = sourceVersion; i < targetVersion; i++) {
            convertors.add(convertorMap.get(convertorKey(sourceVersion, sourceVersion + 1)));
        }
        for (Convertor convertor : convertors) {
            source = convertor.convert(source);
        }
        return source;
    }
}
