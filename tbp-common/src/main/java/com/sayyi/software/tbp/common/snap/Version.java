package com.sayyi.software.tbp.common.snap;

import com.sayyi.software.tbp.common.store.Record;

/**
 *
 * snapshot的版本信息接口
 * @author xuchuang
 * @date 2021/5/20
 */
public interface Version extends Record {

    int version();
}
