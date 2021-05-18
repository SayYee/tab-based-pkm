package com.sayyi.software.tbp.generator.fieldstr;

/**
 * @author xuchuang
 * @date 2021/5/18
 */
public class JListLong implements JType{

    @Override
    public String getSign() {
        return "listlong";
    }

    @Override
    public String importStr() {
        return "import java.util.LinkedList;\n" +
                "import java.util.List;";
    }

    @Override
    public String declareStr(String fieldName) {
        return "private List<Long> " + fieldName + ";";
    }

    @Override
    public String serialize(String fieldName) {
        return "if (" + fieldName + " == null) {\n" +
                "\t\t\t"+ JType.SERIALIZE_PARAM +".writeInt(-1);\n" +
                "\t\t} else {\n" +
                "\t\t\t"+ JType.SERIALIZE_PARAM +".writeInt(" + fieldName + ".size());\n" +
                "\t\t\tfor (Long s : "+fieldName+") {\n" +
                "\t\t\t\t"+ JType.SERIALIZE_PARAM +".writeLong(s);\n" +
                "\t\t\t}\n" +
                "\t\t}";
    }

    @Override
    public String deserialize(String fieldName) {
        return "int " +fieldName+ "_size = " +JType.SERIALIZE_PARAM+".readInt();\n" +
                "\t\tif ("+fieldName+"_size == -1) {\n" +
                "\t\t\t"+fieldName+" = null;\n" +
                "\t\t} else {\n" +
                "\t\t\t"+fieldName+" = new LinkedList<>();\n" +
                "\t\t\tfor(int i = 0; i < "+fieldName+"_size; i++) {\n" +
                "\t\t\t\t"+fieldName+".add("+JType.SERIALIZE_PARAM+".readLong());\n" +
                "\t\t\t}\n" +
                "\t\t}";
    }
}
