package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.FileInfo;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.action.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.*;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
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
    private static final String RECENT_OPENED_TAG = "RECENT_MODIFIED";
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
    public FileMetadata create(CreateAction createAction) {
        isModified = true;
        FileInfo fileInfo = createAction.getFileInfo();
        Set<String> tags = createAction.getTags();

        FileMetadata fileMetadata = createMetadata(fileInfo);
        id2FileMap.put(fileMetadata.getId(), fileMetadata);
        fileMetadataList.add(fileMetadata);

        for (String tag : tags) {
            addFileTag(tag, fileMetadata);
        }
        // 通过流上传文件时，初始没有标签，所以把这个打上，就可以快速在列表中展示这个文件了
        // 逻辑上来说，感觉也没问题来着。
        addFileTag(RECENT_OPENED_TAG, fileMetadata);

        log.debug("添加文件【{}】", fileMetadata);
        return fileMetadata;
    }

    private FileMetadata createMetadata(FileInfo fileInfo) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(nextFileId++);
        fileMetadata.setFilename(fileInfo.getFilename());
        fileMetadata.setRelativePath(fileInfo.getRelativePath());
        fileMetadata.setTags(new HashSet<>());
        fileMetadata.setCreateTime(fileInfo.getModifyTime());
        fileMetadata.setLastOpenTime(fileInfo.getModifyTime());
        return fileMetadata;
    }

    @Override
    public void rename(RenameAction renameAction) throws TbpException {
        final FileInfo fileInfo = renameAction.getFileInfo();
        final long id = renameAction.getId();
        FileMetadata fileMetadata = getFileById(id);
        fileMetadata.setRelativePath(fileInfo.getRelativePath());
        fileMetadata.setFilename(fileInfo.getFilename());
    }

    @Override
    public void modifyTag(ModifyTagAction modifyTagAction) throws TbpException {
        isModified = true;

        final long id = modifyTagAction.getId();
        FileMetadata fileMetadata = getFileById(id);
        final Set<String> newTags = modifyTagAction.getNewTags();

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
    public void open(OpenAction openAction) throws TbpException {
        isModified = true;
        FileMetadata fileMetadata = getFileById(openAction.getId());
        fileMetadata.setLastOpenTime(openAction.getOpenTime());
        addFileTag(RECENT_OPENED_TAG, fileMetadata);
    }

    @Override
    public void delete(DeleteAction deleteAction) throws TbpException {
        isModified = true;
        final long id = deleteAction.getId();
        FileMetadata fileMetadata = getFileById(deleteAction.getId());
        fileMetadataList.remove(fileMetadata);
        id2FileMap.remove(id);

        final Set<String> tags = fileMetadata.getTags();
        for (String tag : tags) {
            removeFileTag(tag, fileMetadata, false);
        }
    }

    @Override
    public void deleteTag(DeleteTagAction deleteTagAction) throws TbpException {
        isModified = true;
        final String tag = deleteTagAction.getTag();
        checkTag(tag);
        Map<Long, FileMetadata> metadataMap = tagFileMap.get(tag);
        // 遍历同时修改集合，只能通过迭代器来处理来着。
        // 怎么办呢？只能重新组装一个集合出来才行了。
        Set<FileMetadata> toModify = new HashSet<>(metadataMap.size());
        toModify.addAll(metadataMap.values());
        toModify.forEach(file -> removeFileTag(tag, file, true));
    }

    @Override
    public void renameTag(RenameTagAction renameTagAction) throws TbpException {
        isModified = true;
        final String tag = renameTagAction.getTag();
        final String newTag = renameTagAction.getNewTag();
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

    /**
     * 获取最近被打开的文件集合
     * @return
     */
    @Override
    public List<FileMetadata> listRecentOpened() {
        return listByTags(Collections.singleton(RECENT_OPENED_TAG),
                Comparator.comparingLong(FileMetadata::getLastOpenTime)
                        .reversed()
        );
    }

    /**
     * 通过标签搜素文件列表
     * @param tags  目标标签集合
     * @param comparator    排序原则（其实没有必要，前端获取到所有数据，完全可以自己排序，毕竟本地应用，数据量不会太过头吧应该。
     *                      出问题了再说）
     * @return  目标文件集合
     */
    @Override
    public List<FileMetadata> listByTags(Set<String> tags, Comparator<FileMetadata> comparator) {
        Map<Long, FileMetadata> fileMap = null;
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

        Stream<FileMetadata> metadataStream = new ArrayList<>(fileMap.values()).stream();
        if (tags.size() > 1) {
            metadataStream = metadataStream.filter(file -> file.getTags().containsAll(tags));
        }
        return metadataStream.sorted(comparator)
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
