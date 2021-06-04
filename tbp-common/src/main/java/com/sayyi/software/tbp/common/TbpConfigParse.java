package com.sayyi.software.tbp.common;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author SayYi
 */
public class TbpConfigParse implements TbpConfig {

    private int port;
    private String snapDir;
    private String storeDir;
    private int treeRetainNum;

    /**
     * properties、yml文件的解析
     * @param configPath
     * @throws IOException
     */
    public TbpConfigParse(String configPath) throws IOException {
        if (configPath.endsWith("yml")) {
            loadFromYaml(configPath);
        } else {
            loadFromCfg(configPath);
        }

    }

    private void loadFromCfg(String configPath) throws IOException {
        File file = new File(configPath);
        try (FileInputStream in = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(in);
            port = Integer.parseInt(properties.getProperty("port"));
            storeDir = properties.getProperty("storeDir");
            snapDir = properties.getProperty("snapDir");
            treeRetainNum = Integer.parseInt(properties.getProperty("treeRetainNum"));
        }
    }

    private void loadFromYaml(String configPath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml();
            final Map<String, Object> load = yaml.load(inputStream);
            port = ((Map<String, Integer>)load.get("server")).get("port");
            storeDir = ((Map<String, String>)load.get("tbp")).get("snap-dir");
            snapDir = ((Map<String, String>)load.get("tbp")).get("store-dir");
            treeRetainNum = ((Map<String, Integer>)load.get("tbp")).get("tree-retain-num");
        }
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getStoreDir() {
        return storeDir;
    }

    @Override
    public String getSnapDir() {
        return snapDir;
    }

    @Override
    public int treeRetainNum() {
        return treeRetainNum;
    }
}
