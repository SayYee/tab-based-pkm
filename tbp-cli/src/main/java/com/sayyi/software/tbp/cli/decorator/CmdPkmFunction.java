package com.sayyi.software.tbp.cli.decorator;

import com.sayyi.software.tbp.core.facade.PkmFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author SayYi
 */
public class CmdPkmFunction implements InvocationHandler {

    private final PkmFunction pkmFunction;
    private final ResultHolder resultHolder;

    public CmdPkmFunction(PkmFunction pkmFunction, ResultHolder resultHolder) {
        this.pkmFunction = pkmFunction;
        this.resultHolder = resultHolder;
    }

    public static PkmFunction create(PkmFunction pkmFunction, ResultHolder resultHolder) {
        CmdPkmFunction cmdPkmFunction = new CmdPkmFunction(pkmFunction, resultHolder);
        return (PkmFunction) Proxy.newProxyInstance(CmdPkmFunction.class.getClassLoader(),
                new Class[]{PkmFunction.class},
                cmdPkmFunction);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Object result = method.invoke(pkmFunction, args);
            resultHolder.setResult(result);
            return result;
        } catch (InvocationTargetException e) {
            // 获取真实的异常信息。
            // 所有的异常都抛出，交给外层管理
            throw e.getCause();
        }
    }
}
