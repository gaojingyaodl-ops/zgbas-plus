/**
 * 
 */
package com.spt.tools.http.feign;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.exception.WebApplicationException;
import com.spt.tools.core.json.JsonUtil;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

/**
 * @author wlddh
 *
 */
public class CommErrorDecoder implements ErrorDecoder {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Exception decode(String methodKey, Response response) {
		Exception exception = null;
		try {
			String err = Util.toString(response.body().asReader());
			ErrorResp resEntity = JsonUtil.json2Object(ErrorResp.class, err);
			// 为了说明我使用的 WebApplicationException 基类，去掉了封装
			int status = response.status();
			exception =new  WebApplicationException(status,resEntity);
			
			// exception = new ApplicationException(resEntity.getErrorId(),
			// resEntity.getMessage());
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		// 这里只封装4开头的请求异常
//		if (response.status() >= 400 && response.status() < 500) {
//			exception = new HystrixBadRequestException("request exception wrapper", exception);
//		} else {
//			logger.error(exception.getMessage(), exception);
//		}
		return exception;
	}

}
