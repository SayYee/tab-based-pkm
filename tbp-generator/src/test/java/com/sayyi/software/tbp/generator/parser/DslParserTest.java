package com.sayyi.software.tbp.generator.parser;

import com.sayyi.software.tbp.generator.DslReader;
import com.sayyi.software.tbp.generator.model.ClassInfo;
import com.sayyi.software.tbp.generator.model.FieldInfo;
import com.sayyi.software.tbp.generator.model.ModuleInfo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DslParserTest {

    @Test
    public void test() throws IOException {
        DslReader dslReader = new DslReader();
        Assert.assertEquals(gen(), dslReader.parse("/test.dsl").get(0));
    }

    private ModuleInfo gen() {
        ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.setModuleName("org.apache.zookeeper.data");
        List<ClassInfo> classInfos = new LinkedList<>();
        moduleInfo.setClassInfos(classInfos);

        ClassInfo classInfo = new ClassInfo();
        classInfos.add(classInfo);
        classInfo.setClassName("Id");
        List<FieldInfo> fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        fieldInfos.add(getField("ustring", "scheme"));
        fieldInfos.add(getField("ustring", "id"));

        classInfo = new ClassInfo();
        classInfos.add(classInfo);
        classInfo.setClassName("ACL");
        fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        fieldInfos.add(getField("int", "perms"));
        fieldInfos.add(getField("Id", "id"));

        classInfo = new ClassInfo();
        classInfos.add(classInfo);
        classInfo.setClassName("Stat");
        fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        fieldInfos.add(getField("long", "czxid"));
        fieldInfos.add(getField("long", "mzxid"));
        fieldInfos.add(getField("long", "ctime"));
        fieldInfos.add(getField("long", "mtime"));
        fieldInfos.add(getField("int", "version"));
        fieldInfos.add(getField("int", "cversion"));
        fieldInfos.add(getField("int", "aversion"));
        fieldInfos.add(getField("long", "ephemeralOwner"));
        fieldInfos.add(getField("int", "dataLength"));
        fieldInfos.add(getField("int", "numChildren"));
        fieldInfos.add(getField("long", "pzxid"));

        classInfo = new ClassInfo();
        classInfos.add(classInfo);
        classInfo.setClassName("StatPersisted");
        fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        fieldInfos.add(getField("long", "czxid"));
        fieldInfos.add(getField("long", "mzxid"));
        fieldInfos.add(getField("long", "ctime"));
        fieldInfos.add(getField("long", "mtime"));
        fieldInfos.add(getField("int", "version"));
        fieldInfos.add(getField("int", "cversion"));
        fieldInfos.add(getField("int", "aversion"));
        fieldInfos.add(getField("long", "ephemeralOwner"));
        fieldInfos.add(getField("long", "pzxid"));

        classInfo = new ClassInfo();
        classInfos.add(classInfo);
        classInfo.setClassName("ClientInfo");
        fieldInfos = new LinkedList<>();
        classInfo.setFields(fieldInfos);
        fieldInfos.add(getField("ustring", "authScheme"));
        fieldInfos.add(getField("ustring", "user"));

        return moduleInfo;
    }

    private FieldInfo getField(String type, String name) {
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setFieldType(type);
        fieldInfo.setFieldName(name);
        return fieldInfo;
    }
}
