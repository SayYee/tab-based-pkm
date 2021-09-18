package com.sayyi.software.tbp.client.component.tree.skin;

import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import com.sun.javafx.scene.control.behavior.TreeViewBehavior;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

/**
 * @author xuchuang
 * @date 2021/9/18
 */
public class CustomTreeCellBehavior<T> extends TreeCellBehavior<T> {

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     *************************************************************************
     * @param control*/
    public CustomTreeCellBehavior(TreeCell<T> control) {
        super(control);
    }

    @Override
    protected void handleClicks(MouseButton button, int clickCount, boolean isAlreadySelected) {
        // handle editing, which only occurs with the primary mouse button
        TreeItem<T> treeItem = getNode().getTreeItem();
        if (button == MouseButton.PRIMARY) {
            if (clickCount == 1 && isAlreadySelected) {
            } else if (clickCount == 1) {
                // cancel editing
                edit(null);
            } else if (clickCount == 2 && treeItem.isLeaf()) {
            } else if (clickCount % 2 == 0) {
                // try to expand/collapse branch tree item
                treeItem.setExpanded(! treeItem.isExpanded());
            }
        }
    }
}
