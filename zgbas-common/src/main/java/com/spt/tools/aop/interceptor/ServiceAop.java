/**
 * 
 */
package com.spt.tools.aop.interceptor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.DBException;
import com.spt.tools.core.exception.UnassignedException;
import com.spt.tools.core.json.JsonUtil;

/**
 * @author huangjian
 *
 */
@Aspect
public class ServiceAop {
	private static final Logger logger = LoggerFactory.getLogger(ServiceAop.class);

	@Autowired
	private Environment env;
	
	@Pointcut("execution(* com.*.*.*.service..*.*(..))")
	public void aspect() {
	};

	@Pointcut("@target(com.spt.tools.aop.annotation.ServiceExceptionAop) || @annotation(com.spt.tools.aop.annotation.ServiceExceptionAop) ")
	public void annotation() {
	};

	@Pointcut("@target(com.spt.tools.aop.annotation.ServiceLogAop) || @annotation(com.spt.tools.aop.annotation.ServiceLogAop) ")
	public void logAop() {
	};


	@Around("(aspect())  && logAop()")
	public Object serviceLog(ProceedingJoinPoint pjp) throws Throwable {
		// start stopwatch
		long start = System.currentTimeMillis();
		String targetName = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();
		Object[] args = pjp.getArgs();
		Object retVal = pjp.proceed(args);
		// stop stopwatch
		long end = System.currentTimeMillis();

		long cost = end - start;
		logger.info("执行耗时：{}.{}, cost {}ms", targetName, methodName, cost);
		return retVal;
	}

	// 配置抛出异常后通知,使用在方法aspect()上注册的切入点
	@AfterThrowing(pointcut = "aspect() && annotation()", throwing = "ex")
	public void afterThrow(JoinPoint jp, Exception ex) throws ApplicationException, DBException {
		Object[] args = jp.getArgs();
		String argstr = args == null ? "" : JsonUtil.obj2Json(args);
		String targetName = jp.getTarget().getClass().getSimpleName();
		String methodName = jp.getSignature().getName();
		String msg = String.format("程序错误：%s.%s args:%s", targetName, methodName, argstr);
		logger.error(msg, ex);

		if (ex instanceof CannotGetJdbcConnectionException) {
			CannotGetJdbcConnectionException e = (CannotGetJdbcConnectionException) ex;
			DBException toThrow = new DBException(CommonErrorId.ERROR_DB_CONNECTION_FAIL, e.getMessage());
			throw toThrow;
		} else if (ex instanceof BadSqlGrammarException) {
			BadSqlGrammarException e = (BadSqlGrammarException) ex;
			DBException toThrow = new DBException(CommonErrorId.ERROR_DB_BAD_SQL, e.getMessage());
			throw toThrow;
		} else if (ex instanceof DataAccessException) {
			// 未映射的数据库异常
			DataAccessException e = (DataAccessException) ex;
			throw new DBException(CommonErrorId.ERROR_DB_UNMAPPED, e.getMessage());
		} else if (ex instanceof ApplicationException) {
			throw (ApplicationException) ex;
		} else if (ex instanceof DBException) {
			throw (DBException) ex;
		} else if (ex instanceof Exception) {
			throw new UnassignedException(ex); // 未知
		}
	}
}
