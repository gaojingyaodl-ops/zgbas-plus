package com.spt.bas.purchase.wx.server.aop;

import com.spt.bas.purchase.wx.server.dao.WxUserInfoDao;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class ServiceAop {
    private static final Logger logger = LoggerFactory.getLogger(ServiceAop.class);

    @Autowired
    private WxUserInfoDao userInfoDao;

    @Pointcut("execution(* com.*.*.*.*.*.service..*.*(..))")
    public void aspect() {
    }

    @Around("(aspect()) ")
    public Object serviceLog(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String targetName = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        Object retVal = pjp.proceed(args);
        long end = System.currentTimeMillis();
        long cost = end - start;
        logger.info("执行耗时：{}.{}, cost {}ms", new Object[]{targetName, methodName, cost});
        return retVal;
    }

}
