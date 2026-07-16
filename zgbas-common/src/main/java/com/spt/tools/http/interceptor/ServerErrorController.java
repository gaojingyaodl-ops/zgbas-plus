package com.spt.tools.http.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spt.tools.core.constants.CommonErrorId;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.exception.SystemException;
import com.spt.tools.core.json.JsonUtil;

/**
 * 内部微服务统一异常处理
 * @author wlddh
 *
 */
@Controller
public class ServerErrorController implements ErrorController {
	private static final String ERR_STATUSCODE = "javax.servlet.error.status_code";
	private static final String ERR_EXCEPTION = "javax.servlet.error.exception";

	private static Logger log = LoggerFactory.getLogger(ServerErrorController.class);

	@Value("${error.path:/error}")
	private String errorPath;

	public String getErrorPath() {
		return errorPath;
	}

	@RequestMapping(value = "${error.path:/error}", produces = "application/json")
	public @ResponseBody ResponseEntity<ErrorResp> error(HttpServletRequest request) {
		final int status = getErrorStatus(request);
		final ErrorResp resp = getErrorMessage(request);
		resp.setStatus(status);
		log.warn("api error:{}",JsonUtil.obj2Json(resp));
		return ResponseEntity.status(status).body(resp);
	}

	private int getErrorStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute(ERR_STATUSCODE);

		return statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

	private Throwable getCauseException(Throwable e) {
		if (e == null || e.getCause() == null || e instanceof SystemException) {
			return e;
		}
		return getCauseException(e.getCause());
	}
	
	private ErrorResp getErrorMessage(HttpServletRequest request) {
		ErrorResp error = new ErrorResp();
		String uri =(String) request.getAttribute("javax.servlet.forward.request_uri");
		error.setPath(uri);
		Throwable exc = (Throwable) request.getAttribute(ERR_EXCEPTION);
		exc = getCauseException(exc);
		if (exc == null) {
			error.setErrorId(CommonErrorId.ERROR_UNKNOWN);
			error.setMessage("未知错误");
			return error;
		}
		
		String msg = null;
		if (exc instanceof SystemException) {
			SystemException sysEx = (SystemException) exc;
			int errorId = sysEx.getErrorId();
			String message = sysEx.getMessage();
			error.setErrorId(errorId);
			error.setMessage(message);
		} else {
			msg = exc != null ? exc.getMessage() : "Unexpected error occurred";
			error.setErrorId(CommonErrorId.ERROR_UNKNOWN);
			error.setMessage(msg);
		}
		log.info(">>>>ApiErrorController.getErrorMessage - {}", JsonUtil.obj2Json(error));

		return error;
	}
}