package com.sayyi.software.tbp.generator.dsl.fieldstr;

public class JInt implements JType{
    @Override
    public String getSign() {
        return "int";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private int " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return JType.SERIALIZE_PARAM + ".writeInt(" + fieldName + ");";
    }

    @Override
    public String deserialize(String fieldName) {
        return fieldName + " = " + JType.SERIALIZE_PARAM + ".readInt();";
    }
}
