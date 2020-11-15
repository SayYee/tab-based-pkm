package com.sayyi.software.tbp.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 从zookeeper中拷贝的缓存组件
 * @author SayYi
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

    private final int cacheSize;

    public LruCache(int cacheSize) {
        super(cacheSize / 4);
        this.cacheSize = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= cacheSize;
    }
}
