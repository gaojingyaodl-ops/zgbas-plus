package com.spt.tools.aop.task;

import java.util.Date;

/**
 * 任务记录表
 * @author wangyilin
 *
 */
public class TaskMonitorEntity 
{

	private String taskName;		//任务名称
	private String thdId;			//线程ID
	private String executeRst;		//执行结果
	private String exceptionMsg;	//异常信息
	private Date executeTime;		//最后执行时间点
	private long executeLength;		//最后执行时间长度
	private Date updateTime;
	private String updateUser;
	
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getExceptionMsg() {
		return exceptionMsg;
	}
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public long getExecuteLength() {
		return executeLength;
	}
	public void setExecuteLength(long executeLength) {
		this.executeLength = executeLength;
	}
	public String getThdId() {
		return thdId;
	}
	public void setThdId(String thdId) {
		this.thdId = thdId;
	}
	public String getExecuteRst() {
		return executeRst;
	}
	public void setExecuteRst(String executeRst) {
		this.executeRst = executeRst;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
}
