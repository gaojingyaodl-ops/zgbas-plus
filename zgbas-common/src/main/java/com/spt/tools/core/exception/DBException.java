package com.spt.tools.core.exception;

/**
 * 数据库操作异常类
 * 
 * @author wangyilin
 *
 */
public class DBException extends SystemException
{
	private static final long serialVersionUID = 1L;
	
	public DBException(int errorId) 
	{
		super(errorId);
	}
	
	public DBException(int errorId, String errorMessage)
	{
		super(errorId, errorMessage);
	}
}
