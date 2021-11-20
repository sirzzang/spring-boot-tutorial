package hello.hellospring.aop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Aspect
@Component
public class RequestLoggerAop {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggerAop.class);

    // controller 내 request pointcut 설정
    @Pointcut("within(hello.hellospring.controller..*)")
    public void onRequest() {

    }

    @Around("hello.hellospring.aop.RequestLoggerAop.onRequest()")
    public Object requestLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        try {
            return joinPoint.proceed();
        } finally {
            logger.info("Request: {} {} {}", req.getRequestURL(), req.getMethod(), req.getRequestURI());
        }
    }
}
