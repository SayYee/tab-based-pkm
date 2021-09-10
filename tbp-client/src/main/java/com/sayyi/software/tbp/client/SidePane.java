package com.sayyi.software.tbp.client;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * 最左侧的功能条布局
 */
public class SidePane {

    private VBox pane;

    public SidePane() {
        // 侧边栏，预留位置放设置按钮，后续可能放置其他功能按钮
        pane = new VBox();
        pane.setStyle("-fx-background-color: #51dd90");
        pane.setPrefWidth(45);
        pane.setMinWidth(45);
        pane.setMaxWidth(45);
        // 插件区域，
        VBox pluginBar = new VBox();
        pluginBar.setStyle("-fx-background-color: #ffd767");
        pluginBar.setPrefWidth(45);
        pluginBar.setPrefHeight(100);

        Label addPluginLabel = new Label();
        addPluginLabel.setPrefSize(45, 45);
        addPluginLabel.setMinSize(45, 45);
        addPluginLabel.setMaxSize(45, 45);
        addPluginLabel.setAlignment(Pos.CENTER);
        ImageView addPluginImage = new ImageView();
        addPluginImage.setPreserveRatio(true);
        addPluginImage.setFitWidth(25);
        addPluginImage.setImage(new Image("img/add.png"));
        addPluginLabel.setGraphic(addPluginImage);
        addPluginLabel.setTooltip(new Tooltip("新增"));

        pluginBar.getChildren().add(addPluginLabel);

        // 最下边的设置按钮，固定45的大小
        Label settingLabel = new Label();
        settingLabel.setPrefSize(45, 45);
        settingLabel.setMinSize(45, 45);
        settingLabel.setMaxSize(45, 45);
        settingLabel.setAlignment(Pos.CENTER);
        settingLabel.setTooltip(new Tooltip("设置"));
        ImageView settingImage = new ImageView();
        settingImage.setImage(new Image("img/setting.png"));
        settingImage.setPreserveRatio(true);
        settingImage.setFitWidth(25);
        settingLabel.setGraphic(settingImage);

        pane.getChildren().addAll(pluginBar, settingLabel);

        pluginBar.prefHeightProperty().bind(pane.prefHeightProperty().subtract(45));
    }

    public Region getPane() {
        return pane;
    }
}
