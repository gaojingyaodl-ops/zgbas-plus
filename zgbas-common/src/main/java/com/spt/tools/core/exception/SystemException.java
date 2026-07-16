package com.spt.tools.core.exception;

/**
 * 系统异常
 * 
 * @author liuxl
 */
public abstract class SystemException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 错误ID
	 */
	protected int errorId; 

	public int getErrorId() {
		return errorId;
	}

	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}
	
	/**
	 * 构造器
	 * @param msg 错误消息
	 */
	public SystemException(String msg)
	{
		super(msg);
	}
	
	/**
	 * 构造器
	 * @param errorId 错误ID
	 */
	public SystemException(int errorId)
	{
		this.errorId = errorId;
	}
	
	/**
	 * 构造器
	 * @param errorId 错误ID
	 */
	public SystemException(int errorId, String msg)
	{
		super(msg);
		this.errorId = errorId;
	}
	public SystemException(int errorId, String msg,Throwable e)
	{
		super(msg,e);
		this.errorId = errorId;
	}
	/**
	 * 构造器
	 * @param e	
	 */
	public SystemException(Throwable e)
	{
		super(e);
	}
}
