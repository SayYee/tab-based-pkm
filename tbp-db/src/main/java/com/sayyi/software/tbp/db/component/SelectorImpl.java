package com.sayyi.software.tbp.db.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.TagInfo;
import com.sayyi.software.tbp.db.api.component.MetadataDb;
import com.sayyi.software.tbp.db.api.component.Selector;
import com.sayyi.software.tbp.db.api.listener.TbpEvent;
import com.sayyi.software.tbp.db.api.listener.TbpEventType;
import com.sayyi.software.tbp.db.api.listener.TbpListener;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectorImpl implements Selector {

    private MetadataDb metadataDb;

    /**
     * id-文件缓存
     */
    private final Map<Long, FileMetadata> id2FileMap = new HashMap<>();
    /**
     * 标签对应文件列表:tag-fileId-file
     */
    private final Map<String, Map<Long, FileMetadata>> tagFileMap = new HashMap<>();
    /** 无标签数据集合 */
    private final Map<Long, FileMetadata> untaggedMap = new HashMap<>();

    private final Lock readLock;
    private final Lock writeLock;

    public SelectorImpl(Lock readLock, Lock writeLock) {
        this.readLock = readLock;
        this.writeLock = writeLock;
    }

    @Override
    public void setMetadataDb(MetadataDb metadataDb) {
        this.metadataDb = metadataDb;
        SelectorListener listener = new SelectorListener();
        this.metadataDb.addListener(listener);

        List<FileMetadata> fileMetadataList = metadataDb.listAll();
        // 生成索引数据
        for (FileMetadata fileMetadata : fileMetadataList) {
            id2FileMap.put(fileMetadata.getId(), fileMetadata);
            if (fileMetadata.getTags().isEmpty()) {
                untaggedMap.put(fileMetadata.getId(), fileMetadata);
            }
        }
        for (FileMetadata fileMetadata : fileMetadataList) {
            for (String tag : fileMetadata.getTags()) {
                Map<Long, FileMetadata> metadataMap = tagFileMap.computeIfAbsent(tag, o -> new HashMap<>());
                metadataMap.put(fileMetadata.getId(), fileMetadata);
            }
        }
    }

    @Override
    public FileMetadata get(long id) {
        readLock.lock();
        try {
            return id2FileMap.get(id);
        } finally {
            readLock.unlock();
        }
    }

    private final Comparator<FileMetadata> defaultComparator = Comparator.comparingLong(FileMetadata::getId);

    @Override
    public List<FileMetadata> list(Set<String> tags, String name) {
        readLock.lock();
        try {
            boolean noTags = tags == null || tags.isEmpty();
            boolean noFilename = name == null || "".equals(name.trim());
            if (noTags && noFilename) {
                return listUntagged();
            }
            // 这里是获取 关联文件最少的标签
            Map<Long, FileMetadata> fileMap = null;
            Stream<FileMetadata> metadataStream;
            if (!noTags) {
                for (String tag : tags) {
                    Map<Long, FileMetadata> map = tagFileMap.get(tag);
                    // bugfix 如果不存在该标签，直接返回
                    if (map == null) {
                        return new ArrayList<>();
                    }
                    if (fileMap == null) {
                        fileMap = map;
                        continue;
                    }
                    if (map.size() < fileMap.size()) {
                        fileMap = map;
                    }
                }
                metadataStream = new ArrayList<>(fileMap.values()).stream()
                        .filter(file -> file.getTags().containsAll(tags));
            } else {
                metadataStream = metadataDb.listAll().stream();
            }
            if (!noFilename) {
                metadataStream = metadataStream.filter(file -> Pattern.matches(name, file.getFilename()));
            }

            return metadataStream.sorted(defaultComparator)
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<FileMetadata> listUntagged() {
        readLock.lock();
        try {
            return new ArrayList<>(untaggedMap.values());
        } finally {
            readLock.unlock();
        }
    }

    private final Comparator<TagInfo> tagComparator = Comparator.comparingLong(TagInfo::getFileNum).reversed();
    @Override
    public List<TagInfo> listTags(Set<String> tags) {
        readLock.lock();
        try {
            if (tags == null || tags.isEmpty()) {
                return tagFileMap.entrySet().stream()
                        .map(entry -> {
                            TagInfo tagInfo = new TagInfo();
                            tagInfo.setTag(entry.getKey());
                            tagInfo.setFileNum(entry.getValue().size());
                            return tagInfo;
                        })
                        .sorted(tagComparator)
                        .collect(Collectors.toList());
            }
            List<FileMetadata> fileMetadataList = list(tags, null);
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
                    .map(entry -> {
                        TagInfo tagInfo = new TagInfo();
                        tagInfo.setTag(entry.getKey());
                        tagInfo.setFileNum(entry.getValue());
                        return tagInfo;
                    })
                    .sorted(tagComparator)
                    .collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }
    }

    private class SelectorListener implements TbpListener {

        @Override
        public int getInterestEvent() {
            return TbpEventType.ADD | TbpEventType.REMOVE | TbpEventType.MODIFY_TAGS | TbpEventType.TAG_UPDATE;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void call(TbpEvent tbpEvent) {
            writeLock.lock();
            try {
                // 这个就不用break了，一路匹配下来就好
                if ((tbpEvent.getEventType() & TbpEventType.ADD) != 0) {
                    dealAdd((FileMetadata) tbpEvent.getNewValue());
                }
                if ((tbpEvent.getEventType() & TbpEventType.REMOVE) != 0) {
                    dealRemove((FileMetadata) tbpEvent.getOldValue());
                }
                if ((tbpEvent.getEventType() & TbpEventType.MODIFY_TAGS) != 0) {
                    dealModifyTag((FileMetadata) tbpEvent.getOldValue(), (FileMetadata) tbpEvent.getNewValue());
                }
                if ((tbpEvent.getEventType() & TbpEventType.TAG_UPDATE) != 0) {
                    dealTagUpdate((Set<String>) tbpEvent.getOldValue(), (Set<String>) tbpEvent.getNewValue());
                }
            } finally {
                writeLock.unlock();
            }
        }

        private void dealAdd(FileMetadata fileMetadata) {
            id2FileMap.put(fileMetadata.getId(), fileMetadata);
            updateTagMap(fileMetadata, fileMetadata.getTags(), new HashSet<>());
            if (fileMetadata.getTags().isEmpty()) {
                untaggedMap.put(fileMetadata.getId(), fileMetadata);
            }
        }

        private void dealRemove(FileMetadata fileMetadata) {
            id2FileMap.remove(fileMetadata.getId());
            updateTagMap(fileMetadata, new HashSet<>(), fileMetadata.getTags());
            untaggedMap.remove(fileMetadata.getId());
        }

        private void dealModifyTag(FileMetadata oldData, FileMetadata newData) {
            Set<String> oldTags = oldData.getTags();
            Set<String> newTags = newData.getTags();
            Set<String> toAddTags = newTags.stream().filter(s -> !oldTags.contains(s)).collect(Collectors.toSet());
            Set<String> toRemoveTags = oldTags.stream().filter(s -> !newTags.contains(s)).collect(Collectors.toSet());
            updateTagMap(newData, toAddTags, toRemoveTags);
            if (newData.getTags().isEmpty()) {
                untaggedMap.put(newData.getId(), newData);
            } else {
                untaggedMap.remove(newData.getId());
            }
        }

        private void dealTagUpdate(Set<String> oldTags, Set<String> newTags) {
            Set<String> toAddTags = newTags.stream().filter(s -> !oldTags.contains(s)).collect(Collectors.toSet());
            Set<String> toRemoveTags = oldTags.stream().filter(s -> !newTags.contains(s)).collect(Collectors.toSet());
            List<FileMetadata> fileMetadataList = list(oldTags, null);
            for (FileMetadata fileMetadata : fileMetadataList) {
                updateTagMap(fileMetadata, toAddTags, toRemoveTags);
                if (fileMetadata.getTags().isEmpty()) {
                    untaggedMap.put(fileMetadata.getId(), fileMetadata);
                } else {
                    untaggedMap.remove(fileMetadata.getId());
                }
            }
        }

        /**
         * 更新map映射
         * @param fileMetadata  元数据对象
         * @param toAddTags 要添加的映射
         * @param toRemoveTags  要移除的映射
         */
        private void updateTagMap(FileMetadata fileMetadata, Set<String> toAddTags, Set<String> toRemoveTags) {
            for (String toAddTag : toAddTags) {
                Map<Long, FileMetadata> metadataMap = tagFileMap.computeIfAbsent(toAddTag, o -> new HashMap<>());
                metadataMap.put(fileMetadata.getId(), fileMetadata);
            }
            for (String toRemoveTag : toRemoveTags) {
                Map<Long, FileMetadata> metadataMap = tagFileMap.get(toRemoveTag);
                metadataMap.remove(fileMetadata.getId());
                if (metadataMap.isEmpty()) {
                    tagFileMap.remove(toRemoveTag);
                }
            }
        }
    }
}
