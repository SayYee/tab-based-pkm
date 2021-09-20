package com.sayyi.software.tbp.client.component.table.menuitem;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.db.DbHelperImpl;
import com.sayyi.software.tbp.ui.api.File2ObservableConverter;
import com.sayyi.software.tbp.ui.api.constant.MetadataColumnName;
import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class NewFileMenuItemProvider implements MenuItemProvider {

    @Override
    @SuppressWarnings("unchecked")
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("新建文件");
        menuItem.setOnAction(event -> {
            // 创建对象
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setResourceType(ResourceType.LOCAL);
            fileMetadata.setFilename("新建文件.md");
            fileMetadata.setResourcePath(DbHelperImpl.getInstance().getFileHelper().assignPath());
            fileMetadata.setTags((Set<String>) tableView.getUserData());

            // 创建文件
            File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // 插入元数据
            DbHelperImpl.getInstance().getMetadata().insert(fileMetadata);
            // 页面展示
            ObservableMetadata observableMetadata = File2ObservableConverter.convert(fileMetadata);
            tableView.getItems().add(observableMetadata);
            int index = tableView.getItems().indexOf(observableMetadata);
            tableView.getSelectionModel().clearAndSelect(index);
            tableView.scrollTo(index);
            tableView.getFocusModel().focus(index);
            for (TableColumn<ObservableMetadata, ?> column : tableView.getColumns()) {
                if (MetadataColumnName.NAME.equals(column.getText())) {
                    tableView.edit(index, column);
                    break;
                }
            }
        });
        return menuItem;
    }
}
