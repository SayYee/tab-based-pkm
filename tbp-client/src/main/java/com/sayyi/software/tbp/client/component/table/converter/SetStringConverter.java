package com.sayyi.software.tbp.client.component.table.converter;

import javafx.collections.FXCollections;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;

/**
 * string与ObservableSet转换的类，单例的就行
 */
public class SetStringConverter extends StringConverter<Set<String>> {

    private static final SetStringConverter instance = new SetStringConverter();

    private SetStringConverter() {}

    public static SetStringConverter getInstance() {
        return instance;
    }

    @Override
    public String toString(Set<String> object) {
        StringJoiner joiner = new StringJoiner(".");
        joiner.setEmptyValue("");
        for (String s : object) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    @Override
    public Set<String> fromString(String string) {
        Set<String> result = FXCollections.observableSet();
        if (string == null || "".equals(string)) {
            return result;
        }
        String[] split = string.split("\\.");
        result.addAll(Arrays.asList(split));
        return result;
    }
}