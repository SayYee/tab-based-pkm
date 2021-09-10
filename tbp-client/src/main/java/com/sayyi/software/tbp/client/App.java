package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.util.Scheduler;
import com.sayyi.software.tbp.client.component.SearchTab;
import com.sayyi.software.tbp.client.component.tree.TagTree;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class App extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        System.out.println("init--------current_thread=" + Thread.currentThread().getName());
    }

    @Override
    public void start(Stage stage) throws Exception {
        HBox root = new HBox(5);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #23a8f2");

        SidePane sidePane = new SidePane();
        Region sideBox = sidePane.getPane();

        MainPane mainPane = new MainPane();
        Region mainBox = mainPane.getPane();

        root.getChildren().addAll(sideBox, mainBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/style.css");
        stage.setScene(scene);

        stage.setTitle("tbp");
        Image image = new Image("img/icon.png");
        stage.getIcons().add(image);
        stage.setWidth(1400);
        stage.setHeight(800);
        stage.setMinWidth(400);
        stage.setMinHeight(200);
        stage.show();

        // 内部的高度绑定
        DoubleBinding heightBinding = stage.heightProperty().subtract(root.getPadding().getBottom() + root.getPadding().getTop());
        // 侧边栏高度动态绑定
        sideBox.prefHeightProperty().bind(heightBinding);
        // main区域宽度绑定
        mainBox.prefWidthProperty().bind(stage.widthProperty().subtract(sideBox.prefWidthProperty()).subtract(30));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Scheduler.get().shutdown();
        System.out.println("stop--------current_thread=" + Thread.currentThread().getName());
    }
}
