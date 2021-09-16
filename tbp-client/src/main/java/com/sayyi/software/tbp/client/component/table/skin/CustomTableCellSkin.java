package com.sayyi.software.tbp.client.component.table.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.TableCellSkinBase;

/**
 * 为了安装自定义的TableCell行为，参考自带的TableCellSkin实现
 * @param <S>
 * @param <T>
 */
public class CustomTableCellSkin<S,T> extends TableCellSkinBase<S, T, TableCell<S,T>> {

    private final BehaviorBase<TableCell<S,T>> behavior;

    public CustomTableCellSkin(TableCell<S, T> control) {
        super(control);
        behavior = new CustomTableCellBehavior<>(control);
    }

    @Override public void dispose() {
        super.dispose();

        if (behavior != null) {
            behavior.dispose();
        }
    }

    /** {@inheritDoc} */
    @Override public ReadOnlyObjectProperty<TableColumn<S,T>> tableColumnProperty() {
        return getSkinnable().tableColumnProperty();
    }
}
