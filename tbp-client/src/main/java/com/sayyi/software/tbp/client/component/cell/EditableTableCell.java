package com.sayyi.software.tbp.client.component.cell;

import com.sayyi.software.tbp.client.component.util.Scheduler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * 拷贝出来的组件，修改了startEdit方法
 * @author xuchuang
 * @date 2021/9/18
 */
public class EditableTableCell<S,T> extends TableCell<S,T> {

    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter) {
        return list -> new EditableTableCell<S,T>(converter);
    }

    private TextField textField;
    public EditableTableCell() {
        this(null);
    }
    public EditableTableCell(StringConverter<T> converter) {
        this.getStyleClass().add("text-field-table-cell");
        setConverter(converter);
    }
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }
    @Override public void startEdit() {
        if (! isEditable()
                || ! getTableView().isEditable()
                || ! getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = CellUtils.createTextField(this, getConverter());
            }

            CellUtils.startEdit(this, getConverter(), null, null, textField);
        }
    }
    @Override public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, getConverter(), null);
    }
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, getConverter(), null, null, textField);
    }
}
