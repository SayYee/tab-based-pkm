package com.sayyi.software.tbp.web.config;

import com.sayyi.software.tbp.web.common.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;

/**
 * @author SayYi
 */
@Slf4j
@Component
@Aspect
public class ControllerAop {

    @Around("execution(public com.sayyi.software.tbp.web.common.ResultBean *(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint proceedingJoinPoint) {
        long startTime = System.currentTimeMillis();
        ResultBean<?> result;
        try {
            result = (ResultBean<?>) proceedingJoinPoint.proceed();
            log.info(proceedingJoinPoint.getSignature() + " use time: " + (System.currentTimeMillis() - startTime));
        } catch (Throwable throwable) {
            log.error(proceedingJoinPoint.getSignature() + " error", throwable);
            result = ResultBean.error(throwable.getMessage());
        }
        return result;
    }
}
