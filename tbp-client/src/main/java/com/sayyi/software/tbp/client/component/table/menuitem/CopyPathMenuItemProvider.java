package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.db.DbHelperImpl;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.File;
import java.util.StringJoiner;

/**
 * 复制文件路径
 */
public class CopyPathMenuItemProvider implements MenuItemProvider {

    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("复制路径");
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            StringJoiner joiner = new StringJoiner("\r\n");
            selectedItems.forEach(observableMetadata -> {
                FileMetadata fileMetadata = DbHelperImpl.getInstance().getSelector().get(observableMetadata.getId());
                File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
                joiner.add(file.getAbsolutePath());
            });
            ClipboardContent content = new ClipboardContent();
            content.putString(joiner.toString());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);
        });
        return menuItem;
    }
}
