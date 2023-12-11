package com.example.project3.config;

import com.example.project3.exception.MissingTokenException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class AopConfig {

    @Pointcut("execution(* com.example.project3.controller..*.*(..))")
    private void cut() {}

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("{} 메소드 실행", method.getName());

        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg == null) {
                log.error("요청 Parameter가 없습니다.");
                throw new MissingTokenException("error");
            }
        }
    }
}

