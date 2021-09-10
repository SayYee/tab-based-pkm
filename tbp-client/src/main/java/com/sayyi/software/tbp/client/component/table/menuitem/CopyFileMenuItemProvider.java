package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.client.model.ObservableMetadata;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.StringJoiner;

public class CopyFileMenuItemProvider implements MenuItemProvider{

    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("复制");
        // 点击按钮时要执行的操作
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            // 获取文件地址，然后放入剪切板
            ClipboardContent content = new ClipboardContent();
            // TODO 填充文件内容
//            content.putFiles();
            // 暂时先用文件名演示
            StringJoiner joiner = new StringJoiner(", ");
            selectedItems.forEach(observableMetadata -> joiner.add(observableMetadata.getName()));
            content.putString(joiner.toString());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);
        });
        return menuItem;
    }
}
