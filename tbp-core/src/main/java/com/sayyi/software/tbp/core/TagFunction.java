package com.sayyi.software.tbp.core;

import com.sayyi.software.tbp.common.TbpException;

import java.io.OutputStream;

/**
 * 标签支持的功能。
 * 后续可以支持 别名、色彩。标签的查询展示也可以再考虑考虑
 * @author SayYi
 */
public interface TagFunction {

    /**
     * 删除标签
     * @param tagName   标签名称
     * @throws TbpException
     */
    void deleteTag(String tagName) throws TbpException;

    /**
     * 重命名标签
     * @param tagName   标签名称
     * @param newName   新的标签名称
     * @throws TbpException
     */
    void renameTag(String tagName, String newName) throws TbpException;

    /**
     * 获取标签图信息
     * @param out   输出流。图信息会被写入流中
     * @throws TbpException
     */
    void tagMap(OutputStream out) throws TbpException;
}
