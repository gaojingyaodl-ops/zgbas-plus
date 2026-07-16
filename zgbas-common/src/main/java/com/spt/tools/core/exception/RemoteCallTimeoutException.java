package com.spt.tools.core.exception;

import com.spt.tools.core.constants.CommonErrorId;

/**
 * 远程方法调用超时
 * @author wangyilin
 *
 */
public class RemoteCallTimeoutException extends ApplicationException
{
	private static final long serialVersionUID = 1L;

	public RemoteCallTimeoutException()
	{
		super("");
		this.errorId = CommonErrorId.ERROR_REMOTE_CALL_TIMEOUT;
	}
}
