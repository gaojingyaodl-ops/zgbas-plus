package com.spt.tools.core.exception;

/**
 * 内部505错误异常
 * 
 * @author lbqi/krics 2016-6-12
 */
public  class InternalErrorException extends Exception
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
	public InternalErrorException(String msg)
	{
		super(msg);
	}
	
	/**
	 * 构造器
	 * @param errorId 错误ID
	 */
	public InternalErrorException(int errorId)
	{
		this.errorId = errorId;
	}
	
	/**
	 * 构造器
	 * @param errorId 错误ID
	 */
	public InternalErrorException(int errorId, String msg)
	{
		super(msg);
		this.errorId = errorId;
	}
	
	/**
	 * 构造器
	 * @param e	
	 */
	public InternalErrorException(Throwable e)
	{
		super(e);
	}
}
