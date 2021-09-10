package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.client.model.ObservableMetadata;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

public class OpenFileMenuItemProvider implements MenuItemProvider{
    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("打开");
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            // TODO 文件打开逻辑
            selectedItems.forEach(observableMetadata -> System.out.println(observableMetadata.getName()));
            // 有一个getHostService的东西，似乎也可以打开文件
        });
        return menuItem;
    }
}
