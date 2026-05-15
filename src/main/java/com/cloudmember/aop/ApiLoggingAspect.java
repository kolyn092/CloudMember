package com.cloudmember.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ApiLoggingAspect {

    @Around("execution(* com.cloudmember.controller..*.*(..))")
    public Object logApiRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentHttpRequest();

        String requestUri = request != null ? request.getRequestURI() : "UNKNOWN";
        Object[] args = joinPoint.getArgs();

        // 요청 로그
        log.info("[API-LOG] request uri={} | args={}", requestUri, Arrays.toString(args));

        Object result = joinPoint.proceed();

        // 응답 로그
        log.info("[API-LOG] response uri={} | args={}", requestUri, result.toString());

        return result;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return attributes != null ? attributes.getRequest() : null;
    }
}
