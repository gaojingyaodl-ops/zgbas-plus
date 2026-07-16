package com.spt.tools.aop.interceptor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Stopwatch;
import com.spt.tools.aop.dao.TaskMonitorDao;
import com.spt.tools.aop.task.TaskMonitorEntity;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.core.task.ITask;

@Aspect
public class TaskInterceptor {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired(required = false)
	private TaskMonitorDao taskDao;

	private volatile boolean allowTask = false;

	@PostConstruct
	private void init() {
		String allowTaskStr = PropertiesUtil.getProperty("task.allow");
		if (StringUtils.isNotBlank(allowTaskStr)) {
			allowTask = BooleanUtils.toBoolean(allowTaskStr);
		}
	}

	@Around("execution(* com..*.*.task..*.*(..)) && @annotation(org.springframework.scheduling.annotation.Scheduled)  && this(task)")
	public Object around(ProceedingJoinPoint pjp, ITask task) throws Throwable {
		String taskName = task.getTaskName();
		String currentThread = Thread.currentThread().getName();
		if (!allowTask) {
			if (logger.isDebugEnabled()) {
				logger.debug("Skip batch ({}): {}", currentThread, taskName);
			}
			return null;
		}
		TaskMonitorEntity entity = new TaskMonitorEntity();
		entity.setTaskName(taskName); // 任务名称
		entity.setThdId(String.valueOf(Thread.currentThread().getId())); // 线程ID
		entity.setExecuteRst("T"); // 执行结果
		if (logger.isDebugEnabled()) {
			logger.debug("Begin batch ({}): {}", currentThread, taskName);
		}
		Stopwatch stopWatch = Stopwatch.createStarted();
		long stime = 0l;
		try {
			entity.setExecuteTime(new Date()); // 执行时间
			Object obj = pjp.proceed();
			stime = stopWatch.elapsed(TimeUnit.MILLISECONDS);
			if (taskDao != null) {
				entity.setExecuteLength(stime); // 执行时长
				taskDao.updateTask(entity);
			}

			return obj;
		} catch (Exception e) {
			logger.error("batch error: {}", taskName, e);
			if (taskDao != null) {
				entity.setExceptionMsg(e.getMessage()); // 错误信息
				entity.setExecuteLength(stime); // 执行时长
				entity.setExecuteRst("F"); // 执行结果
				taskDao.updateTask(entity);
			}
			return null;
		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("End batch ({}): {}, elapsed = {} (ms)", currentThread, taskName, stime);
			}
		}
	}
}