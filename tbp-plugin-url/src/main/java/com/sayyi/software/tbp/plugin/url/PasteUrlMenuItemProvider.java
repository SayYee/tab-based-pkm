package com.sayyi.software.tbp.plugin.url;

import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.db.api.component.DbHelper;
import com.sayyi.software.tbp.ui.api.File2ObservableConverter;
import com.sayyi.software.tbp.ui.api.constant.MetadataColumnName;
import com.sayyi.software.tbp.ui.api.menu.MenuItemProvider;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;

public class PasteUrlMenuItemProvider implements MenuItemProvider {

    private final DbHelper dbHelper;

    public PasteUrlMenuItemProvider(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public MenuItem get(TableView<ObservableMetadata> tableView) {
        Menu menu = new Menu("粘贴url");
        MenuItem emptyItem = new MenuItem("----空----");
        menu.getItems().add(emptyItem);
        menu.setOnShowing(event -> {
            menu.getItems().clear();
            menu.getItems().add(emptyItem);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String string = clipboard.getString();
                if (string.startsWith("http")) {
                    String text = string;
                    if (text.length() > 20) {
                        text = string.substring(0, 20) + "...";
                    }
                    MenuItem urlItem = new MenuItem(text);
                    urlItem.setOnAction(t -> addUrlAction(string, tableView));
                    menu.getItems().add(urlItem);
                }
            }
        });
        return menu;
    }

    @SuppressWarnings("unchecked")
    private void addUrlAction(String url, TableView<ObservableMetadata> tableView) {
        // 组装元数据
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setResourceType(ResourceType.NET);
        fileMetadata.setFilename("未命名.html");
        fileMetadata.setResourcePath(dbHelper.getFileHelper().assignPath());
        fileMetadata.setTags((Set<String>) tableView.getUserData());
        // 创建文件
        File file = dbHelper.getFileHelper().getFile(fileMetadata);
        try {
            createHtmlFile(file, url);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // 持久化
        System.out.println("made data: " + fileMetadata);
        dbHelper.getMetadata().insert(fileMetadata);
        // 展示
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
    }

    public void createHtmlFile(File file, String url) throws IOException {
        Files.write(file.toPath(), generateHtml(url).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成url跳转的html文件内容
     * @param url   目标地址
     * @return
     */
    private String generateHtml(String url) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<title>url中转页面</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>页面跳转中</h1>\n" +
                "</body>\n" +
                "<script>\n" +
                "    var targetUrl = \"" + url + "\";\n" +
                "    document.write(\"<a href='\" + targetUrl + \"'>\" + targetUrl + \"</a>\");\n" +
                "    window.location.href = targetUrl;\n" +
                "</script>\n" +
                "</html>";
    }
}
