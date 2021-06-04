package com.sayyi.software.tbp.generator;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.IOException;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class Utils {

    /**
     * 获取项目根目录
     * @return
     */
    public static String getProjectRootPath() {
        return System.getProperty("user.dir");
    }

    public static String getModulePath(String projectModule) {
        return getProjectRootPath() + File.separator + projectModule;
    }

    /**
     * 获取对应模块java文件存储位置
     * @param projectModule
     * @return
     */
    public static String getModuleJavaDir(String projectModule) {
        return String.join(File.separator, getProjectRootPath(), projectModule, "src", "main", "java");
    }

    public static Template getProxyTemplate(String templateName) throws IOException {
        return Utils.getProxyTemplate("template/", "/" + templateName);
    }

    /**
     * 获取可复用的template模版
     * @param templateDir
     * @param templatePath
     * @return
     * @throws IOException
     */
    public static Template getProxyTemplate(String templateDir, String templatePath) throws IOException {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(templateDir);
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        Template root = gt.getTemplate(templatePath);
        // 可重用模版。每次render之后，还可以重新绑定所有变量，继续使用。
        return gt.getTemplateProxy(root);
    }
}
