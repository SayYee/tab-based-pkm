package com.sayyi.software.tbp.plugin.tags;

import com.sayyi.software.tbp.common.model.UpdateTagsOp;
import com.sayyi.software.tbp.db.api.component.DbHelper;
import com.sayyi.software.tbp.ui.api.component.SearchableTextField;
import com.sayyi.software.tbp.ui.api.converter.SetStringConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class TagsRenameTab {

    private Tab tab;

    public TagsRenameTab(DbHelper dbHelper) {
        tab = new Tab("标签重命名");

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER);

        VBox inner = new VBox(10);
        inner.setPadding(new Insets(150));

        SearchableTextField fromField = new SearchableTextField(null, dbHelper.getSelector());
        fromField.setPromptText("原标签集合");
        SearchableTextField toField = new SearchableTextField(null, dbHelper.getSelector());
        toField.setPromptText("新标签集合");
        Button button = new Button("提交");

        inner.getChildren().addAll(fromField, toField, button);
        vBox.getChildren().addAll(inner);

        button.setOnAction(event -> {
            UpdateTagsOp updateTagsOp = new UpdateTagsOp();
            updateTagsOp.setOldTags(SetStringConverter.getInstance().fromString(fromField.getText()));
            updateTagsOp.setNewTags(SetStringConverter.getInstance().fromString(toField.getText()));
            dbHelper.getMetadata().updateTags(updateTagsOp);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "修改成功");
            alert.show();
        });

        tab.setContent(vBox);
    }

    public Tab getTag() {
        return tab;
    }
}
