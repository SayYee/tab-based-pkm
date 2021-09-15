package com.sayyi.software.tbp.db.api.listener;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TbpEvent {

    /**
     * 事件类型
     */
    private int eventType;
    /**
     * 原数据
     */
    private Object oldValue;
    /**
     * 新的数据
     */
    private Object newValue;
}
