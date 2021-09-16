package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.util.MenuItemFactory;
import com.sayyi.software.tbp.client.component.util.SidebarToolFactory;
import com.sayyi.software.tbp.ui.api.UiHelper;
import com.sayyi.software.tbp.ui.api.menu.MenuItemProviderRegister;
import com.sayyi.software.tbp.ui.api.sidebar.SidebarToolRegister;
import com.sayyi.software.tbp.ui.api.tab.TabRegister;

public class UiHelperImpl implements UiHelper {

    private static final UiHelperImpl instance = new UiHelperImpl();
    private UiHelperImpl() {}

    public static UiHelper getInstance() {
        return instance;
    }

    @Override
    public MenuItemProviderRegister getMenuItemProviderRegister() {
        return MenuItemFactory.getInstance();
    }

    @Override
    public TabRegister getTabRegister() {
        return MainPane.getInstance();
    }

    @Override
    public SidebarToolRegister getSidebarTollRegister() {
        return SidebarToolFactory.getInstance();
    }
}
