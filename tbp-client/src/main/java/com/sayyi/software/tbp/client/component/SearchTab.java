package com.sayyi.software.tbp.client.component;

import com.sayyi.software.tbp.client.component.table.MetadataTableView;
import com.sayyi.software.tbp.client.component.table.converter.SetStringConverter;
import com.sayyi.software.tbp.client.component.util.File2ObservableConverter;
import com.sayyi.software.tbp.client.model.ObservableMetadata;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.db.DbHelper;
import com.sayyi.software.tbp.db.component.Selector;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 这个类，自己具有完备的逻辑
 */
public class SearchTab {

    private final TabPane parent;
    private final Tab tab;
    private final VBox vBox;
    private SearchableTextField inputField;
    private TextField nameField;
    private MetadataTableView metadataTableView;

    public SearchTab(TabPane parent) {
        this(null, parent);
    }

    private final String TITLE_PREFIX = "检索：";

    public SearchTab(String title, TabPane parent) {
        title = title == null ? "" : title;
        this.parent = parent;
        tab = new Tab(title);
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        initTextField();
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(inputField, nameField);

        initTableView();
        bindTextAndTable();
        TableView<ObservableMetadata> tableView = metadataTableView.getTableView();
        vBox.getChildren().addAll(hBox, tableView);
        tab.setContent(vBox);

        tableView.prefHeightProperty().bind(parent.heightProperty().subtract(35));
        inputField.prefWidthProperty().bind(hBox.widthProperty().subtract(nameField.prefWidthProperty()).subtract(10));

        inputField.setText(title);
        inputField.fireEvent(new ActionEvent());
    }

    public void initTextField() {
        inputField = new SearchableTextField();
        inputField.setPromptText("输入检索信息");

        nameField = new TextField();
        nameField.setPromptText("名称正则表达式");
        nameField.setPrefWidth(200);
    }

    public void initTableView() {
        metadataTableView = new MetadataTableView();
        // 这里设置 双击、拖拽的处理
        TableView<ObservableMetadata> tableView = metadataTableView.getTableView();
        tableView.setRowFactory(param -> initTableRow());
    }

    /**
     * 这里仅仅进行单行的操作，批量化的操作，就放在TableView的右键菜单中进行了
     * 1、双击打开文件；2、字符串拖拽添加标签；3、文件拖拽进入操作系统
     * @return
     */
    private TableRow<ObservableMetadata> initTableRow() {
        TableRow<ObservableMetadata> tableRow = new TableRow<>();
        // 双击事件：打开文件
        tableRow.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                ObservableMetadata item = tableRow.getItem();
                if (item == null) {
                    return;
                }
                FileMetadata fileMetadata = DbHelper.getInstance().getSelector().get(item.getId());
                File file = DbHelper.getInstance().getFileHelper().getFile(fileMetadata);
                try {
                    FileUtil.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.consume();
            }
        });
        // 多选拖拽，挺常见的吧……
        // 这里做成多个的，感觉也可以接受吧
        tableRow.setOnDragDetected(event -> {
            // 发起复制操作
            Dragboard dragboard = tableRow.startDragAndDrop(TransferMode.COPY);

            ObservableList<ObservableMetadata> selectedItems = tableRow.getTableView().getSelectionModel().getSelectedItems();
            List<File> list = new ArrayList<>();
            for (ObservableMetadata selectedItem : selectedItems) {
                FileMetadata fileMetadata = DbHelper.getInstance().getSelector().get(selectedItem.getId());
                File file = DbHelper.getInstance().getFileHelper().getFile(fileMetadata);
                list.add(file);
            }
            ClipboardContent content = new ClipboardContent();
            content.putFiles(list);
            dragboard.setContent(content);
        });

        // 允许接收拖拽事件
        tableRow.setOnDragOver(event -> {
            // 空白条目不能接收这个处理
            if (tableRow.getItem() == null) {
                return;
            }
            event.acceptTransferModes(TransferMode.COPY);
        });
        tableRow.setOnDragEntered(event -> tableRow.getStyleClass().add("tag-drag-enter"));
        tableRow.setOnDragExited(event -> tableRow.getStyleClass().remove("tag-drag-enter"));
        // 接收拖拽进入的字符串
        tableRow.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            // 接收字符串
            if (dragboard.hasString()) {
                String tagsStr = dragboard.getString();
                Set<String> toAddTags = SetStringConverter.getInstance().fromString(tagsStr);
                ObservableMetadata item = tableRow.getItem();
                Set<String> tags = new HashSet<>();
                tags.addAll(toAddTags);
                tags.addAll(item.getTags());
                FileMetadata fileMetadata = new FileMetadata();
                fileMetadata.setId(item.getId());
                fileMetadata.setTags(tags);
                DbHelper.getInstance().getMetadata().update(fileMetadata);

                item.getTags().addAll(toAddTags);
                event.consume();
            }
        });
        return tableRow;
    }

    /**
     * 建立text和tableView组件直接的关联
     */
    private void bindTextAndTable() {
        inputField.setOnAction(event -> search());
        nameField.setOnAction(event -> search());
    }

    private void search() {
        ObservableList<ObservableMetadata> observableMetadata = loadMetadata(inputField.getText(), nameField.getText());
        metadataTableView.setMetadata(inputField.getText(), observableMetadata);
        tab.setText(TITLE_PREFIX + inputField.getText());
    }

    public Tab getSearchTab() {
        return tab;
    }

    public TextField getTextField() {
        return inputField;
    }

    public TableView<ObservableMetadata> getTableView() {
        return metadataTableView.getTableView();
    }

    /**
     * 通过标签字符串加载数据
     * @param tagStr
     * @return
     */
    private ObservableList<ObservableMetadata> loadMetadata(String tagStr, String nameReg) {
        Selector selector = DbHelper.getInstance().getSelector();
        List<FileMetadata> fileMetadataList = selector.list(SetStringConverter.getInstance().fromString(tagStr), nameReg);
        List<ObservableMetadata> observableMetadataList = fileMetadataList.stream()
                .map(File2ObservableConverter::convert)
                .collect(Collectors.toList());
        // 针对 name、tags、lastUpdateTime 字段的更新，会被观测到。
        // 这里不监听数据的变化，因为应该先修改持久化数据，成功来再展示页面
        return FXCollections.observableList(observableMetadataList,
                param -> new Observable[]{param.nameProperty(), param.tagsProperty(), param.lastUpdateTimeProperty()});

    }

}
