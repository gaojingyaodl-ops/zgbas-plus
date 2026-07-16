package com.spt.tools.core.exception;

import com.spt.tools.core.constants.CommonErrorId;

public class SecurityException extends SystemException
{
	private static final long serialVersionUID = -1064438870770169282L;

	public SecurityException(int errorId) 
	{
		super(CommonErrorId.ERROR_REMOTE_SECURITY_ERROR);
	}

}
