package com.sayyi.software.tbp.ui.api.menu;

import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

/**
 * tableView右键菜单项提供组件
 */
public interface MenuItemProvider {

    MenuItem get(TableView<ObservableMetadata> tableView);
}
