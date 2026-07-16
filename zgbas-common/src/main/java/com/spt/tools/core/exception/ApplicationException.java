package com.spt.tools.core.exception;

/**
 * 应用级异常
 * 
 * @author liuxl
 */
public class ApplicationException extends SystemException
{
	private static final long serialVersionUID = 1L;
	
	public ApplicationException(int errorId, String msg)
    {
    	super(errorId,errorId+":"+ msg);
    }
	public ApplicationException(int errorId, String msg,Throwable e)
	{
		super(errorId,errorId+":"+ msg,e);
	}
	
    public ApplicationException(String msg) 
    {
		super(msg);
	}
    
    public ApplicationException(int errorId)
    {
    	super(errorId);
    }
    
    public ApplicationException(Throwable e)
	{
		super(e);
	}
}
