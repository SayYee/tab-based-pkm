package com.sayyi.software.tbp.generator;

import com.sayyi.software.tbp.core.facade.PkmFunction;
import com.sayyi.software.tbp.generator.feign.FunctionParser;
import com.sayyi.software.tbp.generator.feign.model.FunctionInfo;
import org.beetl.core.Template;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class FeignGenerator {

    public static void main(String[] args) throws IOException {
        // 解析接口信息
        final FunctionInfo functionInfo = FunctionParser.parse(PkmFunction.class);
        // 写api接口
        String interfacePackage = "com.sayyi.software.tbp.web.api";
        String interfaceName = "FeignPkmFunction";
        writeInterface(functionInfo, interfacePackage, interfaceName);
        // 写controller实现
        String functionName = interfacePackage + "." + interfaceName;
        String controllerPackage = "com.sayyi.software.tbp.web.controller";
        String controllerName = "ApiController";
        writeController(functionInfo, functionName, controllerPackage, controllerName);
    }

    private static void writeController(FunctionInfo functionInfo, String functionName, String packageName, String controllerName) throws IOException {
        Template template = Utils.getProxyTemplate("FeignControllerTemplate.txt");

        template.binding("package", packageName);
        template.binding("controllerName", controllerName);
        template.binding("functionName", functionName);
        template.binding("functionInfo", functionInfo);
        String str = template.render();

        // 写入文件
        String rootPath = Utils.getModuleJavaDir("tbp-web");
        String[] packages = packageName.split("\\.");
        String packagePath = rootPath + File.separator + String.join(File.separator, packages);
        File file = new File(packagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File javaFile = new File(packagePath, controllerName + ".java");
        if (javaFile.exists()) {
            javaFile.delete();
        }
        Files.write(javaFile.toPath(), str.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeInterface(FunctionInfo functionInfo, String packageName, String functionName) throws IOException {
        Template template = Utils.getProxyTemplate("FeignInterfaceTemplate.txt");

        // 接口类 路由
        String rootMapping = "/function";

        template.binding("rootMapping", rootMapping);
        template.binding("package", packageName);
        template.binding("functionName", functionName);
        template.binding("functionInfo", functionInfo);
        String str = template.render();

        // 写入文件
        String rootPath = Utils.getModuleJavaDir("tbp-web-api");
        String[] packages = packageName.split("\\.");
        String packagePath = rootPath + File.separator + String.join(File.separator, packages);
        File file = new File(packagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File javaFile = new File(packagePath, functionName + ".java");
        if (javaFile.exists()) {
            javaFile.delete();
        }
        Files.write(javaFile.toPath(), str.getBytes(StandardCharsets.UTF_8));
    }

}
