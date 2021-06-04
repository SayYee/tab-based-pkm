package com.sayyi.software.tbp.generator.feign.model;

import lombok.Data;

import java.util.List;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
@Data
public class FunctionInfo {

    private String functionName;

    private List<MethodInfo> methodInfos;
}
