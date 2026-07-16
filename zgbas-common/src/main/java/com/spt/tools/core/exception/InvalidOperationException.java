package com.spt.tools.core.exception;

/**
 * 不正确的操作异常
 * @author wangyilin
 *
 */
public class InvalidOperationException extends ApplicationException
{
	private static final long serialVersionUID = 1L;

	private String operation;

	public InvalidOperationException(String operation)
	{
		super(operation);
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
}
