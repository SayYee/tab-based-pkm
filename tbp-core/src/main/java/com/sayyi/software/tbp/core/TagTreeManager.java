package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.TbpException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 具备存储功能。可以设置保留的历史版本数量。默认会加载最新的数据。
 * @author SayYi
 */
public class TagTreeManager implements TagTreeFunction {

    /** 默认tree文件路径 */
    private static final String DEFAULT_TREE = "default.tree";

    /**
     * tree文件前缀
     */
    private static final String TREE_FILE_PREFIX = "tree-";

    /** 文件存储路径 */
    private final String snapDir;
    /** 保留的历史版本数量 */
    private final int retainNum;

    /**
     * 当前树数据
     */
    private String currentTreeStr;

    /** 当前最大id */
    private long maxId;
    /** 下一个tree文件id。溢出也不会有影响 */
    private long nextId;

    private final IdContainer idContainer;

    public TagTreeManager(String snapDir, int retainNum) {
        this.snapDir = snapDir;
        this.retainNum = retainNum;
        this.idContainer = new IdContainer(retainNum, this::delete);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        File file = new File(snapDir);
        if (file.exists()) {
            String[] filenames = file.list((dir, name) -> name.startsWith(TREE_FILE_PREFIX));
            // 如果用户第一次启动，还没有tree信息，需要加载默认的tree数据出来
            if (filenames == null || filenames.length == 0) {
                maxId = 0;
                nextId = maxId + 1;
                // 加载默认的tree信息
                currentTreeStr = loadDefaultTree();
                return;
            }
            long[] ids = Arrays.stream(filenames)
                    .mapToLong(s -> Long.parseLong(s.substring(TREE_FILE_PREFIX.length())))
                    // TODO 需要注意这里。万一真的溢出了，这里排序就出问题了
                    .sorted()
                    .toArray();
            for (long id : ids) {
                idContainer.offer(id);
            }
            maxId = idContainer.getLast();
            nextId = maxId + 1;
            currentTreeStr = read(maxId);
        }
    }

    /**
     * 获取所有的树文件id
     * @return
     */
    @Override
    public List<Long> listIds() {
        return idContainer.listAll();
    }

    @Override
    public String getCurrentTree() {
        return currentTreeStr;
    }

    @Override
    public String getTree(long id) {
        if (!listIds().contains(id)) {
            throw new TbpException("对应tree文件不存在【" + id + "】");
        }
        return read(id);
    }

    @Override
    public long setTree(String treeStr) {
        write(nextId, treeStr);
        idContainer.offer(nextId);
        currentTreeStr = treeStr;
        maxId++;
        nextId++;
        return maxId;
    }

    /**
     * 通过id删除对应的文件
     * @param id
     */
    private boolean delete(long id) {
        File file = getFile(id);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    private String read(long id) {
        try {
            File file = getFile(id);
            byte[] content = Files.readAllBytes(file.toPath());
            return new String(content, UTF_8);
        } catch (IOException e) {
            throw new TbpException("读取文件失败【" + id + "】");
        }
    }

    private void write(long id, String treeStr) {
        try {
            File file = getFile(id);
            Files.write(file.toPath(), treeStr.getBytes(UTF_8));
        } catch (IOException e) {
            throw new TbpException("写入文件失败【" + id + "】");
        }
    }

    /**
     * 加载默认tree文件内容
     * @return
     */
    private String loadDefaultTree() {
        try {
            File file = new File(Objects.requireNonNull(this.getClass().getResource("/" + DEFAULT_TREE)).toURI());
            byte[] content = Files.readAllBytes(file.toPath());
            return new String(content, UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new TbpException("加载默认tree文件失败");
        }
    }

    private File getFile(long id) {
        return new File(snapDir, TREE_FILE_PREFIX + id);
    }

    /**
     * id容器。先进先出，移除的元素会调用处理函数进行处理
     */
    private static class IdContainer {
        /** 容器大小 */
        private final int size;
        /** 超出数据处理函数 */
        private final Function<Long, Boolean> function;

        private final LinkedList<Long> ids = new LinkedList<>();

        private IdContainer(int size, Function<Long, Boolean> function) {
            this.size = size;
            this.function = function;
        }

        public void offer(long value) {
            ids.offer(value);
            if (ids.size() > size) {
                function.apply(ids.poll());
            }
        }

        public long getLast() {
            return ids.getLast();
        }

        public List<Long> listAll() {
            return Collections.unmodifiableList(ids);
        }

    }
}
