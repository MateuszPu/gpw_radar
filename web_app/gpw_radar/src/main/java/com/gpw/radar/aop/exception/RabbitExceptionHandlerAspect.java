package com.gpw.radar.aop.exception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

@Aspect
public class RabbitExceptionHandlerAspect {

    private final Logger logger = LoggerFactory.getLogger(RabbitExceptionHandlerAspect.class);

    @Around("@annotation(com.gpw.radar.aop.exception.RabbitExceptionHandler)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            logger.error("Error occurs during consuming message: {}", ex.fillInStackTrace().getMessage());
            throw new AmqpRejectAndDontRequeueException(ex);
        }
    }
}
