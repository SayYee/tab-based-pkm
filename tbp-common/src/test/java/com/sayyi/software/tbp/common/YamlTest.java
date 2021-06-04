package com.sayyi.software.tbp.common;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class YamlTest {

    public static void main(String[] args) {
        final URL resource = YamlTest.class.getClassLoader().getResource("application.yml");
        try (FileInputStream inputStream = new FileInputStream(new File(resource.toURI()))) {
            Yaml yaml = new Yaml();
            final Map<String, Object> load = yaml.load(inputStream);
            Integer port = ((Map<String, Integer>)load.get("server")).get("port");
            String snapDir = ((Map<String, String>)load.get("tbp")).get("snap-dir");
            String storeDir = ((Map<String, String>)load.get("tbp")).get("store-dir");
            Integer treeRetainNum = ((Map<String, Integer>)load.get("tbp")).get("tree-retain-num");
            System.out.println(port);
            System.out.println(snapDir);
            System.out.println(storeDir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        yaml.loadAll()
    }
}
