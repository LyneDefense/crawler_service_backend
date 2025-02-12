package com.app.tuantuan.config;

import cn.hutool.core.date.StopWatch;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

  @Around("execution(public * com.app.tuantuan.controller..*(..))")
  public Object logApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取方法签名和参数
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();
    Object[] args = joinPoint.getArgs();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    log.info("[请求: {}.{} 参数: {}]", className, methodName, Arrays.toString(args));
    Object result = joinPoint.proceed();
    stopWatch.stop();
    log.info("[{}.{}返回成功，执行时间: {} ms]", className, methodName, stopWatch.getTotalTimeMillis());

    return result;
  }
}
