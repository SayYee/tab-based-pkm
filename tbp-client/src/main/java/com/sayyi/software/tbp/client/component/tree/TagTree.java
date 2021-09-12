package com.sayyi.software.tbp.client.component.tree;

import com.sayyi.software.tbp.common.Tree;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 这个类，自己就是一个完备的组件了，提供数据加载展示功能。
 */
public class TagTree {

    public TreeView<String> getTree() {
        return new TreeView<>();
    }

    public Region getNode() {
        // TODO 数据保存
        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: #2b2b2b");

        TextField textField = new TextField();
        textField.setPromptText("检索信息");
        // TODO 数据记载
        TagTreeView treeView = new TagTreeView(getTestTree(), System.out::println);

        vBox.getChildren().addAll(textField, treeView.getTreeView());

        textField.setOnAction(event -> treeView.filter(textField.getText()));

        treeView.getTreeView().prefHeightProperty().bind(vBox.heightProperty().subtract(30));

        return vBox;
    }

    /**
     * 创建假数据
     * @return
     */
    private Tree getTestTree() {
        Tree root = new Tree();
        root.setName("root");
        List<Tree> children = new ArrayList<>();
        children.add(new Tree("one", Arrays.asList(new Tree("sh"), new Tree("check"), new Tree("alisi"))));
        children.add(new Tree("two", Arrays.asList(new Tree("lll1"), new Tree("li"), new Tree("fa"))));
        children.add(new Tree("three", Arrays.asList(new Tree("wennd"), new Tree("six"), new Tree("fu"))));
        children.add(new Tree("four", Arrays.asList(new Tree("xxd"), new Tree("man"), new Tree("dis"))));
        children.add(new Tree("five", Arrays.asList(new Tree("111"), new Tree("manue"))));
        root.setChildren(children);
        return root;
    }

}
