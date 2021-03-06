package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.db.DbHelperImpl;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;

public class OpenFileMenuItemProvider implements MenuItemProvider {
    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("打开");
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            selectedItems.forEach(observableMetadata -> {
                FileMetadata fileMetadata = DbHelperImpl.getInstance().getSelector().get(observableMetadata.getId());
                File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
                try {
                    FileUtil.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        return menuItem;
    }
}
