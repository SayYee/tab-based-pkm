package com.sayyi.software.tbp.ui.api.tab;

import javafx.scene.control.Tab;

public interface TabRegister {

    /**
     * tab中的content必须为Region的子类，从而可以进行宽高的自适应处理
     * @param tab
     */
    void registry(Tab tab);
}
