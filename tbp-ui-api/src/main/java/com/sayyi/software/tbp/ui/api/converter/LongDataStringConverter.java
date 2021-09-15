package com.sayyi.software.tbp.ui.api.converter;

import javafx.util.StringConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LongDataStringConverter extends StringConverter<Long> {

    private static final LongDataStringConverter instance = new LongDataStringConverter();

    public static LongDataStringConverter getInstance() {
        return instance;
    }

    private LongDataStringConverter() {}

    private static final String format = "yyyy-MM-dd HH:mm:ss";
    @Override
    public String toString(Long object) {
        if (object == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(object);
        return sdf.format(date);
    }

    @Override
    public Long fromString(String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(string).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
