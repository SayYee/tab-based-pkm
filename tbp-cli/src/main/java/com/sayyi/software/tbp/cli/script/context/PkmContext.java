package com.sayyi.software.tbp.cli.script.context;

import java.io.IOException;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public abstract class PkmContext {

    /**
     * 初始化环境
     */
    public abstract void init() throws IOException;

    /**
     * 清理环境。默认什么都不做
     */
    public void clear() {

    }
}
