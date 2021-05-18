package com.sayyi.software.tbp.generator.model;

import lombok.Data;

import java.util.List;

@Data
public class ModuleInfo {
    private String moduleName;
    private List<ClassInfo> classInfos;
}
