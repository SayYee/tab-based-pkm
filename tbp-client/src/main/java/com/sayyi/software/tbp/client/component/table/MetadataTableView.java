package com.sayyi.software.tbp.client.component.table;

import com.sayyi.software.tbp.client.component.table.converter.LongDataStringConverter;
import com.sayyi.software.tbp.client.component.table.converter.SetStringConverter;
import com.sayyi.software.tbp.client.component.table.menuitem.MenuItemFactory;
import com.sayyi.software.tbp.client.component.table.skin.CustomTableCellSkin;
import com.sayyi.software.tbp.client.model.ObservableMetadata;
import com.sayyi.software.tbp.common.constant.ResourceType;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LongStringConverter;

import java.util.Set;

/**
 * 这个tableView自身不进行任何数据的持久化操作。仅仅支持一般的排序、编辑，自定义的右键菜单。编辑操作也仅仅是修改可观察对象，持久化的逻辑在对象监听中处理
 */
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
        TableColumn<ObservableMetadata, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        TableColumn<ObservableMetadata, String> nameCol = new TableColumn<>("名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(350);
        TableColumn<ObservableMetadata, Integer> typeCol = new TableColumn<>("类型");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(50);
        TableColumn<ObservableMetadata, Set<String>> tagsCol = new TableColumn<>("标签");
        tagsCol.setCellValueFactory(new PropertyValueFactory<>("tags"));
        tagsCol.setPrefWidth(350);
        TableColumn<ObservableMetadata, Long> updateTimeCol = new TableColumn<>("修改时间");
        updateTimeCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdateTime"));
        updateTimeCol.setPrefWidth(150);
        tableView.getColumns().addAll(idCol, nameCol, typeCol, tagsCol, updateTimeCol);
        // 允许多选
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 允许编辑
        tableView.setEditable(true);
        idCol.setCellFactory(param -> new SimpleCellTableCell<>(Pos.CENTER, new LongStringConverter()));
        // 使用自定义的Skin，双击不再触发编辑
        nameCol.setCellFactory(param -> {
            TextFieldTableCell<ObservableMetadata, String> textFieldTableCell = new TextFieldTableCell<>(new DefaultStringConverter());
            textFieldTableCell.setSkin(new CustomTableCellSkin<>(textFieldTableCell));
            return textFieldTableCell;
        });
        typeCol.setCellFactory(param -> new TypeCell());
        // 使用文本域的方式更新标签，主要在于这样方便提交，不用点击别的地方，直接回车就可以提交更新
        tagsCol.setCellFactory(param -> {
            TagsCell tagsCell = new TagsCell(SetStringConverter.getInstance());
            tagsCell.setSkin(new CustomTableCellSkin<>(tagsCell));
            return tagsCell;
        });
        updateTimeCol.setCellFactory(param -> new SimpleCellTableCell<>(Pos.CENTER, LongDataStringConverter.getInstance()));

        // 右键菜单初始化。
        // 这个就姑且放tableView中实现好了，问题不大，也不涉及持久化的各种操作
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(MenuItemFactory.getAll(tableView));
        tableView.setContextMenu(contextMenu);
    }

    /**
     * 更新TableView中的数据条目
     * @param metadata
     */
    public void setMetadata(ObservableList<ObservableMetadata> metadata) {
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
            if (empty || item == null) {
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

        private TextArea textField;
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
        }

        @Override
        protected void updateItem(Set<String> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item.isEmpty()) {
                return;
            }
            this.setGraphic(showPane);
            // javafx应该是针对大量数据进行过优化，条目划出屏幕就会释放，进来后重新渲染
            // 因此这里需要先清空一下label
            pane.getChildren().clear();
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
            if (textField == null) {
                textField = new TextArea();
                // 自动换行
                textField.setWrapText(true);
                // enter提交，esc取消
                textField.setOnKeyReleased(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        // 这里提交的时候，需要把回车给干掉
                        String text = textField.getText();
                        text = text.replace("\n", "").replace("\r", "");
                        this.commitEdit(setStringConverter.fromString(text));
                        event.consume();
                    } else if (event.getCode() == KeyCode.ESCAPE) {
                        this.cancelEdit();
                        event.consume();
                    }
                });
            }
            this.setGraphic(textField);
            textField.setPrefSize(this.getWidth(), this.getHeight());
            textField.setText(setStringConverter.toString(this.getItem()));
            textField.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            updateItem(this.getItem(), false);
        }

        @Override
        public void commitEdit(Set<String> newValue) {
            // 提交后持久化逻辑，都交给外边统一处理了
            super.commitEdit(newValue);
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
