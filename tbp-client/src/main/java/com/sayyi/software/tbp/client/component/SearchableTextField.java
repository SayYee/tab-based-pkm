package com.sayyi.software.tbp.client.component;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * 带检索提示框的输入框。检索提示信息使用contextMenu实现
 */
public class SearchableTextField extends TextField {

    /** 用于将字符串转换成 menuitem，以及从menuitem中获取字符串 */
    private final MenuItemStringConvert convert = new MenuItemStringConvert();

    /** 点击item后要执行的操作 */
    private Consumer<String> itemAction;

    public void setItemAction(Consumer<String> itemAction) {
        this.itemAction = itemAction;
    }

    /**
     * 展示contextMenu
     */
    public void showMenu() {
        ContextMenu contextMenu = this.getContextMenu();
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
            this.setContextMenu(contextMenu);
        }
        contextMenu.show(this, Side.BOTTOM, 0, 0);
    }

    /**
     * 调用item提供程序，更新菜单项
     */
    public boolean updateItems(List<String> items) {
        ContextMenu contextMenu = this.getContextMenu();
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
            this.setContextMenu(contextMenu);
        } else {
            // 清空原有的数据
            contextMenu.getItems().clear();
        }
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (String item : items) {
            MenuItem menuItem =convert.fromString(item);
            // item点击事件
            menuItem.setOnAction(event -> {
                String text = convert.toString((MenuItem)event.getSource());
                itemAction.accept(text);
            });
            contextMenu.getItems().add(menuItem);
        }
        return true;
    }

    /**
     * 根据字符串生成MenuItem，以及从MenuItem获取字符串
     */
    private class MenuItemStringConvert extends StringConverter<MenuItem> {

        @Override
        public String toString(MenuItem menuItem) {
            Label label = (Label) menuItem.getGraphic();
            return label.getText();
        }

        @Override
        public MenuItem fromString(String s) {
            MenuItem menuItem = new MenuItem();
            // menuItem宽度设置，通过内部组件实现
            Label label = new Label(s);
            label.setWrapText(true);
            // 宽度绑定
            // 为啥总是多了22？应该是MenuItem内部有一些额外的空间占用吧
            label.prefWidthProperty().bind(SearchableTextField.this.widthProperty().subtract(22));
            menuItem.setGraphic(label);
            return menuItem;
        }
    }
}
