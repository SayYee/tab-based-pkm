package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.Snapshot;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.flow.ProcessorPipeline;
import com.sayyi.software.tbp.core.flow.processor.FinalProcessor;
import com.sayyi.software.tbp.core.flow.processor.PrepProcessor;
import com.sayyi.software.tbp.core.flow.processor.SyncProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author SayYi
 */
@Slf4j
public class PkmServiceImpl implements PkmService {

    private final FileManager fileManager;
    private final MetadataFunction metadataManager;
    private final DbFunction dbFunction;

    private final PrepProcessor prepProcessor;
    private final SyncProcessor syncProcessor;
    private final FinalProcessor finalProcessor;
    private final ProcessorPipeline processorPipeline;

    private long nextOpId = 1;

    public PkmServiceImpl(FileManager fileManager, MetadataFunction metadataManager, DbFunction dbFunction) {
        this.fileManager = fileManager;
        this.metadataManager = metadataManager;
        this.dbFunction = dbFunction;

        prepProcessor = new PrepProcessor(fileManager, metadataManager);
        syncProcessor = new SyncProcessor(dbFunction);
        finalProcessor = new FinalProcessor(metadataManager);
        processorPipeline = new ProcessorPipeline();

        processorPipeline.addFirst(finalProcessor);
        recovery();
        processorPipeline.addFirst(syncProcessor);
        processorPipeline.addFirst(prepProcessor);
    }

    public void recovery() {
        log.info("从本地恢复数据");
        long currentTime = System.currentTimeMillis();
        try {
            Snapshot snapshot = dbFunction.loadSnap();
            if (snapshot.getLastOpId() != -1) {
                nextOpId = snapshot.getLastOpId();
                metadataManager.recovery(snapshot);
            }
            Iterator<Request> requestIterator = dbFunction.requestIterator(nextOpId);
            Response response = new Response();
            while (requestIterator.hasNext()) {
                Request request = requestIterator.next();
                if (request.getOpId() < nextOpId) {
                    continue;
                }
                try {
                    // 用户在实际进行某些操作时，可能输入无效的参数，但是日志依然会被记录
                    // 因此需要忽略这些异常，保证后续的行为正常提交
                    processorPipeline.deal(request, response);
                } catch (Exception e) {
                    log.warn("恢复请求时出现异常【{}】", e.getMessage());
                }
            }
            // 更新opId
            if (processorPipeline.getLastProcessedOpId() != -1) {
                nextOpId = processorPipeline.getLastProcessedOpId() + 1;
            }

            // 存储新的快照
            Snapshot currentSnap = new Snapshot();
            currentSnap.setLastOpId(nextOpId);
            currentSnap.setLastFileId(metadataManager.getNextFileId());
            currentSnap.setFileMetadataList(metadataManager.listAllFile());
            dbFunction.storeSnap(currentSnap);

            // 清理过期的数据
            dbFunction.cleanOutOfDateFile(nextOpId);
        } catch (IOException e) {
            throw new TbpException(e.getMessage());
        }
        log.info("数据恢复完成，耗时【{}ms】", System.currentTimeMillis() - currentTime);
    }

    @Override
    public Response deal(int requestType, byte[] data) {
        Request request = new Request();
        request.setOpId(nextOpId++);
        request.setOpType(requestType);
        request.setData(data);
        Response response = new Response();
        processorPipeline.deal(request, response);
        return response;
    }
}
