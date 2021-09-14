package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.client.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.common.model.update.DeleteOp;
import com.sayyi.software.tbp.db.DbHelper;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeleteFileMenuItemProvider implements MenuItemProvider{
    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("删除");
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            for (ObservableMetadata selectedItem : selectedItems) {
                FileMetadata fileMetadata = DbHelper.getInstance().getSelector().get(selectedItem.getId());
                File file = DbHelper.getInstance().getFileHelper().getFile(fileMetadata);
                if (file.exists()) {
                    try {
                        FileUtil.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                }

                DeleteOp deleteOp = new DeleteOp();
                deleteOp.setId(fileMetadata.getId());
                DbHelper.getInstance().getMetadata().delete(deleteOp);

                tableView.getItems().remove(selectedItem);
            }
        });
        return menuItem;
    }
}
