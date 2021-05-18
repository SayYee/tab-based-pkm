package com.sayyi.software.tbp.generator.fieldstr;

/**
 * java 类型的 字符串输出。没有设置getter setter，使用lombok的注解来实现。
 */
public interface JType {

    /** 序列化、反序列化参数名称 */
    String SERIALIZE_PARAM  = "archive";

    /**
     * 获取对应类型在dsl中的描述
     * @return
     */
    String getSign();

    /**
     * import 字符串
     * @return
     */
    default String importStr(){
        return null;
    }

    /**
     * 字段声明 字符串
     * @param fieldName
     * @return
     */
    String declareStr(String fieldName);

    /**
     * 序列化方法 字符串
     * @param fieldName
     * @return
     */
    String serialize(String fieldName);

    /**
     * 反序列化 字符串
     * @param fieldName
     * @return
     */
    String deserialize(String fieldName);

}
