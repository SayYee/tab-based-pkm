package com.sayyi.software.tbp.generator.feign.model;

import lombok.Data;

import java.util.List;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
@Data
public class MethodInfo {

    private String returnType;
    private String methodName;
    private List<FieldInfo> fieldInfos;
    private List<String> exceptions;
}
