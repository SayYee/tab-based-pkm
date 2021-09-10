package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.client.model.ObservableMetadata;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.StringJoiner;

/**
 * 复制文件路径
 */
public class CopyPathMenuItemProvider implements MenuItemProvider{
    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("复制路径");
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            // TODO 数据填充：获取文件路径
            StringJoiner joiner = new StringJoiner("\r\n");
            selectedItems.forEach(observableMetadata -> joiner.add(observableMetadata.getName()));
            ClipboardContent content = new ClipboardContent();
            content.putString(joiner.toString());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);
        });
        return menuItem;
    }
}
