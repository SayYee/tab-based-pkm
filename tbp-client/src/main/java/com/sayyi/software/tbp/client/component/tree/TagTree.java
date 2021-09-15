package com.sayyi.software.tbp.client.component.tree;

import com.sayyi.software.tbp.client.MainPane;
import com.sayyi.software.tbp.client.component.SearchTab;
import com.sayyi.software.tbp.common.Tree;
import com.sayyi.software.tbp.db.DbHelperImpl;
import com.sayyi.software.tbp.db.api.component.TreeComponent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 这个类，自己就是一个完备的组件了，提供数据加载展示功能。
 */
public class TagTree {

    public Region getNode() {
        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: #2b2b2b");

        HBox hBox = new HBox(5);
        Button saveBtn = new Button();
        saveBtn.setPrefSize(20, 20);
        saveBtn.getStyleClass().add("tree-save");
        TextField textField = new TextField();
        textField.setPromptText("检索信息");
        hBox.getChildren().addAll(saveBtn, textField);

        TagTreeView treeView = new TagTreeView(getTreeData(), tags -> {
            SearchTab searchTab = new SearchTab(tags);
            MainPane.getInstance().registry(searchTab.getSearchTab());
        });

        vBox.getChildren().addAll(hBox, treeView.getTreeView());

        // 点击按钮时，保存新的tree数据
        saveBtn.setOnAction(event -> {
            TreeComponent treeComponent = DbHelperImpl.getInstance().getTreeComponent();
            treeComponent.store(treeView.getCurrentTree());
        });
        saveBtn.disableProperty().bind(treeView.getModified().not());
        textField.setOnAction(event -> {
            if (textField.getText().equals("")) {
                treeView.setTree(getTreeData());
            } else {
                treeView.filter(textField.getText());
            }
        });

        textField.prefWidthProperty().bind(hBox.widthProperty().subtract(25));
        hBox.prefWidthProperty().bind(vBox.widthProperty());
        treeView.getTreeView().prefHeightProperty().bind(vBox.heightProperty().subtract(30));

        return vBox;
    }

    /**
     * 创建假数据
     * @return
     */
    private Tree getTreeData() {
        TreeComponent treeComponent = DbHelperImpl.getInstance().getTreeComponent();
        return treeComponent.load();
    }

}
