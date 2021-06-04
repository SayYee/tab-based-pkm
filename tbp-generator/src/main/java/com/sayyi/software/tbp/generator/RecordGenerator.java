package com.sayyi.software.tbp.generator;

import com.sayyi.software.tbp.generator.dsl.DslReader;
import com.sayyi.software.tbp.generator.dsl.fieldstr.JType;
import com.sayyi.software.tbp.generator.dsl.fieldstr.JTypeFactory;
import com.sayyi.software.tbp.generator.dsl.model.ClassInfo;
import com.sayyi.software.tbp.generator.dsl.model.FieldInfo;
import com.sayyi.software.tbp.generator.dsl.model.ModuleInfo;
import org.beetl.core.Template;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 根据dsl配置，生成可序列化类（实现 Record 接口）
 * @author xuchuang
 */
public class RecordGenerator {

    public static void main(String[] args) throws IOException {
        String dslFilePath = "/tbp.dsl";
        String templateDir = "template/";
        String templatePath = "/RecordTemplate.txt";

        // 输出到common模块中
        String rootPath = Utils.getModuleJavaDir("tbp-common");

        Template template = Utils.getProxyTemplate(templateDir, templatePath);
        List<ModuleInfo> moduleInfos = parseFromDsl(dslFilePath);

        for (ModuleInfo moduleInfo : moduleInfos) {
            genModule(rootPath, moduleInfo, template);
        }
    }

    /**
     * 解析dsl文件，生成包、类信息
     * @param dslFilePath
     * @return
     * @throws IOException
     */
    private static List<ModuleInfo> parseFromDsl(String dslFilePath) throws IOException {
        DslReader dslReader = new DslReader();
        return dslReader.parse(dslFilePath);
    }

    /**
     * 生成包内所有类：创建package，生成java文件
     * @param rootPath
     * @param moduleInfo
     * @param template
     * @throws IOException
     */
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

    /**
     * 生成类：生成java文件
     * @param packagePath
     * @param packageStr
     * @param classInfo
     * @param template
     * @throws IOException
     */
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
