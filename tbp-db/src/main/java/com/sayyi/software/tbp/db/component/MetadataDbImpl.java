package com.sayyi.software.tbp.db.component;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.model.DeleteOp;
import com.sayyi.software.tbp.common.model.UpdateTagsOp;
import com.sayyi.software.tbp.common.snap.model.CurrentSnapshot;
import com.sayyi.software.tbp.db.api.component.MetadataDb;
import com.sayyi.software.tbp.db.api.component.Selector;
import com.sayyi.software.tbp.db.api.listener.TbpEvent;
import com.sayyi.software.tbp.db.api.listener.TbpEventType;
import com.sayyi.software.tbp.db.api.listener.TbpListener;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 这个类不做并发安全控制，交给代理类来控制。
 * 因为这里涉及一个请求有序性处理的问题，保证操作id大的数据是在id小的请求前被处理
 * 而且个人使用的软件，不太有必要做太多并发方面的设计。一切从简
 */
@Slf4j
public class MetadataDbImpl implements MetadataDb {

    /**
     * 所有的文件元数据信息
     */
    private final List<FileMetadata> fileMetadataList = new ArrayList<>();
    /**
     * 下一个文件id
     */
    private long nextFileId = 1;

    public MetadataDbImpl() {}

    public MetadataDbImpl(CurrentSnapshot currentSnapshot) {
        if (currentSnapshot.getLastOpId() == -1) {
            return;
        }
        nextFileId = currentSnapshot.getLastFileId();
        fileMetadataList.addAll(currentSnapshot.getFileMetadataList());
    }

    private Selector selector;

    @Override
    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public List<FileMetadata> listAll() {
        return fileMetadataList;
    }

    @Override
    public long getNextFileId() {
        return nextFileId;
    }

    private final List<TbpListener> listeners = new LinkedList<>();

    @Override
    public void addListener(TbpListener tbpListener) {
        listeners.add(tbpListener);
    }

    @Override
    public void removeListener(TbpListener tbpListener) {
        listeners.remove(tbpListener);
    }

    private void fireEvent(TbpEvent event) {
        // 这里是不是多线程处理比较好？
        // 但是异步也不太合适，因为这里更新要用到selector中的缓存，需要保证那边第一个更新、及时更新才行
        for (TbpListener listener : listeners) {
            if ((listener.getInterestEvent() & event.getEventType()) != 0) {
                listener.call(event);
            }
        }
    }

    @Override
    public FileMetadata insert(FileMetadata metadata) {
        // 复制还是直接使用？
        metadata.setId(nextFileId++);
        // 空标签集合，应该使用空集合，而不是null来表示
        metadata.setTags(metadata.getTags() == null ? new HashSet<>() : new HashSet<>(metadata.getTags()));
        metadata.setCreateTime(System.currentTimeMillis());
        metadata.setLastOpenTime(System.currentTimeMillis());

        fileMetadataList.add(metadata);
        fireEvent(new TbpEvent(TbpEventType.ADD, null, metadata));
        return metadata;
    }

    @Override
    public FileMetadata update(FileMetadata metadata) {
        // 直接替换引用，是否合适？这会导致封装效果变差啊……
        FileMetadata fileMetadata = selector.get(metadata.getId());
        int tbpEventType = 0;
        FileMetadata oldMetadata = new FileMetadata();
        oldMetadata.setId(fileMetadata.getId());
        // 可以更新的内容
        if (metadata.getFilename() != null
                && !fileMetadata.getFilename().equals(metadata.getFilename())) {
            tbpEventType |= TbpEventType.MODIFY_NAME;
            oldMetadata.setFilename(fileMetadata.getFilename());
            fileMetadata.setFilename(metadata.getFilename());
        }
        // 路径变更
        if (metadata.getResourcePath() != null
                && !Arrays.equals(fileMetadata.getResourcePath(), metadata.getResourcePath())) {
            tbpEventType |= TbpEventType.MODIFY_PATH;
            oldMetadata.setResourcePath(fileMetadata.getResourcePath());
            fileMetadata.setResourcePath(metadata.getResourcePath());
        }
        //  标签变更
        if (metadata.getTags() != null
                && !fileMetadata.getTags().equals(metadata.getTags())) {
            tbpEventType |= TbpEventType.MODIFY_TAGS;
            oldMetadata.setTags(fileMetadata.getTags());
            fileMetadata.setTags(new HashSet<>(metadata.getTags()));
        }
        // 最后更新时间变更
        if (metadata.getLastOpenTime() != 0 &&
                fileMetadata.getLastOpenTime() != metadata.getLastOpenTime()) {
            tbpEventType |= TbpEventType.MODIFY_UPDATE_TIME;
            oldMetadata.setLastOpenTime(fileMetadata.getLastOpenTime());
            fileMetadata.setLastOpenTime(metadata.getLastOpenTime());
        }
        if (tbpEventType != 0) {
            fireEvent(new TbpEvent(tbpEventType, oldMetadata, fileMetadata));
        }
        return fileMetadata;
    }

    @Override
    public FileMetadata delete(DeleteOp deleteOp) {
        FileMetadata fileMetadata = selector.get(deleteOp.getId());
        fileMetadataList.remove(fileMetadata);
        fireEvent(new TbpEvent(TbpEventType.REMOVE, fileMetadata, null));
        return fileMetadata;
    }

    @Override
    public void updateTags(UpdateTagsOp updateTagsOp) {
        Set<String> oldTags = updateTagsOp.getOldTags();
        Set<String> newTags = updateTagsOp.getNewTags();
        Set<String> toAddTags = newTags.stream().filter(s -> !oldTags.contains(s)).collect(Collectors.toSet());
        Set<String> toRemoveTags = oldTags.stream().filter(s -> !newTags.contains(s)).collect(Collectors.toSet());
        log.debug("toAddTags: {}", toAddTags);
        log.debug("toRemoveTags: {}", toRemoveTags);
        List<FileMetadata> fileMetadatas = selector.list(oldTags, null);
        for (FileMetadata fileMetadata : fileMetadatas) {
            FileMetadata oldMetadata = new FileMetadata();
            oldMetadata.setId(fileMetadata.getId());
            oldMetadata.setTags(new HashSet<>(fileMetadata.getTags()));

            fileMetadata.getTags().addAll(toAddTags);
            fileMetadata.getTags().removeAll(toRemoveTags);

            fireEvent(new TbpEvent(TbpEventType.MODIFY_TAGS, oldMetadata, fileMetadata));
        }
    }
}
