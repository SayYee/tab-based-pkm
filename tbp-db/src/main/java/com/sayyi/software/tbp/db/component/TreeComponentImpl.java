package com.sayyi.software.tbp.db.component;

import com.alibaba.fastjson.JSON;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.Tree;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TreeComponentImpl implements TreeComponent{

    private final String snapDir;

    private Tree tree;

    /** 默认tree文件路径 */
    private static final String DEFAULT_TREE = "default.tree";

    /**
     * tree文件前缀
     */
    private static final String TREE_FILE = "tree";
    private static final String TEMP_TREE_FILE = "tree-temp";

    public TreeComponentImpl(String snapDir) {
        this.snapDir = snapDir;
        File snapDirFile = new File(snapDir);
        if (!snapDirFile.exists()) {
            if (!snapDirFile.mkdirs()) {
                throw new IllegalArgumentException(snapDirFile.toString() + " file is missing and create failed");
            }
        }
        String treeJson = loadTree();
        if (treeJson == null) {
            treeJson = loadDefaultTree();
        }
        tree = JSON.parseObject(treeJson, Tree.class);
    }

    private String loadTree() {
        File file = new File(snapDir, TREE_FILE);
        if (!file.exists()) {
            return null;
        }
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            return new String(content, UTF_8);
        } catch (IOException e) {
            throw new TbpException("读取tree文件失败");
        }
    }

    private String loadDefaultTree() {
        try {
            File file = new File(Objects.requireNonNull(this.getClass().getResource("/" + DEFAULT_TREE)).toURI());
            byte[] content = Files.readAllBytes(file.toPath());
            return new String(content, UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new TbpException("加载默认tree文件失败");
        }
    }

    private void write(Tree tree) {
        try {
            File treeTemp = new File(snapDir, TEMP_TREE_FILE);
            Files.write(treeTemp.toPath(), JSON.toJSONString(tree).getBytes(UTF_8));
            File treeFile = new File(snapDir, TREE_FILE);
            if (treeFile.exists()) {
                treeFile.delete();
            }
            FileUtil.rename(treeTemp, TREE_FILE);
        } catch (IOException e) {
            throw new TbpException("写入文件失败");
        }
    }

    @Override
    public Tree load() {
        return tree;
    }

    @Override
    public void store(Tree tree) {
        write(tree);
        this.tree = tree;
    }
}
