package com.spt.bas.server.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.NestedServletException;

import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ErrorResp;
import com.spt.tools.core.exception.SystemException;
import com.spt.tools.core.exception.WebApplicationException;


@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController implements ErrorController {
//	private static Logger log = LoggerFactory.getLogger(BasicErrorController.class);
	private static final String ERR_STATUSCODE = "javax.servlet.error.status_code";
	private static final String ERR_EXCEPTION = "javax.servlet.error.exception";
	@Value("${error.path:/error}")
	private String errorPath;

	@Autowired
	private ErrorAttributes errorAttributes;
	@Autowired(required = false)
	private List<ErrorViewResolver> errorViewResolvers;

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute(ERR_STATUSCODE);
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	private ModelAndView resolveErrorView(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
			Map<String, Object> model) {
		for (ErrorViewResolver resolver : this.errorViewResolvers) {
			ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
			if (modelAndView != null) {
				return modelAndView;
			}
		}
		return null;
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		WebRequest webRequest = new ServletWebRequest(request);
		ErrorAttributeOptions options =ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
		return this.errorAttributes.getErrorAttributes(webRequest, options);
	}

	@RequestMapping(produces = "text/html")
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		Map<String, Object> model = getErrorAttributes(request, true);
		ErrorResp resp = getErrorResp(request);
		if (resp != null) {
			model.put("errorId", resp.getErrorId());
			model.put("errorMsg", resp.getMessage());
		}
		response.setStatus(status.value());
		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
		// insertError(request);
		String errorPage = "error";
		if (status == HttpStatus.BAD_REQUEST) {
			errorPage = "error/400";
		} else if (status == HttpStatus.NOT_FOUND) {
			errorPage = "error/404";
		} else if (status == HttpStatus.UNAUTHORIZED) {
			errorPage = "error/401";
		} else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
			errorPage = "error/500";
		}

		return modelAndView == null ? new ModelAndView(errorPage, model) : modelAndView;
	}

	@RequestMapping
	public @ResponseBody ResponseEntity<RespVo<ErrorResp>> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, true);
		HttpStatus status = getStatus(request);
		RespVo<ErrorResp> respVo = new RespVo<>();
		final ErrorResp err = getErrorResp(request);
		err.setStatus(status.value());
		err.setPath(String.valueOf(body.get("path")));
		err.setError(String.valueOf(body.get("error")));
		err.setTimestamp(LocalDateTime.now());
		respVo.setData(err);
		respVo.setMessage(err.getMessage());
		respVo.setCode(err.getErrorId() + "");
		return ResponseEntity.status(status).body(respVo);
	}

	private ErrorResp getErrorResp(HttpServletRequest request) {
		Throwable e = (Throwable) request.getAttribute(ERR_EXCEPTION);
		ErrorResp resp = getErrorResp(e);
		return resp;
	}
	private static WebApplicationException getWebApplicationException(Throwable e) {
		WebApplicationException ex = null;
		if (e == null ) {
			return ex;
		}
		if (e instanceof WebApplicationException) {
			return (WebApplicationException)e;
		}
		if (e.getCause() == null) {
			return ex;
		}
		if (e.getCause() instanceof WebApplicationException) {
			ex = (WebApplicationException) e.getCause();
		} else if (e.getCause().getCause() != null) {
			if (e.getCause().getCause() instanceof WebApplicationException) {
				ex = (WebApplicationException) e.getCause().getCause();
			}
		}
		return ex;
	}

	private static SystemException getSystemException(Throwable e) {
		SystemException ex=null;
		if (e instanceof SystemException) {
			return (SystemException) e;
		} else if (e instanceof NestedServletException) {
			if (e.getCause() != null && e.getCause() instanceof SystemException) {
				return (SystemException) e.getCause();
			}
		}
		return ex;
	}
	public static ErrorResp getErrorResp(Throwable e) {
		ErrorResp resp;
		WebApplicationException ex = getWebApplicationException(e);
		if (ex != null) {
			resp = ex.getResp();
		} else {
			resp = new ErrorResp();
			if (e == null) {
				return resp;
			}
			SystemException es = getSystemException(e);
			if (es!=null) {
				resp.setErrorId(es.getErrorId());
				resp.setMessage(es.getMessage());
			}else {
				resp.setMessage(e.getMessage());
			}
		}
		return resp;
	}

	public String getErrorPath() {
		return errorPath;
	}

}
