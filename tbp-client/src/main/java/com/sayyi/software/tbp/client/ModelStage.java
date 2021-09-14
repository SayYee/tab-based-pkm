package com.sayyi.software.tbp.client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 一个简单的模态框组件，遮住窗口，不让用户操作
 */
public class ModelStage {

    private static volatile ModelStage instance;

    protected static void init(Stage stage) {
        // 真是闲的了。
        if (instance == null) {
            synchronized (ModelStage.class) {
                if (instance == null) {
                    instance = new ModelStage(stage);
                }
            }
        }
    }

    public static ModelStage get() {
        if (instance == null) {
            throw new NullPointerException("未初始化");
        }
        return instance;
    }

    private final Stage model;
    private final Stage parent;

    private ModelStage(Stage stage) {
        parent = stage;

        model = new Stage();
        model.initOwner(stage);
        model.initModality(Modality.APPLICATION_MODAL);
        model.initStyle(StageStyle.TRANSPARENT);

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #00000070");
        stackPane.setAlignment(Pos.CENTER);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        stackPane.getChildren().add(progressIndicator);

        Scene modelScene = new Scene(stackPane);
        modelScene.setFill(null);
        model.setScene(modelScene);
    }

    public void show() {
        model.show();
        model.setHeight(parent.getHeight());
        model.setWidth(parent.getWidth());
        model.setX(parent.getX());
        model.setY(parent.getY());
    }

    public void close() {
        model.close();
    }
}
