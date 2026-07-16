package com.spt.tools.core.exception;

/**
 * 没有查询到记录的异常
 * 
 * @author wangyilin
 *
 */
public class EmptyResultException extends ApplicationException 
{
	private static final long serialVersionUID = 1L;
	
	public EmptyResultException()
	{
		super("");
	}
	
	public EmptyResultException(String msg)
    {
        super(msg);
    }
}
