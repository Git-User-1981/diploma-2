package ru.book_shop.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogExecutionTime {
    @Around("within(ru.book_shop.controllers..*) && !@target(ru.book_shop.annotations.NotLogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = jp.proceed();
        log.info("Запрос: " + jp.getSignature().getName() + ", время выполнения " + (System.currentTimeMillis() - start) + " мс");
        return proceed;
    }
}
