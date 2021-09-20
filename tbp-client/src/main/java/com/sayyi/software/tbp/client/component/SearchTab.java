package com.sayyi.software.tbp.client.component;

import com.sayyi.software.tbp.client.component.table.MetadataTableView;
import com.sayyi.software.tbp.ui.api.component.SearchableTextField;
import com.sayyi.software.tbp.ui.api.converter.SetStringConverter;
import com.sayyi.software.tbp.ui.api.File2ObservableConverter;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.db.DbHelperImpl;
import com.sayyi.software.tbp.db.api.component.Selector;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class SearchTab {

    private final Tab tab;
    private final VBox vBox;
    private SearchableTextField inputField;
    private TextField nameField;
    private MetadataTableView metadataTableView;

    public SearchTab() {
        this(null);
    }

    private final String TITLE_PREFIX = "检索：";

    public SearchTab(String title) {
        log.debug("初始化检索条件【{}】", title);
        title = title == null ? "" : title;
        tab = new Tab(title);
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        initTextField(title);
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(inputField, nameField);

        initTableView();
        bindTextAndTable();
        TableView<ObservableMetadata> tableView = metadataTableView.getTableView();
        vBox.getChildren().addAll(hBox, tableView);
        tab.setContent(vBox);

        tableView.prefHeightProperty().bind(vBox.heightProperty().subtract(35));
        inputField.prefWidthProperty().bind(hBox.widthProperty().subtract(nameField.prefWidthProperty()).subtract(10));

        // 发布事件，执行查询
        inputField.fireEvent(new ActionEvent());
    }

    public void initTextField(String initStr) {
        inputField = new SearchableTextField(initStr, DbHelperImpl.getInstance().getSelector());
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
                FileMetadata fileMetadata = DbHelperImpl.getInstance().getSelector().get(item.getId());
                File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
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
                FileMetadata fileMetadata = DbHelperImpl.getInstance().getSelector().get(selectedItem.getId());
                File file = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
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
                DbHelperImpl.getInstance().getMetadata().update(fileMetadata);

                item.getTags().addAll(toAddTags);
                tableRow.getTableView().getSelectionModel().clearAndSelect(tableRow.getIndex());
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
        Selector selector = DbHelperImpl.getInstance().getSelector();
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
