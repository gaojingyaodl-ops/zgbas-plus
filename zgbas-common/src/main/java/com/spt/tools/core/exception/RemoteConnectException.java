package com.spt.tools.core.exception;

import com.spt.tools.core.constants.CommonErrorId;

/**
 * 远程连接异常
 * 
 * @author wangyilin
 *
 */
public class RemoteConnectException extends ApplicationException
{
	private static final long serialVersionUID = 1L;

	public RemoteConnectException() 
	{
		super("");
		this.errorId = CommonErrorId.ERROR_REMOTE_CONNECTION_FAIL;
	}
	public RemoteConnectException(String msg) 
	{
		super(msg);
		this.errorId = CommonErrorId.ERROR_REMOTE_CONNECTION_FAIL;
	}

}
