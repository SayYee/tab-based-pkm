package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.SearchTab;
import com.sayyi.software.tbp.client.component.tree.TagTree;
import com.sayyi.software.tbp.ui.api.constant.ID;
import com.sayyi.software.tbp.ui.api.tab.TabRegister;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 主区域，包括标签树和TabPane
 */
public class MainPane implements TabRegister {

    private static final MainPane instance = new MainPane();

    public static MainPane getInstance() {
        return instance;
    }

    private final SplitPane pane;

    private TabPane tabPane;

    private MainPane() {
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
        tabPane = new TabPane();
        tabPane.setId(ID.TAB_PANE);
        tabPane.setStyle("-fx-background-color: #55da71");
        SearchTab searchTab = new SearchTab();
        Tab tab = searchTab.getSearchTab();
        // 默认的这个搜索窗口，不让关闭
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        tabBox.getChildren().add(tabPane);

        pane.getItems().addAll(treeBox, tabBox);
        pane.setDividerPositions(0.2d, 0.8d);

        treeRegion.prefHeightProperty().bind(treeBox.heightProperty());
        tabPane.prefHeightProperty().bind(tabBox.heightProperty());

        // 双击标题，创建新的检索页
        tabPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                SearchTab newTab = new SearchTab();
                MainPane.this.registry(newTab.getSearchTab());
                event.consume();
            }
        });
    }

    public Region getPane() {
        return pane;
    }

    @Override
    public void registry(Tab tab) {
        Region content = (Region) tab.getContent();
        content.prefHeightProperty().bind(tabPane.heightProperty());
        content.prefWidthProperty().bind(tabPane.widthProperty());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
