package com.sayyi.software.tbp.client.component.table;

import com.sayyi.software.tbp.client.component.table.converter.LongDataStringConverter;
import com.sayyi.software.tbp.client.component.table.converter.SetStringConverter;
import com.sayyi.software.tbp.client.component.table.skin.CustomTableCellSkin;
import com.sayyi.software.tbp.ui.api.File2ObservableConverter;
import com.sayyi.software.tbp.common.FileMetadata;
import com.sayyi.software.tbp.common.FileUtil;
import com.sayyi.software.tbp.common.constant.ResourceType;
import com.sayyi.software.tbp.db.DbHelperImpl;
import com.sayyi.software.tbp.client.component.util.MenuItemFactory;
import com.sayyi.software.tbp.ui.api.model.ObservableMetadata;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LongStringConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.sayyi.software.tbp.ui.api.constant.MetadataColumnName.*;

/**
 *
 */
@Slf4j
public class MetadataTableView {



    private TableView<ObservableMetadata> tableView;

    public MetadataTableView() {
        initTableView();
    }

    @SuppressWarnings("unchecked")
    public void initTableView() {
        tableView = new TableView<>();
        tableView.setPlaceholder(new Label("未获取到数据"));

        // 初始化各个列渲染、编辑
        TableColumn<ObservableMetadata, Long> idCol = new TableColumn<>(ID);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        idCol.setMinWidth(80);
        TableColumn<ObservableMetadata, String> nameCol = new TableColumn<>(NAME);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(350);
        nameCol.setMinWidth(350);
        TableColumn<ObservableMetadata, Integer> typeCol = new TableColumn<>(TYPE);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(50);
        typeCol.setMinWidth(50);
        TableColumn<ObservableMetadata, Set<String>> tagsCol = new TableColumn<>(TAGS);
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));
        tagsCol.setPrefWidth(350);
        tagsCol.setMinWidth(350);
        TableColumn<ObservableMetadata, Long> updateTimeCol = new TableColumn<>(UPDATE_TIME);
        updateTimeCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdateTime"));
        updateTimeCol.setPrefWidth(150);
        updateTimeCol.setMinWidth(150);
        tableView.getColumns().addAll(idCol, nameCol, typeCol, tagsCol, updateTimeCol);
        // 允许多选
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 允许编辑
        tableView.setEditable(true);
        idCol.setCellFactory(param -> new SimpleCellTableCell<>(Pos.CENTER, new LongStringConverter()));
        // 使用自定义的Skin，双击不再触发编辑
        nameCol.setCellFactory(param -> {
            TextFieldTableCell<ObservableMetadata, String> textFieldTableCell = new TextFieldTableCell<>(new DefaultStringConverter()) {
                @Override
                public void commitEdit(String newValue) {
                    ObservableMetadata item = getTableRow().getItem();
                    FileMetadata metadata = DbHelperImpl.getInstance().getSelector().get(item.getId());
                    File file = DbHelperImpl.getInstance().getFileHelper().getFile(metadata);
                    try {
                        FileUtil.rename(file, newValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        cancelEdit();
                        return;
                    }
                    FileMetadata fileMetadata = new FileMetadata();
                    fileMetadata.setId(item.getId());
                    fileMetadata.setFilename(newValue);
                    DbHelperImpl.getInstance().getMetadata().update(fileMetadata);
                    super.commitEdit(newValue);
                }
            };
            textFieldTableCell.setAlignment(Pos.CENTER_LEFT);
            // 禁止双击触发编辑
            textFieldTableCell.setSkin(new CustomTableCellSkin<>(textFieldTableCell));
            return textFieldTableCell;
        });
        typeCol.setCellFactory(param -> new TypeCell());
        // 使用文本域的方式更新标签，主要在于这样方便提交，不用点击别的地方，直接回车就可以提交更新
        tagsCol.setCellFactory(param -> {
            TagsCell tagsCell = new TagsCell(SetStringConverter.getInstance());
            // 禁止双击触发编辑
            tagsCell.setSkin(new CustomTableCellSkin<>(tagsCell));
            return tagsCell;
        });
        updateTimeCol.setCellFactory(param -> new SimpleCellTableCell<>(Pos.CENTER, LongDataStringConverter.getInstance()));

        // 右键菜单初始化。
        // 这个就姑且放tableView中实现好了，问题不大，也不涉及持久化的各种操作
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(MenuItemFactory.getInstance().generateMenuItem(tableView));
        tableView.setContextMenu(contextMenu);

        // 拖拽进入。
        // 这个功能放在这里，因为tableRow只允许拖拽进入有数据的行，如果没有数据就不允许触发拖拽，逻辑不同。
        tableView.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY);
        });
        tableView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                for (File file : dragboard.getFiles()) {
                    // 这个功能，可以更加有用一点，拖拽进来后，自带当前页面的标签如何？
                    FileMetadata fileMetadata = new FileMetadata();
                    fileMetadata.setFilename(file.getName());
                    fileMetadata.setResourceType(ResourceType.LOCAL);
                    // 跟随当前页面标签
                    fileMetadata.setTags((Set<String>) tableView.getUserData());
                    // 请求分配一个存储路径
                    fileMetadata.setResourcePath(DbHelperImpl.getInstance().getFileHelper().assignPath());
                    File storeFile = DbHelperImpl.getInstance().getFileHelper().getFile(fileMetadata);
                    try {
                        if (file.isDirectory()) {
                            FileUtil.copyDir(file, storeFile);
                        } else {
                            FileUtil.copyFile(file, storeFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DbHelperImpl.getInstance().getMetadata().insert(fileMetadata);
                    ObservableMetadata observableMetadata = File2ObservableConverter.convert(fileMetadata);
                    tableView.getItems().add(observableMetadata);
                }
            }
        });
    }

    /**
     * 更新TableView中的数据条目
     * @param metadata
     */
    public void setMetadata(String tagsStr, ObservableList<ObservableMetadata> metadata) {
        Set<String> set = SetStringConverter.getInstance().fromString(tagsStr);
        log.debug("设置tableView查询tag集合【{}】", set);
        tableView.setUserData(set);
        tableView.setItems(metadata);
    }

    /**
     * 获取渲染好的tableView
     * @return
     */
    public TableView<ObservableMetadata> getTableView() {
        return tableView;
    }

    /**
     * 资源类型cell渲染
     */
    private static class TypeCell extends TableCell<ObservableMetadata, Integer> {

        private ImageView imageView;

        public TypeCell() {
            imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(25);
            this.setGraphic(imageView);
            this.setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            // 这种为空的情况，不要直接返回，有可能原本有数据，后来删除了，此时应该将单元格渲染为空白样式
            if (empty || item == null) {
                imageView.setImage(null);
                return;
            }
            Image image = item == ResourceType.LOCAL ? new Image("img/local-file.png") : new Image("img/web-file.png");
            imageView.setImage(image);
        }
    }

    /**
     * 标签集合渲染，还要能够编辑
     */
    private static class TagsCell extends TableCell<ObservableMetadata, Set<String>> {

        private TextArea textArea;
        private Group showPane;
        private FlowPane pane;

        private final StringConverter<Set<String>> setStringConverter;

        public TagsCell(StringConverter<Set<String>> setStringConverter) {
            this.setStringConverter = setStringConverter;
            pane = new FlowPane();
            pane.setVgap(5);
            pane.setHgap(5);
            // FlowPane 需要包裹一下，才能刚好满足内部组件需要的大小
            showPane = new Group();
            showPane.getChildren().add(pane);
            this.setGraphic(showPane);

            pane.prefWidthProperty().bind(this.widthProperty().subtract(3));
        }

        @Override
        protected void updateItem(Set<String> item, boolean empty) {
            super.updateItem(item, empty);
            // javafx应该是针对大量数据进行过优化，条目划出屏幕就会释放，进来后重新渲染
            // 因此这里需要先清空一下label
            pane.getChildren().clear();
            if (empty || item.isEmpty()) {
                return;
            }
            this.setGraphic(showPane);
            for (String s : item) {
                Label label = new Label(s);
                label.getStyleClass().add("tag");
                pane.getChildren().add(label);
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
            // 新增功能：放入一个textField，多个标签点号分隔，回车提交
            if (textArea == null) {
                textArea = new TextArea();
                // 自动换行
                textArea.setWrapText(true);
                // enter提交，esc取消
                textArea.setOnKeyReleased(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        // 这里提交的时候，需要把回车给干掉
                        String text = textArea.getText();
                        text = text.replace("\n", "").replace("\r", "");
                        this.commitEdit(setStringConverter.fromString(text));
                        event.consume();
                    } else if (event.getCode() == KeyCode.ESCAPE) {
                        this.cancelEdit();
                        event.consume();
                    }
                });
            }
            this.setGraphic(textArea);
            textArea.setPrefSize(this.getWidth(), this.getHeight());
            textArea.setText(setStringConverter.toString(this.getItem()));
            // 姑且和TextFile的行为保持一致
            textArea.selectAll();
            textArea.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            updateItem(this.getItem(), false);
        }

        @Override
        public void commitEdit(Set<String> newValue) {
            long id = getTableRow().getItem().getId();
            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setId(id);
            fileMetadata.setTags(newValue);
            DbHelperImpl.getInstance().getMetadata().update(fileMetadata);
            // 这里如果是空的集合，就不会联动展示，非空集合就可以。奇了怪了
            // updateItem，写的有问题，改下就好了
            super.commitEdit(FXCollections.observableSet(newValue));
        }
    }

    /**
     * 仅仅只是展示，可以设置数据的对齐
     * @param <T>
     */
    private static class  SimpleCellTableCell<T> extends TableCell<ObservableMetadata, T> {

        private final StringConverter<T> converter;

        public SimpleCellTableCell(Pos pos, StringConverter<T> converter) {
            this.converter = converter;
            this.setAlignment(pos);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText("");
            } else {
                this.setText(converter.toString(item));
            }
        }
    }

}
