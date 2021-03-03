package com.sayyi.software.tbp.core.flow.processor;

import com.sayyi.software.tbp.common.constant.RequestType;
import com.sayyi.software.tbp.common.flow.Request;
import com.sayyi.software.tbp.common.flow.Response;
import com.sayyi.software.tbp.core.flow.BindType;

/**+
 * 处理器。返回值必须为boolean，为了方便判断是否需要继续向下传递.
 * 看了zk的源码后，我对switch产生了深深的恐惧，我希望自己的代码里能够避免类似的反复出现的东西。
 * 同时让关联的东西能够一起被修改。
 * 这么搞了之后，怎么有种在写controller的感觉？
 * @author SayYi
 */
public interface Processor {

    /**
     * 通过流的方式将文件加入系统
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.UPLOAD)
    boolean upload(Request request, Response response);

    /**
     * 本地文件复制
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.COPY)
    boolean copy(Request request, Response response);

    /**
     * 创建文件
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.CREATE)
    boolean create(Request request, Response response);

    /**
     * 将网络资源入库
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.ADD_URL)
    boolean addUrl(Request request, Response response);

    /**
     * 修改资源名称
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.RENAME)
    boolean rename(Request request, Response response);

    /**
     * 修改资源标签
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.MODIFY_RESOURCE_TAG)
    boolean modifyResourceTag(Request request, Response response);

    /**
     * 添加资源标签
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.ADD_RESOURCE_TAG)
    boolean addResourceTag(Request request, Response response);

    /**
     * 删除资源标签
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.DELETE_RESOURCE_TAG)
    boolean deleteResourceTag(Request request, Response response);

    /**
     * 打开文件
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.OPEN)
    boolean open(Request request, Response response);

    /**
     * 删除资源
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.DELETE)
    boolean delete(Request request, Response response);

    /**
     * 通过id获取资源信息
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.GET_BY_ID)
    boolean getById(Request request, Response response);

    /**
     * 查询资源信息
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.LIST_RESOURCES)
    boolean listResources(Request request, Response response);

    /**
     * 删除tag
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.DELETE_TAG)
    boolean deleteTag(Request request, Response response);

    /**
     * 重命名tag
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.RENAME_TAG)
    boolean renameTag(Request request, Response response);

    /**
     * 批量修改tag
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.BATCH_MODIFY_TAGS)
    boolean batchModifyTags(Request request, Response response);

    /**
     * 查询tag
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.LIST_TAGS)
    boolean listTags(Request request, Response response);

    /**
     * 获取tagMap
     * @param request
     * @param response
     * @return
     */
    @BindType(RequestType.TAG_MAP)
    boolean tagMap(Request request, Response response);

    /** 获取tree id列表 */
    @BindType(RequestType.LIST_TREE_IDS)
    default boolean listTreeIds(Request request, Response response) {
        return true;
    }
    /** 获取最新tree数据 */
    @BindType(RequestType.GET_CURRENT_TREE)
    default boolean getCurrentTree(Request request, Response response) {
        return true;
    }
    /** 获取指定tree数据 */
    @BindType(RequestType.GET_ASSIGN_TREE)
    default boolean getAssignTree(Request request, Response response) {
        return true;
    }
    /** 保存tree */
    @BindType(RequestType.SAVE_TREE)
    default boolean saveTree(Request request, Response response) {
        return true;
    }
}
