package com.sayyi.software.tbp.client.component;

import com.sayyi.software.tbp.client.component.table.converter.SetStringConverter;
import com.sayyi.software.tbp.common.model.TagInfo;
import com.sayyi.software.tbp.db.DbHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * 带检索提示框的输入框。检索提示信息使用Popup实现
 */
public class SearchableTextField extends TextField {

    private List<TagInfo> tagInfos;
    private String lastTagsStr;

    private final Popup popup;
    private final ListView<TagInfo> tagInfoListView;

    private boolean allowShowPopup = true;

    public SearchableTextField() {
        popup = new Popup();
        popup.setConsumeAutoHidingEvents(false);
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);

        tagInfoListView = new ListView<>();
        tagInfoListView.prefWidthProperty().bind(this.widthProperty().subtract(5));
        tagInfoListView.setPrefHeight(300);
        popup.getContent().add(tagInfoListView);
        init();
    }

    private void init() {
        tagInfoListView.setCellFactory(param -> new TagInfoCell());
        tagInfoListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                TagInfo selectedItem = tagInfoListView.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    return;
                }
                allowShowPopup = false;
                hidePopup();
                String tag = selectedItem.getTag();
                String[] strings = parseText(SearchableTextField.this.getText());
                String newText = strings[0].equals("") ? tag : strings[0] + "." + tag;
                SearchableTextField.this.setText(newText);
                SearchableTextField.this.positionCaret(newText.length());

            }
        });
        updateList("", "");
        tagInfoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // 随着输入内容，变更下拉列表内容
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!allowShowPopup) {
                allowShowPopup = true;
                return;
            }
            showPopup();
            String[] parseText = parseText(newValue);
            updateList(parseText[0], parseText[1]);
        });

    }

    /**
     * 将输入文本解析为 搜索标签 和 过滤词 两个部分
     * @param text
     * @return
     */
    private String[] parseText(String text) {
        int i = text.lastIndexOf('.');
        // 没有标点符号，全量查询+过滤
        String tagsStr;
        String filterWord;
        if (i == -1) {
            tagsStr = "";
            filterWord = text;
        } else if (i < text.length() - 1) {
            tagsStr = text.substring(0, i);
            filterWord = text.substring(i + 1);
        } else {
            tagsStr = text.substring(0, i);
            filterWord = "";
        }
        return new String[]{tagsStr, filterWord};
    }

    /**
     * 更新下拉列表内容
     * @param tagsStr
     * @param word
     */
    private void updateList(String tagsStr, String word) {
        if (!tagsStr.equals(lastTagsStr)) {
            lastTagsStr = tagsStr;
            Set<String> set = SetStringConverter.getInstance().fromString(tagsStr);
            tagInfos = DbHelper.getInstance().getSelector().listTags(set);
        }
        word = word.toLowerCase();
        String finalWord = word;
        List<TagInfo> collect = tagInfos.stream().filter(tagInfo -> tagInfo.getTag().toLowerCase().contains(finalWord)).collect(Collectors.toList());
        tagInfoListView.setItems(FXCollections.observableList(collect));
        tagInfoListView.getSelectionModel().select(0);
    }

    private void hidePopup() {
        popup.hide();
    }

    private void showPopup() {
        if (popup.isShowing()) {
            return;
        }
        Point2D point2D = this.localToScreen(1, this.getHeight());
        popup.show(this.getScene().getWindow(), point2D.getX(), point2D.getY());
    }

    private static class TagInfoCell extends ListCell<TagInfo> {

        @Override
        protected void updateItem(TagInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText("");
                this.setGraphic(null);
                return;
            }
            this.setText(item.getTag());
            this.setGraphic(new Button(item.getFileNum() + ""));
        }
    }

}
