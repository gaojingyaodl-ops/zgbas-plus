package com.spt.tools.core.exception;

/**
 * 过滤器Exception
 * 
 * @author wangyilin
 *
 */
public class FilterException extends SystemException
{
	private static final long serialVersionUID = 1L;
	
	private boolean closeConnection; //是否关闭连接
	
	public FilterException(String msg)
	{
		super(msg);
		closeConnection = false;
	}

	public FilterException(String msg, boolean closeConnection)
	{
		super(msg);
		this.closeConnection = closeConnection;
	}
	
	public boolean isCloseConnection() {
		return closeConnection;
	}

	public void setCloseConnection(boolean closeConnection) {
		this.closeConnection = closeConnection;
	}
	
	
	
}
