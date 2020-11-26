package com.sayyi.software.tbp.core.flow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解，标注在Processor的各个方法上边，用于维护请求类型与处理方法的对应关系
 * @author SayYi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindType {

    /**
     * 这个就是记录opType的
     * @return  opType
     */
    int value();
}
