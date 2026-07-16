package com.spt.bas.web.open.apply;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.vo.api.ResSsoLoginRequestVo;
import com.spt.bas.client.vo.api.SsoLoginRequestVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.shiro.SsoUsernamePasswordToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

@Controller
@RequestMapping(value = "/open/user")
public class UserOpenController {
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Value("${zgBas.secret}")
	private String secretKey;

	private final Logger logger = LoggerFactory.getLogger(UserOpenController.class);

	/**
	 * 单点登录（如已经登录，则直接跳转）
	 *
	 * @param userCode
	 *            登录用户编码
	 * @param token
	 *            登录令牌，令牌组成：sso密钥+用户名+日期，进行md5加密，举例： String secretKey =
	 *            PropertiesUtil.getProperty("shiro.sso.secretKey"); String token =
	 *            Md5EncryptUtils.encrypt(secretKey + userCode +
	 *            DateUtils.getDate("yyyyMMdd"));
	 * @param url
	 *            登录成功后跳转的url地址。
	 * @param relogin
	 *            是否重新登录，需要重新登录传递true
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "ssoLogin",method=RequestMethod.GET)
	public String ssoLogin(SsoLoginRequestVo vo, Model model,HttpServletRequest request) throws UnsupportedEncodingException {
		if(StringUtils.isBlank(vo.getRedirectUrl())){
			vo.setRedirectUrl("/index");
		}
		// 如果已经登录
		if (ShiroUtil.isLogin()) {
			// 如果设置强制重新登录，则重新登录
			if (vo.getRelogin()) {
				SecurityUtils.getSubject().logout();
			}
			// 否则，直接跳转到目标页
			else {
				return "redirect:" + URLDecoder.decode(vo.getRedirectUrl(),"utf8");
			}
		}
		// 进行单点登录
		if (vo.getToken() != null) {

			SsoUsernamePasswordToken upt = new SsoUsernamePasswordToken();
			try {
				SysUserSdk user = authOpenFacade.findUserById(vo.getUserId());
				upt.setUsername(user.getUserName()); // 登录用户名
				upt.setPassword(vo.getToken().toCharArray()); // 密码组成：sso密钥+用户名+日期，进行md5加密
				upt.setAppCode(vo.getAppCode()); // 单点登录识别参数，see： AuthorizingRealm.assertCredentialsMatch
			} catch (Exception ex) {
				if (!ex.getMessage().startsWith("msg:")) {
					ex = new AuthenticationException("msg:授权令牌错误，请联系管理员。");
				}
				model.addAttribute("exception", ex);
			}
			try {
				SecurityUtils.getSubject().login(upt);
				return "redirect:" + URLDecoder.decode(vo.getRedirectUrl(),"utf8");
			} catch (AuthenticationException ae) {
				if (!ae.getMessage().startsWith("msg:")) {
					ae = new AuthenticationException("msg:授权错误，请检查用户配置，若不能解决，请联系管理员。");
				}
				model.addAttribute("exception", ae);
			}
		}
		return "login";
	}

	/**
	 *
	 * 不确定上面这个方法是否有用，固重新开一个方法
	 *
	 * @param vo
	 * @param model
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "resSsoLogin",method=RequestMethod.GET)
	public String ssoLogin(ResSsoLoginRequestVo vo, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info(" ===resSsoLogin=== {}", vo.toString());
		if(StringUtils.isBlank(vo.getRedirectUrl())){
			vo.setRedirectUrl("/index");
		}
		// 如果已经登录
		if (ShiroUtil.isLogin()) {
			return "redirect:" + URLDecoder.decode(vo.getRedirectUrl(),"utf8");
		}
		// 进行单点登录
		if (StringUtils.isNotBlank(vo.getAccessToken())) {
			SsoUsernamePasswordToken upt = new SsoUsernamePasswordToken();
			try {
				UserLoginVo searchVo = new UserLoginVo();
				searchVo.setLoginName(vo.getUserId());
				SysUserSdk user = authOpenFacade.findUserByLoginName(searchVo);
				if (Objects.isNull(user)) {
					throw new AuthenticationException("msg:单点登录失败，请联系管理员。");
				}
				upt.setPassword(vo.getAccessToken().toCharArray());
				upt.setUsername(user.getUserName()); // 登录用户名
			} catch (Exception ex) {
				if (!ex.getMessage().startsWith("msg:")) {
					ex = new AuthenticationException("msg:授权令牌错误，请联系管理员。");
				}
				model.addAttribute("exception", ex);
				return "login";
			}
			try {
				SecurityUtils.getSubject().login(upt);
				return "redirect:" + URLDecoder.decode(vo.getRedirectUrl(),"utf8");
			} catch (AuthenticationException ae) {
				if (!ae.getMessage().startsWith("msg:")) {
					ae = new AuthenticationException("msg:授权错误，请检查用户配置，若不能解决，请联系管理员。");
				}
				model.addAttribute("exception", ae);
			}
		}
		return "login";
	}
}
