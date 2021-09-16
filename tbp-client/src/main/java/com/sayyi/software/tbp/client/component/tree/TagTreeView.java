package com.sayyi.software.tbp.client.component.tree;

import com.sayyi.software.tbp.common.Tree;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class TagTreeView {

    private TreeView<String> treeView;
    /** 外部传入的tree */
    private Tree tree;
    /** 点击小图标时要执行的动作，会将对应的标签字符串作为参数传入 */
    private final Consumer<String> onClick;

    private final BooleanProperty modified = new SimpleBooleanProperty(false);

    public TagTreeView(Tree tree, Consumer<String> onClick) {
        this.tree = tree;
        this.onClick = onClick;
        initTreeView();
    }

    private void initTreeView() {
        treeView = new TreeView<>();
        if (tree != null) {
            TreeItem<String> treeItem = toItem(tree);
            treeView.setRoot(treeItem);
        }
        treeView.setShowRoot(false);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 这里只处理拖拽，不再使用默认的编辑功能了，太难受了这玩意儿
        treeView.setCellFactory(param -> getTreeCell());

        // 右键菜单：增删改
        initContextMenu();
    }

    /**
     * 初始化右键菜单：增删改
     */
    private void initContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem delMenuItem = new MenuItem();
        HBox delBox = new HBox(5);
        delBox.getChildren().add(new Label("删除"));
        delBox.setAlignment(Pos.CENTER_LEFT);
        delMenuItem.setGraphic(delBox);
        delMenuItem.setOnAction(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            // 整个节点及其子节点全部移除
            selectedItem.getParent().getChildren().remove(selectedItem);
            modified.set(true);
        });

        MenuItem addMenuItem = new MenuItem();
        HBox addBox = new HBox(5);
        Label addLabel = new Label("新增");
        TextField addField = new TextField();
        addField.setPromptText("输入子节点名称");
        addBox.getChildren().addAll(addLabel, addField);
        addBox.setAlignment(Pos.CENTER_LEFT);
        addMenuItem.setGraphic(addBox);
        addField.setOnAction(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            TreeItem<String> newItem = create(addField.getText());
            selectedItem.getChildren().add(newItem);
            selectedItem.setExpanded(true);
            treeView.getSelectionModel().select(newItem);
            modified.set(true);
        });

        MenuItem modifyItem = new MenuItem();
        HBox modifyBox = new HBox(5);
        Label modifyLabel = new Label("修改");
        TextField modifyField = new TextField();
        modifyBox.getChildren().addAll(modifyLabel, modifyField);
        modifyBox.setAlignment(Pos.CENTER_LEFT);
        modifyItem.setGraphic(modifyBox);
        modifyField.setOnAction(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            selectedItem.setValue(modifyField.getText());
            modified.set(true);
        });

        contextMenu.getItems().addAll(delMenuItem, addMenuItem, modifyItem);
        // 打开右键菜单时，初始化文本框内容
        contextMenu.setOnShown(event -> {
            addField.setText("");
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            modifyField.setText(selectedItem.getValue());
        });
        treeView.setContextMenu(contextMenu);
    }

    /**
     * 获取初始化好的TreeView
     * @return
     */
    public TreeView<String> getTreeView() {
        return treeView;
    }

    /**
     * 设置tree
     * @param tree
     */
    public void setTree(Tree tree) {
        this.tree = tree;
        treeView.setRoot(toItem(tree));
        modified.set(false);
    }

    /**
     * 获取当前对象的tree
     * @return
     */
    public Tree getCurrentTree() {
        try {
            return toTree(treeView.getRoot());
        } finally {
            modified.set(false);
        }
    }

    public BooleanProperty getModified() {
        return modified;
    }

    /**
     * 根据传入的关键字过滤数据。不匹配的节点将会被折叠
     * @param word
     */
    public void filter(String word) {
        // 先清除原本的选中状态，免得干扰
        treeView.getSelectionModel().clearSelection();
        word = word.toLowerCase();
        isMatch(word, treeView.getRoot());
    }

    /**
     * 折叠没有包含匹配项的节点，展开包含匹配项的节点
     * @param match
     * @param toCheck
     * @return
     */
    private boolean isMatch(String match, TreeItem<String> toCheck) {
        boolean isMatch = toCheck.getValue().toLowerCase().contains(match);
        for (TreeItem<String> child : toCheck.getChildren()) {
            boolean temp = isMatch(match, child);
            // 暂且如此处理。
            // 最好能加个样式，不然不明显……
            // 问题是什么时候清除这个样式？？？？
            child.setExpanded(temp);
            if (temp) {
                // 这……那就这样吧，虽然看着有点怪怪的，但是确实可以用
                treeView.getSelectionModel().select(child);
            }
            isMatch = isMatch || temp;
        }
        return isMatch;
    }

    /**
     * 将tree数据对象转换为TreeItem，同时设置TreeItem点击查询的功能
     * @param tree
     * @return
     */
    private TreeItem<String> toItem(Tree tree) {
        TreeItem<String> treeItem = create(tree.getName());
        if (tree.getChildren() != null && !tree.getChildren().isEmpty()){
            for (Tree child : tree.getChildren()) {
                TreeItem<String> childItem = toItem(child);
                treeItem.getChildren().add(childItem);
            }
        }
        return treeItem;
    }

    /**
     * item转换为tree
     * @param item
     * @return
     */
    private Tree toTree(TreeItem<String> item) {
        Tree tree = new Tree();
        tree.setName(item.getValue());
        List<Tree> children = new ArrayList<>();
        for (TreeItem<String> child : item.getChildren()) {
            children.add(toTree(child));
        }
        tree.setChildren(children);
        return tree;
    }

    /**
     * 创建带图标的treeitem
     * @param value
     * @return
     */
    private TreeItem<String> create(String value) {
        TreeItem<String> treeItem = new TreeItem<>();
        treeItem.setValue(value);
        // 点击图标，使用标签查询数据
        // 点击这玩意儿，也会触发编辑……把编辑关了算了，通过右键菜单编辑好了。
        StackPane tagPane = new StackPane();
        tagPane.getStyleClass().add("tree-tag");
        tagPane.setPrefSize(20, 20);
        tagPane.setOnMouseClicked(event -> {
            onClick.accept(getItemTagStr(treeItem));
        });
        // 鼠标放上去变成小手
        tagPane.setCursor(Cursor.HAND);
        treeItem.setGraphic(tagPane);
        return treeItem;
    }

    /**
     * 从item中获取对应的tag字符串
     * @param item
     * @return
     */
    private String getItemTagStr(TreeItem<String> item) {
        LinkedList<String> tagList = new LinkedList<>();
        loadTagsByItem(item, tagList);
        // 移除根节点
        tagList.removeLast();
        StringJoiner joiner = new StringJoiner(".");
        for (String s : tagList) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    /**
     * 获取item对应的标签集合
     * @param item
     * @param tagList
     */
    private void loadTagsByItem(TreeItem<String> item, List<String> tagList) {
        tagList.add(item.getValue());
        if (item.getParent() != null) {
            loadTagsByItem(item.getParent(), tagList);
        }
    }

    /**
     * 拖拽移动的源item
     */
    private TreeItem<String> moveItem;

    /**
     * 支持拖拽的TreeCell
     * @return
     */
    private TreeCell<String> getTreeCell() {
        // 禁止编辑
        TextFieldTreeCell<String> treeCell = new TextFieldTreeCell<>(new DefaultStringConverter());
        treeCell.setEditable(false);

        // 拖动
        treeCell.setOnDragDetected(event -> {
            // 组件内拖动
            Dragboard dragboard = treeCell.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            ClipboardContent content = new ClipboardContent();
            // 放入item对应的标签，用于拖动添加元数据标签
            content.putString(getItemTagStr(treeCell.getTreeItem()));
            dragboard.setContent(content);
            WritableImage snapshot = treeCell.snapshot(new SnapshotParameters(), null);
            dragboard.setDragView(snapshot);
            moveItem = treeCell.getTreeItem();
            event.consume();
        });
        treeCell.setOnDragOver(event -> {
            // 这里需要处理子节点问题。不能将父节点放入子节点
            if (isParent(treeCell.getTreeItem(), moveItem)) {
                return;
            }
            event.acceptTransferModes(TransferMode.MOVE);
            // 处理框选问题
            double height = treeCell.getHeight() / 4;
            if (event.getY() < height) {
                treeCell.setBackground(null);
                treeCell.setBorder(new Border(new BorderStroke(Color.valueOf("#24acf2"), BorderStrokeStyle.SOLID, null, new BorderWidths(2, 0, 0, 0), null)));
            } else if (event.getY() < 3 * height) {
                // 这里粗暴的处理样式，会导致原本的样式丢失，出现选中节点后，因为文字变白导致节点看不见的问题
                treeCell.setBorder(null);
                treeCell.setBackground(new Background(new BackgroundFill(Color.valueOf("#24acf2"), null, null)));
            } else {
                treeCell.setBorder(new Border(new BorderStroke(Color.valueOf("#24acf2"), BorderStrokeStyle.SOLID, null, new BorderWidths(0, 0, 2, 0), null)));
                treeCell.setBackground(null);
            }
            event.consume();
        });
        // 离开的时候，清除掉样式
        treeCell.setOnDragExited(event -> {
            treeCell.setBorder(null);
            treeCell.setBackground(null);
            event.consume();
        });
        // 松开的时候，需要根据情况判断
        treeCell.setOnDragDropped(event -> {
            // 有可能又放回原来的位置了
            if (treeCell.getTreeItem() == moveItem) {
                return;
            }
            // 直接在这里处理删除操作，不用放到Done事件中了。
            moveItem.getParent().getChildren().remove(moveItem);
            TreeItem<String> treeItem = treeCell.getTreeItem();
            TreeItem<String> parent = treeItem.getParent();
            int index = parent.getChildren().indexOf(treeItem);
            TreeItem<String> toItem = moveItem;

            double height = treeCell.getHeight() / 4;
            if (event.getY() < height) {
                parent.getChildren().add(index, toItem);
            } else if (event.getY() < 3 * height) {
                treeItem.getChildren().add(0, toItem);
            } else {
                parent.getChildren().add(index + 1, toItem);
            }
            treeView.getSelectionModel().select(toItem);
            modified.set(true);
            event.setDropCompleted(true);
            event.consume();
        });
        return treeCell;
    }

    /**
     *
     * @param toCheck   需要判断的标签
     * @param parent    父节点
     * @return
     */
    private boolean isParent(TreeItem<String> toCheck, TreeItem<String> parent) {
        TreeItem<String> toCheckParent = toCheck.getParent();
        if (toCheckParent == null) {
            return false;
        }
        return toCheckParent == parent || isParent(toCheckParent, parent);
    }

}
