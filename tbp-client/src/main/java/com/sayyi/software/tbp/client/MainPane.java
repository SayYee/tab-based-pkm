package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.SearchTab;
import com.sayyi.software.tbp.client.component.tree.TagTree;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 主区域，包括标签树和TabPane
 */
public class MainPane {

    private final SplitPane pane;

    public MainPane() {
        pane = new SplitPane();
        pane.setStyle("-fx-background-color: #b28a42");

        // 插件工作区域（待定，目前就放一个treeView）
        VBox treeBox = new VBox();
        treeBox.setStyle("-fx-background-color: #ffff00");
        treeBox.setMinWidth(300);
        TagTree tagTree = new TagTree();
        Region treeRegion = tagTree.getNode();
        treeBox.getChildren().add(treeRegion);

        // 主区域
        VBox tabBox = new VBox();
        tabBox.setStyle("-fx-background-color: #cccccc");
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #55da71");
        SearchTab searchTab = new SearchTab("检索tab", tabPane);
        tabPane.getTabs().add(searchTab.getSearchTab());
        tabBox.getChildren().add(tabPane);

        pane.getItems().addAll(treeBox, tabBox);
        pane.setDividerPositions(0.2d, 0.8d);

        treeRegion.prefHeightProperty().bind(treeBox.heightProperty());
        tabPane.prefHeightProperty().bind(tabBox.heightProperty());
    }

    public Region getPane() {
        return pane;
    }
}
