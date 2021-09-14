package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.util.Scheduler;
import com.sayyi.software.tbp.db.DbHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App extends Application {

    @Override
    public void init() throws Exception {
        log.info("init--------current_thread=" + Thread.currentThread().getName());
        // TODO 插件加载
    }

    @Override
    public void start(Stage stage) throws Exception {
        ModelStage.init(stage);

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

        initDb();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Scheduler.get().shutdown();
        log.info("stop--------current_thread=" + Thread.currentThread().getName());
    }

    private void initDb() {
        ModelStage.get().show();
        // 启动一个Task来初始化元数据管理组件
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                log.info("初始化元数据管理组件");
                long currentTimeMillis = System.currentTimeMillis();
                DbHelper.getInstance();
                log.info("元数据管理组件初始化完毕，耗时【{}ms】", System.currentTimeMillis() - currentTimeMillis);
                return null;
            }

            @Override
            protected void succeeded() {
                ModelStage.get().close();
            }

            @Override
            protected void failed() {
                Alert alert = new Alert(Alert.AlertType.ERROR, "组件启动失败");
                alert.show();
                alert.setOnCloseRequest(event -> {
                    Platform.exit();
                });
            }
        }).start();
    }
}
