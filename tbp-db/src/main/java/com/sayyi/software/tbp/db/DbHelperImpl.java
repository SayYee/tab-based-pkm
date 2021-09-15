package com.sayyi.software.tbp.db;

import com.sayyi.software.tbp.common.TbpConfig;
import com.sayyi.software.tbp.common.TbpConfigParse;
import com.sayyi.software.tbp.common.TbpException;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.snap.Version;
import com.sayyi.software.tbp.common.snap.model.CurrentSnapshot;
import com.sayyi.software.tbp.common.store.BinaryInputArchive;
import com.sayyi.software.tbp.common.store.BinaryOutputArchive;
import com.sayyi.software.tbp.common.store.Record;
import com.sayyi.software.tbp.db.api.component.*;
import com.sayyi.software.tbp.db.component.FileHelperImpl;
import com.sayyi.software.tbp.db.component.MetadataDbImpl;
import com.sayyi.software.tbp.db.component.SelectorImpl;
import com.sayyi.software.tbp.db.component.TreeComponentImpl;
import com.sayyi.software.tbp.db.persistence.FileBasedPersistenceComponent;
import com.sayyi.software.tbp.db.persistence.PersistenceComponent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class DbHelperImpl implements DbHelper {

    private static final DbHelperImpl DB_HELPER_IMPL = new DbHelperImpl();
    private DbHelperImpl(){
        try {
            init();
        } catch (Exception e) {
            log.error("元数据管理组件启动失败", e);
            throw new TbpException("元数据管理组件启动失败");
        }
    }

    public static DbHelper getInstance() {
        return DB_HELPER_IMPL;
    }

    private Selector selector;
    private MetadataDb metadataDb;
    private PersistenceComponent persistenceComponent;
    private TreeComponent treeComponent;
    private FileHelper fileHelper;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public void init() throws IOException {
        TbpConfig tbpConfig = new TbpConfigParse(
                Objects.requireNonNull(DbHelperImpl.class.getClassLoader().getResource("tbp.cfg")).getPath());

        persistenceComponent = new FileBasedPersistenceComponent(tbpConfig.getSnapDir());
        selector = new SelectorImpl(readLock, writeLock);
        MetadataDb unWrappedDb = recovery(persistenceComponent, selector);

        // DB处理
        UpdaterProxy updaterProxy = new UpdaterProxy(unWrappedDb);
        metadataDb = (MetadataDb) Proxy.newProxyInstance(Updater.class.getClassLoader(), new Class[]{MetadataDb.class}, updaterProxy);

        treeComponent = new TreeComponentImpl(tbpConfig.getSnapDir());
        fileHelper = new FileHelperImpl(tbpConfig.getStoreDir());
    }

    private MetadataDb recovery(PersistenceComponent persistenceComponent, Selector selector) throws IOException {
        Version version = persistenceComponent.loadSnap();
        MetadataDb metadataDb;
        if (version != null) {
            CurrentSnapshot currentSnapshot = (CurrentSnapshot) version;
            lastOpId = currentSnapshot.getLastOpId();
            metadataDb = new MetadataDbImpl(currentSnapshot);
        } else {
            metadataDb = new MetadataDbImpl();
        }
        // 这个给个不含持久化的Db好了，对外暴露的，是包含持久化逻辑的Db
        metadataDb.setSelector(selector);
        selector.setMetadataDb(metadataDb);

        Recovery recovery = new Recovery(metadataDb);

        Iterator<Request> requestIterator = persistenceComponent.requestIterator(lastOpId);
        while (requestIterator.hasNext()) {
            Request request = requestIterator.next();
            // 允许等于
            if (request.getOpId() < lastOpId) {
                continue;
            }
            lastOpId = request.getOpId();
            try {
                // 用户在实际进行某些操作时，可能输入无效的参数，但是日志依然会被记录
                // 因此需要忽略这些异常，保证后续的行为正常提交
                recovery.recovery(request);
            } catch (Exception e) {
                log.warn("恢复请求时出现异常【{}】", e.getMessage());
            }
        }
        lastOpId++;

        // 存储新的快照
        CurrentSnapshot currentSnap = new CurrentSnapshot();
        currentSnap.setLastOpId(lastOpId);
        currentSnap.setLastFileId(metadataDb.getNextFileId());
        currentSnap.setFileMetadataList(metadataDb.listAll());

        persistenceComponent.storeSnap(currentSnap.getLastOpId(), currentSnap);

        // 清理过期的数据
        persistenceComponent.cleanOutOfDateFile(lastOpId);
        return metadataDb;
    }

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public MetadataDb getMetadata() {
        return metadataDb;
    }

    @Override
    public FileHelper getFileHelper() {
        return fileHelper;
    }

    @Override
    public TreeComponent getTreeComponent() {
        return treeComponent;
    }

    private long lastOpId = 0;

    /**
     * updater代理类，提供持久化逻辑
     */
    public class UpdaterProxy implements InvocationHandler {


        private final Updater updater;
        private final Map<Method, Integer> methodIntegerMap = new HashMap<>();

        public UpdaterProxy(Updater updater) {
            this.updater = updater;
            for (Method declaredMethod : Updater.class.getDeclaredMethods()) {
                final BindType annotation = declaredMethod.getAnnotation(BindType.class);
                int type = annotation.value();
                methodIntegerMap.put(declaredMethod, type);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals":
                    return equals(args[0]);
                case "hashCode":
                    return hashCode();
                case "toString":
                    return toString();
                default:
            }
            // 简简单单加个锁得了。
            writeLock.lock();
            try {
                // 从map中获取映射信息，获取不到，调用的是不需要持久化的方法，直接放行。
                Integer type = methodIntegerMap.get(method);
                if (type == null) {
                    return method.invoke(updater, args);
                }
                Request request = new Request();
                request.setOpId(lastOpId++);
                request.setOpType(type);
                request.setData(BinaryOutputArchive.serialize((Record) args[0]));
                persistenceComponent.storeRequest(request);

                return method.invoke(updater, args);
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * request恢复为方法调用的类
     */
    public static class Recovery {
        private final Updater updater;
        private final Map<Integer, Method> methodMap = new HashMap<>();
        private final Map<Method, Class<?>> methodFieldMap = new HashMap<>();

        public Recovery(Updater updater) {
            this.updater = updater;
            for (Method declaredMethod : Updater.class.getDeclaredMethods()) {
                final BindType annotation = declaredMethod.getAnnotation(BindType.class);
                int type = annotation.value();
                methodMap.put(type, declaredMethod);

                Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                methodFieldMap.put(declaredMethod, parameterType);
            }
        }

        public void recovery(Request request) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
            // 获取方法
            Method method = methodMap.get(request.getOpType());
            if (method == null) {
                throw new UnsupportedOperationException("未知方法调用【" + request.getOpType() + "】");
            }
            // 获取方法参数，反序列化参数，并调用方法
            Class<?> clazz = methodFieldMap.get(method);
            Record record = (Record) clazz.getDeclaredConstructor().newInstance();
            BinaryInputArchive.deserialize(record, request.getData());
            method.invoke(updater, record);
        }
    }
}
