package com.spt.tools.core.exception;

import com.spt.tools.core.constants.CommonErrorId;

/**
 * 未指定异常
 * 
 * @author liuxl
 */
public class UnassignedException extends ApplicationException
{
    private static final long serialVersionUID = 1L;

    public UnassignedException(String msg)
    {
        super(msg);
        this.errorId = CommonErrorId.ERROR_UNKNOWN;
    }
    
    public UnassignedException(Throwable cause)
    {
        super(cause);
        this.errorId = CommonErrorId.ERROR_UNKNOWN;
    }
    
}
