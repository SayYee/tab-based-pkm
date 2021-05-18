package com.sayyi.software.tbp.generator.fieldstr;

public class JString implements JType{
    @Override
    public String getSign() {
        return "string";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private String " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return JType.SERIALIZE_PARAM + ".writeString(" + fieldName + ");";
    }

    @Override
    public String deserialize(String fieldName) {
        return fieldName + " = " + JType.SERIALIZE_PARAM + ".readString();";
    }
}
