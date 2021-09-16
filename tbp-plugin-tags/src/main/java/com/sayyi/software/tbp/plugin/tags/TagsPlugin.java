package com.sayyi.software.tbp.plugin.tags;

import com.sayyi.software.tbp.db.api.component.DbHelper;
import com.sayyi.software.tbp.ui.api.Plugin;
import com.sayyi.software.tbp.ui.api.UiHelper;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.util.Objects;

public class TagsPlugin implements Plugin {

    @Override
    public void init(DbHelper dbHelper, UiHelper uiHelper) {
        Label label = createLabel(dbHelper, uiHelper);
        uiHelper.getSidebarTollRegister().registry(label);
    }

    private Label createLabel(DbHelper dbHelper, UiHelper uiHelper) {
        Label label = new Label();
        // 鼠标放上去变成小手
        label.setCursor(Cursor.HAND);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(25);
        // 加载资源，需要用对应的类加载器来
        imageView.setImage(new Image(Objects.requireNonNull(TagsPlugin.class.getClassLoader().getResourceAsStream("img/tag.png"))));
        label.setGraphic(imageView);
        label.setTooltip(new Tooltip("标签修改"));

        label.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                TagsRenameTab tagsRenameTab = new TagsRenameTab(dbHelper);
                uiHelper.getTabRegister().registry(tagsRenameTab.getTag());
            }
        });
        return label;
    }
}
