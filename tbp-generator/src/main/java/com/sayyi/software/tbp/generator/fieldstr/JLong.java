package com.sayyi.software.tbp.generator.fieldstr;

public class JLong implements JType{
    @Override
    public String getSign() {
        return "long";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private long " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return JType.SERIALIZE_PARAM + ".writeLong(" + fieldName + ");";
    }

    @Override
    public String deserialize(String fieldName) {
        return fieldName + " = " + JType.SERIALIZE_PARAM + ".readLong();";
    }
}
