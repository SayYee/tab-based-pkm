package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.client.model.ObservableMetadata;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * menuItem工厂
 */
public class MenuItemFactory {

    private static final List<MenuItemProvider> menuItemProviders = new ArrayList<>();

    static {
        // TODO 开放插件注册程序。需要添加扫描逻辑
        menuItemProviders.add(new OpenFileMenuItemProvider());
        menuItemProviders.add(new DeleteFileMenuItemProvider());
        menuItemProviders.add(new CopyFileMenuItemProvider());
        menuItemProviders.add(new CopyPathMenuItemProvider());
    }

    /**
     * 生成tableView对应的右键菜单项
     * @param tableView
     * @return
     */
    public static List<MenuItem> getAll(TableView<ObservableMetadata> tableView) {
        return menuItemProviders.stream().map(menuItemProvider -> menuItemProvider.get(tableView)).collect(Collectors.toList());
    }
}