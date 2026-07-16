package com.spt.tools.core.exception;

import com.spt.tools.core.constants.CommonErrorId;

/**
 * 输入参数校验异常
 * 
 * @author wangyilin
 *
 */
public class InvalidParamException extends ApplicationException
{
	private static final long serialVersionUID = 1L;
	
	private String paramName; //错误参数名称
	
	/**
	 * 构造器
	 * 
	 * @param paramName	错误参数名称
	 */
	public InvalidParamException(String paramName)
	{
		super(paramName);
		this.setErrorId(CommonErrorId.ERROR_UP_PARAM);
		this.setParamName(paramName);
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
