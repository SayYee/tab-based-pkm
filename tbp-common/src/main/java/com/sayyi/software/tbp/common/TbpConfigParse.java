package com.sayyi.software.tbp.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author SayYi
 */
public class TbpConfigParse implements TbpConfig {

    private final int port;
    private final String snapDir;
    private final String storeDir;

    public TbpConfigParse(String configPath) throws IOException {
        File file = new File(configPath);
        FileInputStream in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        port = Integer.parseInt(properties.getProperty("port"));
        storeDir = properties.getProperty("storeDir");
        snapDir = properties.getProperty("snapDir");
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
}
