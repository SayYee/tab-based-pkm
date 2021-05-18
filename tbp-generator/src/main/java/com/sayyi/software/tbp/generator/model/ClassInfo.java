package com.sayyi.software.tbp.generator.model;

import lombok.Data;

import java.util.List;

@Data
public class ClassInfo {

    private String className;
    private List<FieldInfo> fields;

}
