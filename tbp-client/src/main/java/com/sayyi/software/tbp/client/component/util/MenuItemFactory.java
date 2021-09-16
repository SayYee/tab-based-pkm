package com.sayyi.software.tbp.client.component.util;

import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;
import com.sayyi.software.tbp.ui.api.menu.MenuItemProviderRegister;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * menuItem工厂
 */
@Slf4j
public class MenuItemFactory implements MenuItemProviderRegister {

    private static final MenuItemFactory instance = new MenuItemFactory();

    public static MenuItemFactory getInstance() {
        return instance;
    }

    private MenuItemFactory() {}

    private static final List<MenuItemProvider> menuItemProviders = new ArrayList<>();

    /**
     * 注册右键菜单生成组件
     * @param menuItemProvider
     */
    @Override
    public void registry(MenuItemProvider menuItemProvider) {
        log.debug("新的菜单提供者注册【{}】", menuItemProvider);
        menuItemProviders.add(menuItemProvider);
    }

    /**
     * 生成tableView对应的右键菜单项
     * @param tableView
     * @return
     */
    public List<MenuItem> generateMenuItem(TableView<ObservableMetadata> tableView) {
        return menuItemProviders.stream().map(menuItemProvider -> menuItemProvider.get(tableView)).collect(Collectors.toList());
    }
}
