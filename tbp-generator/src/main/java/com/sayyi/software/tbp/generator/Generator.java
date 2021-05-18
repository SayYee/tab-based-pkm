package com.sayyi.software.tbp.generator;

import com.sayyi.software.tbp.generator.fieldstr.JType;
import com.sayyi.software.tbp.generator.fieldstr.JTypeFactory;
import com.sayyi.software.tbp.generator.model.ClassInfo;
import com.sayyi.software.tbp.generator.model.FieldInfo;
import com.sayyi.software.tbp.generator.model.ModuleInfo;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Generator {

    public static void main(String[] args) throws IOException {
        String dslFilePath = "/tbp.dsl";
        String templateDir = "template/";
        String templatePath = "/classTemplate.txt";

        // 输出到common模块中
        String rootPath = "/Users/xuchuang/Code/Java/tag-based-pkm/tbp-common/src/main/java";

        Template template = getProxyTemplate(templateDir, templatePath);
        List<ModuleInfo> moduleInfos = parseFromDsl(dslFilePath);

        for (ModuleInfo moduleInfo : moduleInfos) {
//            System.out.println(moduleInfo);
            genModule(rootPath, moduleInfo, template);
        }
    }

    private static List<ModuleInfo> parseFromDsl(String dslFilePath) throws IOException {
        DslReader dslReader = new DslReader();
        return dslReader.parse(dslFilePath);
    }

    private static Template getProxyTemplate(String templateDir, String templatePath) throws IOException {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(templateDir);
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template root = gt.getTemplate(templatePath);
        // 可重用模版。每次render之后，还可以重新绑定所有变量，继续使用。
        return gt.getTemplateProxy(root);
    }

    private static void genModule(String rootPath, ModuleInfo moduleInfo, Template template) throws IOException {
        String[] packages = moduleInfo.getModuleName().split("\\.");
        String packagePath = rootPath + File.separator + String.join(File.separator, packages);
        File file = new File(packagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (ClassInfo classInfo : moduleInfo.getClassInfos()) {
            genClass(packagePath, moduleInfo.getModuleName(), classInfo, template);
        }
    }

    private static void genClass(String packagePath, String packageStr, ClassInfo classInfo, Template template) throws IOException {
        File javaFile = new File(packagePath, classInfo.getClassName() + ".java");
        if (javaFile.exists()) {
            javaFile.delete();
        }
        Map<String, JType> fields = new LinkedHashMap<>();
        for (FieldInfo field : classInfo.getFields()) {
            fields.put(field.getFieldName(), JTypeFactory.get(field.getFieldType()));
        }

        template.binding("package", packageStr);
        template.binding("class", classInfo.getClassName());
        template.binding("fields", fields);
        String str = template.render();
        Files.write(javaFile.toPath(), str.getBytes(StandardCharsets.UTF_8));
    }
}
