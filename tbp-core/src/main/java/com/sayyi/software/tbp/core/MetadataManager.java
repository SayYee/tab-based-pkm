package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.TagInfo;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.FileBaseInfo;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件标签管理。把这个单独摘出来，是希望可以添加对标签的扩展功能。比如空标签、临时标签、限额标签之类
 * 这个类。所有对tag的操作都交给这个类处理
 * @author SayYi
 */
@Slf4j
public class MetadataManager implements MetadataFunction {

    /**
     * 空 标签
     */
    private static final String EMPTY_TAG = "EMPTY";
    /**
     * 最近被打开 标签
     */
    public static final String RECENT_OPENED_TAG = "RECENT_MODIFIED";
    /** 最近打开 标签允许关联的文件上限 */
    private static final int OPENED_TAG_LIMIT = 20;

    /**
     * 所有的文件元数据信息
     */
    private final List<FileMetadata> fileMetadataList = new ArrayList<>();
    /**
     * id-文件缓存
     */
    private final Map<Long, FileMetadata> id2FileMap = new HashMap<>();
    /**
     * 下一个文件id
     */
    private long nextFileId = 1;
    /**
     * 标签对应文件列表:tag-fileId-file
     */
    private final Map<String, Map<Long, FileMetadata>> tagFileMap = new HashMap<>();

    private boolean isModified = true;
    private final GexfGenerator gexfGenerator = new GexfGenerator();

    @Override
    public void recovery(Snapshot snapshot) {
        if (snapshot.getLastOpId() == -1) {
            return;
        }
        nextFileId = snapshot.getLastFileId();
        fileMetadataList.addAll(snapshot.getFileMetadataList());
        for (FileMetadata fileMetadata : fileMetadataList) {
            id2FileMap.put(fileMetadata.getId(), fileMetadata);
        }

        for (FileMetadata fileMetadata : fileMetadataList) {
            for (String tag : fileMetadata.getTags()) {
                Map<Long, FileMetadata> metadataMap = tagFileMap.computeIfAbsent(tag, o -> new HashMap<>());
                metadataMap.put(fileMetadata.getId(), fileMetadata);
            }
        }
    }

    @Override
    public FileMetadata create(int resourceType, FileBaseInfo fileBaseInfo) {
        isModified = true;

        FileMetadata fileMetadata = createMetadata(resourceType, fileBaseInfo);
        id2FileMap.put(fileMetadata.getId(), fileMetadata);
        fileMetadataList.add(fileMetadata);

        Set<String> tags = fileBaseInfo.getTags();
        for (String tag : tags) {
            addFileTag(tag, fileMetadata);
        }
        // 通过流上传文件时，初始没有标签，所以把这个打上，就可以快速在列表中展示这个文件了
        // 逻辑上来说，感觉也没问题来着。
        addFileTag(RECENT_OPENED_TAG, fileMetadata);

        log.debug("添加文件【{}】", fileMetadata);
        return fileMetadata;
    }

    private FileMetadata createMetadata(int resourceType, FileBaseInfo fileBaseInfo) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(nextFileId++);
        fileMetadata.setFilename(fileBaseInfo.getFilename());
        fileMetadata.setResourceType(resourceType);
        fileMetadata.setResourcePath(fileBaseInfo.getResourcePath());
        fileMetadata.setTags(new HashSet<>());
        fileMetadata.setCreateTime(fileBaseInfo.getModifyTime());
        fileMetadata.setLastOpenTime(fileBaseInfo.getModifyTime());
        return fileMetadata;
    }

    @Override
    public void rename(FileBaseInfo fileBaseInfo) {
        long id = fileBaseInfo.getFileId();
        FileMetadata fileMetadata = getFileById(id);
        fileMetadata.setResourcePath(fileBaseInfo.getResourcePath());
        fileMetadata.setFilename(fileBaseInfo.getFilename());
    }

    @Override
    public void modifyTag(long fileId, Set<String> newTags) {
        isModified = true;
        FileMetadata fileMetadata = getFileById(fileId);

        Set<String> toAddTags = new HashSet<>();
        Set<String> toRemoveTags = new HashSet<>();
        for (String tag : fileMetadata.getTags()) {
            if (newTags.contains(tag)) {
                continue;
            }
            toRemoveTags.add(tag);
        }
        for (String newTag : newTags) {
            if (fileMetadata.getTags().contains(newTag)) {
                continue;
            }
            toAddTags.add(newTag);
        }
        // 也许应该改成忽略，而不是报错来着。
        log.debug("to remove tags【{}】", toRemoveTags);
        log.debug("to add tags【{}】", toAddTags);
        checkTag(toRemoveTags);
        checkTag(toAddTags);
        for (String toAddTag : toAddTags) {
            addFileTag(toAddTag, fileMetadata);
        }
        for (String toRemoveTag : toRemoveTags) {
            removeFileTag(toRemoveTag, fileMetadata, true);
        }
    }

    @Override
    public void open(long fileId, long openTime) {
        isModified = true;
        FileMetadata fileMetadata = getFileById(fileId);
        fileMetadata.setLastOpenTime(openTime);
        addFileTag(RECENT_OPENED_TAG, fileMetadata);
    }

    @Override
    public void delete(long fileId) {
        isModified = true;
        FileMetadata fileMetadata = getFileById(fileId);
        fileMetadataList.remove(fileMetadata);
        id2FileMap.remove(fileId);

        Set<String> tags = new HashSet<>(fileMetadata.getTags());
        for (String tag : tags) {
            removeFileTag(tag, fileMetadata, false);
        }
    }

    @Override
    public void deleteTag(String tag) {
        isModified = true;
        checkTag(tag);
        Map<Long, FileMetadata> metadataMap = tagFileMap.get(tag);
        // 遍历同时修改集合，只能通过迭代器来处理来着。
        // 怎么办呢？只能重新组装一个集合出来才行了。
        Set<FileMetadata> toModify = new HashSet<>(metadataMap.size());
        toModify.addAll(metadataMap.values());
        toModify.forEach(file -> removeFileTag(tag, file, true));
    }

    @Override
    public void renameTag(String tag, String newTag) {
        isModified = true;
        if (tag.equals(newTag)) {
            log.info("标签没有发生变化");
            return;
        }
        checkTag(tag);
        checkTag(newTag);

        Map<Long, FileMetadata> metadataMap = tagFileMap.get(tag);
        Set<FileMetadata> toModify = new HashSet<>(metadataMap.size());
        toModify.addAll(metadataMap.values());
        toModify.forEach(file -> {
            removeFileTag(tag, file, false);
            addFileTag(newTag, file);
        });
    }

    @Override
    public FileMetadata getFileById(long fileId) throws TbpException {
        final FileMetadata fileMetadata = id2FileMap.get(fileId);
        if (fileMetadata == null) {
            throw new TbpException("未找到文件信息");
        }
        return fileMetadata;
    }

    @Override
    public List<FileMetadata> listAllFile() {
        return fileMetadataList;
    }

    @Override
    public long getNextFileId() {
        return nextFileId;
    }

    private final Comparator<FileMetadata> defaultComparator = Comparator.comparingLong(FileMetadata::getId);

    @Override
    public List<FileMetadata> listResources(String filenameReg, Set<String> tags) {
        boolean noTags = tags == null || tags.isEmpty();
        boolean noFilename = filenameReg == null || "".equals(filenameReg.trim());
        // 这里是获取 关联文件最少的标签
        Map<Long, FileMetadata> fileMap = null;
        Stream<FileMetadata> metadataStream;
        if (!noTags) {
            for (String tag : tags) {
                Map<Long, FileMetadata> map = tagFileMap.get(tag);
                if (fileMap == null) {
                    fileMap = map;
                    continue;
                }
                if (map.size() < fileMap.size()) {
                    fileMap = map;
                }
            }
            if (fileMap == null) {
                return new ArrayList<>();
            }
            metadataStream = new ArrayList<>(fileMap.values()).stream()
                    .filter(file -> file.getTags().containsAll(tags));
        } else {
            metadataStream = fileMetadataList.stream();
        }
        if (!noFilename) {
            metadataStream = metadataStream.filter(file -> Pattern.matches(filenameReg, file.getFilename()));
        }

        return metadataStream.sorted(defaultComparator)
                .collect(Collectors.toList());
    }

    private final Comparator<TagInfo> tagComparator = Comparator.comparingLong(TagInfo::getFileNum).reversed();

    @Override
    public List<TagInfo> listTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return tagFileMap.entrySet().stream()
                    .map(entry -> new TagInfo(entry.getKey(), entry.getValue().size()))
                    .sorted(tagComparator)
                    .collect(Collectors.toList());
        }
        List<FileMetadata> fileMetadataList = listResources(null, tags);
        Map<String, Integer> tagInfoMap = new HashMap<>();
        for (FileMetadata fileMetadata : fileMetadataList) {
            for (String tag : fileMetadata.getTags()) {
                if (tags.contains(tag)) {
                    continue;
                }
                Integer count = tagInfoMap.get(tag);
                count = count == null ? 1 : count + 1;
                tagInfoMap.put(tag, count);
            }
        }
        return tagInfoMap.entrySet().stream()
                .map(entry -> new TagInfo(entry.getKey(), entry.getValue()))
                .sorted(tagComparator)
                .collect(Collectors.toList());
    }

    private void checkTag(Set<String> tags) throws TbpException {
        if (tags.contains(EMPTY_TAG) || tags.contains(RECENT_OPENED_TAG)) {
            throw new TbpException("特殊标签不能进行操作");
        }
    }
    private void checkTag(String tagName) throws TbpException {
        if (EMPTY_TAG.equals(tagName) || RECENT_OPENED_TAG.equals(tagName)) {
            throw new TbpException("特殊标签不能进行操作");
        }
    }
    /**
     * 和下边的方法，一起进行特殊标签的处理
     * 删除文件的某个标签
     * @param tagName
     * @param fileMetadata
     * @param checkEmpty 是否要进行无标签校验。主要是为了删除文件时，清理标签的场景。一般就是true就可以
     */
    private void removeFileTag(String tagName, FileMetadata fileMetadata, boolean checkEmpty) {
        final Map<Long, FileMetadata> metadataMap = tagFileMap.get(tagName);
        metadataMap.remove(fileMetadata.getId());
        fileMetadata.getTags().remove(tagName);
        // 如果集合空了，就移除对应标签
        if (!EMPTY_TAG.equals(tagName) && metadataMap.isEmpty()) {
            tagFileMap.remove(tagName);
            log.info("标签【{}】无关联文件，移除", tagName);
        }
        
        // 如果空了，就添加 空 标签
        if (checkEmpty && fileMetadata.getTags().isEmpty()) {
            addFileTag(EMPTY_TAG, fileMetadata);
            log.info("文件【{}-{}】添加空标签", fileMetadata.getId(), fileMetadata.getFilename());
        }
    }

    /**
     * 为文件添加标签
     * @param tagName
     * @param fileMetadata
     */
    private void addFileTag(String tagName, FileMetadata fileMetadata) {
        final Map<Long, FileMetadata> metadataMap = tagFileMap.computeIfAbsent(tagName, o -> new HashMap<>());
        metadataMap.put(fileMetadata.getId(), fileMetadata);
        if (fileMetadata.getTags() == null) {
            fileMetadata.setTags(new HashSet<>());
        }
        fileMetadata.getTags().add(tagName);

        // 如果除了 空 还有别的标签，就删除EMPTY
        boolean needDropEmptyTag = fileMetadata.getTags().contains(EMPTY_TAG) && fileMetadata.getTags().size() > 1;
        if (needDropEmptyTag) {
            // 这里 true或者false没什么区别
            removeFileTag(EMPTY_TAG, fileMetadata, true);
            log.info("移除文件【{}-{}】空标签", fileMetadata.getId(), fileMetadata.getFilename());
        }

        // 如果加入的是 最近修改 标签
        if (RECENT_OPENED_TAG.equals(tagName)) {
            boolean outOfLimit = metadataMap.size() > OPENED_TAG_LIMIT;
            if (outOfLimit) {
                // 获取打开时间最小的文件
                Optional<FileMetadata> first = metadataMap.values().stream()
                        .min(Comparator.comparingLong(FileMetadata::getLastOpenTime));
                removeFileTag(RECENT_OPENED_TAG, first.get(), true);
                log.info("移除文件【{}-{}】的最近打开标签", first.get().getId(), first.get().getFilename());
            }
        }
    }

    /**
     * 生成标签gexf文件
     * @param out
     * @throws IOException
     */
    @Override
    public void tagMap(OutputStream out) throws IOException {
        gexfGenerator.tagMap(out);
    }

    /**
     * gexf文件生成组件
     */
    private class GexfGenerator {
        /**
         * gexf视图对象
         */
        private Gexf gexf;
        /** 是否被修改过 */
        private final StaxGraphWriter graphWriter = new StaxGraphWriter();

        /**
         * 将gexf信息写入流中。如果文件被修改了，每次都会创建一个新的gexf出来
         * 后边可以自己实现一个支持动态修改的类出来
         * @param out
         * @throws IOException
         */
        private void tagMap(OutputStream out) throws IOException {
            if (!isModified) {
                // 这个方法，并不会调用out的close方法
                // flush会被调用多次
                graphWriter.writeToStream(gexf, out, "UTF-8");
                log.info("从缓存获取tagMap");
                return;
            }
            gexf = new GexfImpl();

            gexf.getMetadata()
                    .setLastModified(new Date())
                    .setCreator("sayyi")
                    .setDescription("tag-gexf");
            gexf.setVisualization(true);

            Graph graph = gexf.getGraph();
            graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

            Map<String, Node> nodeMap = new HashMap<>(tagFileMap.size());
            // 创建节点
            for (Map.Entry<String, Map<Long, FileMetadata>> tagEntry : tagFileMap.entrySet()) {
                // TODO 这里应该有颜色表
                String tag = tagEntry.getKey();
                Node node = graph.createNode(tag)
                        .setLabel(tag)
                        .setSize(tagEntry.getValue().size());
                nodeMap.put(tag, node);
            }
            // 创建连线
            for (Map.Entry<String, Map<Long, FileMetadata>> tagEntry : tagFileMap.entrySet()) {
                String sourceTag = tagEntry.getKey();
                Set<String> targetTags = new HashSet<>();
                for (FileMetadata fileMetadata : tagEntry.getValue().values()) {
                    targetTags.addAll(fileMetadata.getTags());
                }
                // 避免自己连接自己
                targetTags.remove(sourceTag);
                // 这里不使用 graph 自带的查找方法。那个底层是基于ArrayList实现的，遍历查找，挺坑爹的
                Node sourceNode = nodeMap.get(sourceTag);
                for (String targetTag : targetTags) {
                    Node targetNode = nodeMap.get(targetTag);
                    sourceNode.connectTo(sourceTag + "-" + targetTag, targetNode);
                }
            }

            graphWriter.writeToStream(gexf, out, "UTF-8");
            log.info("tagMap生成成功");
            isModified = false;
        }
    }

}
