package com.sayyi.software.tbp.cli.script;

import com.sayyi.software.tbp.core.facade.PkmFunction;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * pkm function 实例holder
 * @author xuchuang
 * @date 2021/6/1
 */
@Slf4j
public class PkmFunctionHolder {

    private static PkmFunction instance;

    public static void set(PkmFunction pkmFunction) {
        if (instance != null) {
            log.warn("已经存储了pkm function实例了");
            return;
        }
        instance = pkmFunction;
    }

    public static PkmFunction get() {
        if (instance == null) {
            throw new RuntimeException("pkm function实例未设置，请先调用 set 方法设置");
        }
        return instance;
    }
}
