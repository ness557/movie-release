package com.ness.movie_release_web.util.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *) && within(com.ness.movie_release_web.service..*)")
    public void movieReleaseServicesPointcut() {
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *) && within(com.ness.movie_release_web.controller..*)")
    public void movieReleaseControllersPointcut() {
    }

    @Pointcut("within(@org.springframework.stereotype.Component *) && within(com.ness.movie_release_web..*)")
    public void movieReleaseComponentsPointcut() {
    }

    @Around("movieReleaseServicesPointcut() || movieReleaseControllersPointcut() || movieReleaseComponentsPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("{}.{}() - start: with argument[s] = {}", joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().toString(), Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.debug("{}.{}() - end: with result = {}", joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().toString(), result);
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
