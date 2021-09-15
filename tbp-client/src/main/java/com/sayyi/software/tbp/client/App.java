package com.sayyi.software.tbp.client;

import com.sayyi.software.tbp.client.component.table.menuitem.*;
import com.sayyi.software.tbp.client.component.util.MenuItemFactory;
import com.sayyi.software.tbp.client.component.util.Scheduler;
import com.sayyi.software.tbp.db.DbHelperImpl;
import com.sayyi.software.tbp.ui.api.Plugin;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

@Slf4j
public class App extends Application {

    @Override
    public void init() throws Exception {
        log.info("init--------current_thread=" + Thread.currentThread().getName());
        log.info("初始化元数据管理组件");
        long currentTimeMillis = System.currentTimeMillis();
        DbHelperImpl.getInstance();
        log.info("元数据管理组件初始化完毕，耗时【{}ms】", System.currentTimeMillis() - currentTimeMillis);
        // 插件机制
        MenuItemFactory.getInstance().registry(new NewDirectoryMenuItemProvider());
        MenuItemFactory.getInstance().registry(new NewFileMenuItemProvider());
        MenuItemFactory.getInstance().registry(new OpenFileMenuItemProvider());
        MenuItemFactory.getInstance().registry(new ShowFileLocationMenuItemProvider());
        MenuItemFactory.getInstance().registry(new DeleteFileMenuItemProvider());
        MenuItemFactory.getInstance().registry(new CopyFileMenuItemProvider());
        MenuItemFactory.getInstance().registry(new CopyPathMenuItemProvider());

        loadPlugin();
    }

    private void loadPlugin() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String property = System.getProperty("user.dir");
        File pluginDirectory = new File(property + "/../plugin");
        File[] jars = pluginDirectory.listFiles(pathname -> pathname.getName().endsWith(".jar"));
        if (jars == null) {
            log.info("未找到jar文件");
            return;
        }
        for (File jar : jars) {
            log.debug("加载jar【{}】", jar.getAbsolutePath());
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, App.class.getClassLoader());
            URL resource = classLoader.getResource("project.properties");
            if (resource == null) {
                continue;
            }
            Properties properties = new Properties();
            properties.load(resource.openStream());
            String mainClass = properties.getProperty("mainClass");
            log.debug("加载主类【{}】", mainClass);
            Class<?> aClass = classLoader.loadClass(mainClass);
            Plugin plugin = (Plugin) aClass.newInstance();
            plugin.init(DbHelperImpl.getInstance(), UiHelperImpl.getInstance());
            log.debug("插件注册完成");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        HBox root = new HBox(5);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #23a8f2");

        SidePane sidePane = new SidePane();
        Region sideBox = sidePane.getPane();

        MainPane mainPane = MainPane.getInstance();
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
        log.info("stop--------current_thread=" + Thread.currentThread().getName());
    }

}
