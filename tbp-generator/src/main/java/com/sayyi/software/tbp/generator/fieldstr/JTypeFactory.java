package com.sayyi.software.tbp.generator.fieldstr;

import cn.hutool.core.util.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xuchuang
 * @date 2021/5/18
 */
public class JTypeFactory {

    public static final Map<String, JType> jTypeObjCache = new HashMap<>();

    static {
        // 扫描包下所有的JType类型的class
        Set<Class<?>> classSet = ClassUtil.scanPackage("com.sayyi.software.tbp.generator.fieldstr",
                JType.class::isAssignableFrom);
        for (Class<?> aClass : classSet) {
            if (aClass.isInterface()) {
                continue;
            }
            try {
                // 创建对应的obj，放入map中。
                JType jType = (JType) aClass.newInstance();
                jTypeObjCache.put(jType.getSign(), jType);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据dsl标识获取对应的字段解析器
     * @param sign  dsl标识
     * @return
     */
    public static JType get(String sign) {
        JType jtype = jTypeObjCache.get(sign);
        if (jtype == null) {
            throw new IllegalArgumentException("未知的字段类型[" + sign + "]");
        }
        return jtype;
    }
}
