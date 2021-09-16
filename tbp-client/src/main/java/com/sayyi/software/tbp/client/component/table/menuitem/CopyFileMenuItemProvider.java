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
import java.util.ArrayList;
import java.util.List;

public class CopyFileMenuItemProvider implements MenuItemProvider {

    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("复制");
        // 点击按钮时要执行的操作
        menuItem.setOnAction(event -> {
            ObservableList<ObservableMetadata> selectedItems = tableView.getSelectionModel().getSelectedItems();
            // 获取文件地址，然后放入剪切板
            ClipboardContent content = new ClipboardContent();
            List<File> files = new ArrayList<>();
            selectedItems.forEach(observableMetadata -> {
                FileMetadata fileMetadata = DbHelperImpl.getInstance().getSelector().get(observableMetadata.getId());
                File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
                files.add(file);
            });
            content.putFiles(files);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);
        });
        return menuItem;
    }
}
