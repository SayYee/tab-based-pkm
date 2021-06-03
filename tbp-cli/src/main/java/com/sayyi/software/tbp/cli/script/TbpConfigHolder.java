package com.sayyi.software.tbp.cli.script;

import com.sayyi.software.tbp.common.TbpConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TbpConfigHolder {

    private static TbpConfig instance;

    public static void set(TbpConfig tbpConfig) {
        if (instance != null) {
            log.warn("tbp config function实例了");
            return;
        }
        instance = tbpConfig;
    }

    public static TbpConfig get() {
        if (instance == null) {
            throw new RuntimeException("tbp config function实例未设置，请先调用 set 方法设置");
        }
        return instance;
    }
}
