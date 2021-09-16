package com.sayyi.software.tbp.common;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author SayYi
 */
public class TbpConfigParse implements TbpConfig {

    private String snapDir;
    private String storeDir;

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
            storeDir = properties.getProperty("storeDir");
            snapDir = properties.getProperty("snapDir");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromYaml(String configPath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(configPath)) {
            Yaml yaml = new Yaml();
            final Map<String, Object> load = yaml.load(inputStream);
            storeDir = ((Map<String, String>)load.get("tbp")).get("snap-dir");
            snapDir = ((Map<String, String>)load.get("tbp")).get("store-dir");
        }
    }

    @Override
    public String getStoreDir() {
        return storeDir;
    }

    @Override
    public String getSnapDir() {
        return snapDir;
    }
}
