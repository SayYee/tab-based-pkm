package com.sayyi.software.tbp.client.component.table.menuitem;

import cn.hutool.poi.excel.reader.AbstractSheetReader;
import com.sayyi.software.tbp.client.component.table.MetadataTableView;
import com.sayyi.software.tbp.client.component.util.File2ObservableConverter;
import com.sayyi.software.tbp.client.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.db.DbHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class NewDirectoryMenuItemProvider implements MenuItemProvider {

    @Override
    @SuppressWarnings("unchecked")
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        MenuItem menuItem = new MenuItem("新建文件夹");
        menuItem.setOnAction(event -> {
            // 创建对象
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setResourceType(ResourceType.LOCAL);
            fileMetadata.setFilename("新建文件夹");
            fileMetadata.setResourcePath(DbHelper.getInstance().getFileHelper().assignPath());
            fileMetadata.setTags((Set<String>) tableView.getUserData());

            // 创建文件夹
            File file = DbHelper.getInstance().getFileHelper().getFile(fileMetadata);
            try {
                Files.createDirectory(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // 插入元数据
            DbHelper.getInstance().getMetadata().insert(fileMetadata);
            // 页面展示
            ObservableMetadata observableMetadata = File2ObservableConverter.convert(fileMetadata);
            tableView.getItems().add(observableMetadata);
            int index = tableView.getItems().indexOf(observableMetadata);
            tableView.getSelectionModel().select(observableMetadata);
            for (TableColumn<ObservableMetadata, ?> column : tableView.getColumns()) {
                if (MetadataTableView.ColumnName.NAME.equals(column.getText())) {
                    tableView.edit(index, column);
                    break;
                }
            }
        });
        return menuItem;
    }
}
