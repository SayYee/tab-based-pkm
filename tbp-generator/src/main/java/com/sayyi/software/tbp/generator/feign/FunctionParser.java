package com.sayyi.software.tbp.generator.feign;

import com.sayyi.software.tbp.generator.feign.model.FieldInfo;
import com.sayyi.software.tbp.generator.feign.model.FunctionInfo;
import com.sayyi.software.tbp.generator.feign.model.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuchuang
 * @date 2021/6/4
 */
public class FunctionParser {

    public static FunctionInfo parse(Class<?> function) {
        FunctionInfo functionInfo = new FunctionInfo();
        functionInfo.setFunctionName(function.getName());

        List<MethodInfo> methodInfos = new ArrayList<>();
        functionInfo.setMethodInfos(methodInfos);
        for (Method declaredMethod : function.getDeclaredMethods()) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfos.add(methodInfo);
            // 方法名
            methodInfo.setMethodName(declaredMethod.getName());
            // 这个可以正确的处理泛型返回值。返回的字符串可以直接使用（数组、泛型、基本类型）。因为是完整的类路径，因此也不需要import了
            final Type genericReturnType = declaredMethod.getGenericReturnType();
            methodInfo.setReturnType(genericReturnType.getTypeName());
            // 参数列表
            // 类型
            List<FieldInfo> fieldInfos = new ArrayList<>();
            methodInfo.setFieldInfos(fieldInfos);
            for (Parameter parameter : declaredMethod.getParameters()) {
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfos.add(fieldInfo);

                fieldInfo.setParamName(parameter.getName());
                fieldInfo.setParamType(parameter.getParameterizedType().getTypeName());
            }
            List<String> exceptions = new ArrayList<>();
            methodInfo.setExceptions(exceptions);
            for (Type genericExceptionType : declaredMethod.getGenericExceptionTypes()) {
                exceptions.add(genericExceptionType.getTypeName());
            }
        }
        return functionInfo;
    }

}
