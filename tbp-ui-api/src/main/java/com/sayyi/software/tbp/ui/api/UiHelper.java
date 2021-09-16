package com.sayyi.software.tbp.ui.api;

import com.sayyi.software.tbp.ui.api.menu.MenuItemProviderRegister;
import com.sayyi.software.tbp.ui.api.sidebar.SidebarToolRegister;
import com.sayyi.software.tbp.ui.api.tab.TabRegister;

public interface UiHelper {

    /**
     * 元数据检索页菜单注册组件
     * @return
     */
    MenuItemProviderRegister getMenuItemProviderRegister();

    /**
     * tab注册组件
     * @return
     */
    TabRegister getTabRegister();

    /**
     * 侧边栏工具注册组件
     * @return
     */
    SidebarToolRegister getSidebarTollRegister();
}
