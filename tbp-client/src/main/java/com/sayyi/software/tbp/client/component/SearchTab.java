package com.sayyi.software.tbp.client.component;

import com.sayyi.software.tbp.client.component.table.MetadataTableView;
import com.sayyi.software.tbp.client.component.table.converter.SetStringConverter;
import com.sayyi.software.tbp.client.model.ObservableMetadata;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * 这个类，自己具有完备的逻辑
 */
public class SearchTab {

    private final TabPane parent;
    private final Tab tab;
    private final VBox vBox;
    private SearchableTextField inputField;
    private MetadataTableView metadataTableView;

    public SearchTab(TabPane parent) {
        this("", parent);
    }

    public SearchTab(String title, TabPane parent) {
        this.parent = parent;
        tab = new Tab(title);
        vBox = new VBox(10);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        initTextField();
        initTableView();
        bindTextAndTable();
        TableView<ObservableMetadata> tableView = metadataTableView.getTableView();
        vBox.getChildren().addAll(inputField, tableView);
        tab.setContent(vBox);

        tableView.prefHeightProperty().bind(parent.heightProperty().subtract(35));
    }

    public void initTextField() {
        inputField = new SearchableTextField();
        inputField.setPromptText("输入检索信息");
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            // 输入点号，触发数据更新，并展示下拉框
            if (newValue.endsWith(".")) {
                // TODO 提供下拉菜单数据
                inputField.updateItems(Arrays.asList("just for test", "default message"));
                inputField.showMenu();
            }
            // TODO 否则进行下拉框的数据过滤
        });
        // TODO 点击item时执行的操作
        inputField.setItemAction(inputField::appendText);
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
        // 双击事件
        tableRow.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                ObservableMetadata item = tableRow.getItem();
                // TODO 打开文件
                System.out.println("row clicked");
                event.consume();
            }
        });
        // 多选拖拽，挺常见的吧……
        // 这里做成多个的，感觉也可以接收吧
        tableRow.setOnDragDetected(event -> {
            Dragboard dragboard = tableRow.startDragAndDrop(TransferMode.COPY);
            // TODO 多文件拖拽功能实现
            ClipboardContent content = new ClipboardContent();
            content.putString("hello");
//            WritableImage snapshot = tableRow.snapshot(new SnapshotParameters(), null);
//            dragboard.setDragView(snapshot);
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
                tableRow.getItem().tagsProperty().addAll(toAddTags);
                event.consume();
            }
            // TODO 是不是可以考虑接收文件？拖拽进入，添加文件
        });
        return tableRow;
    }

    /**
     * 建立text和tableView组件直接的关联
     */
    private void bindTextAndTable() {
        inputField.setOnAction(event -> {
            ObservableList<ObservableMetadata> observableMetadata = loadMetadata(inputField.getText());
            metadataTableView.setMetadata(observableMetadata);
        });
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
    private ObservableList<ObservableMetadata> loadMetadata(String tagStr) {
        // TODO 加载真正的数据
        List<ObservableMetadata> list = Arrays.asList(
                new ObservableMetadata(1, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(2, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(3, "one", 1, new HashSet<>(Arrays.asList("hello", "world", "wrap", "please", "help")), new Date().getTime()),
                new ObservableMetadata(4, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(5, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(6, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(7, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(8, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(9, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(10, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(11, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(12, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(13, "one", 2, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(14, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(15, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(16, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime()),
                new ObservableMetadata(17, "one", 1, new HashSet<>(Arrays.asList("hello", "world")), new Date().getTime())
        );
        // 针对 name、tags、lastUpdateTime 字段的更新，会被观测到。
        ObservableList<ObservableMetadata> observableMetadata = FXCollections.observableList(list,
                param -> new Observable[]{param.nameProperty(), param.tagsProperty(), param.lastUpdateTimeProperty()});
        // TODO 执行持久化操作
        observableMetadata.addListener((ListChangeListener<ObservableMetadata>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    // 排序相关，不用管
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        //permutate
                    }
                } else if (c.wasUpdated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        // 这里获取到的，是更新后的数据
                        ObservableMetadata updated = observableMetadata.get(i);
                        System.out.println("name: " + updated.getName() + ", tags: " + updated.getTags());
                    }
                } else {
                    for (ObservableMetadata remitem : c.getRemoved()) {
                        //删除
                        System.out.println("remove: " + c);
                    }
                    for (ObservableMetadata additem : c.getAddedSubList()) {
                        // 添加
                        System.out.println("add: " + c);
                    }
                }
            }
        });
        return observableMetadata;

    }

}
