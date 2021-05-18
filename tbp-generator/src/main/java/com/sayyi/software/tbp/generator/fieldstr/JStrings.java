package com.sayyi.software.tbp.generator.fieldstr;

/**
 *
 * @author xuchuang
 */
public class JStrings implements JType{
    @Override
    public String getSign() {
        return "strings";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private String[] " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return "if (" + fieldName + " == null) {\n" +
                "\t\t\t"+ JType.SERIALIZE_PARAM +".writeInt(-1);\n" +
                "\t\t} else {\n" +
                "\t\t\t"+ JType.SERIALIZE_PARAM +".writeInt(" + fieldName + ".length);\n" +
                "\t\t\tfor (String s : "+fieldName+") {\n" +
                "\t\t\t\t"+ JType.SERIALIZE_PARAM +".writeString(s);\n" +
                "\t\t\t}\n" +
                "\t\t}";
    }

    @Override
    public String deserialize(String fieldName) {
        return "int " +fieldName+ "_size = " +JType.SERIALIZE_PARAM+".readInt();\n" +
                "\t\tif ("+fieldName+"_size == -1) {\n" +
                "\t\t\t"+fieldName+" = null;\n" +
                "\t\t} else {\n" +
                "\t\t\t"+fieldName+" = new String["+fieldName+"_size];\n" +
                "\t\t\tfor(int i = 0; i < "+fieldName+"_size; i++) {\n" +
                "\t\t\t\t"+fieldName+"[i] = "+JType.SERIALIZE_PARAM+".readString();\n" +
                "\t\t\t}\n" +
                "\t\t}";
    }
}
