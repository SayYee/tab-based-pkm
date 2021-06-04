package com.sayyi.software.tbp.generator.dsl.fieldstr;

/**
 * @author xuchuang
 * @date 2021/5/18
 */
public class JBytes implements JType{
    @Override
    public String getSign() {
        return "bytes";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private byte[] " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return JType.SERIALIZE_PARAM + ".writeBuffer(" + fieldName + ");";
    }

    @Override
    public String deserialize(String fieldName) {
        return fieldName + " = " + JType.SERIALIZE_PARAM + ".readBuffer();";
    }
}
