package com.spt.bas.web.controller.dataScreen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.IpUtils;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ServletUtils;
import com.spt.tools.core.bean.ShiroUser;
import com.spt.tools.core.encrypt.Md5Encrypt;
import com.spt.tools.http.util.HTTPUtility;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping(value = "/data/screen")
public class DataScreenController {
	@Value("${basData.md5.secret.key}")
	private String basDataMd5SecretKey;
	@Value("${basData.server.url}")
	private String basDataLoginUrl;

	@RequestMapping(value = "yjBigScreen",method = RequestMethod.GET)
	public String yjBigScreen(Long id,Model model) {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
		LocalDate now = LocalDate.now();
		model.addAttribute("nowTargetMonth", now.format(pattern));
		model.addAttribute("basDataServerUrl", getBasDataServerUrl(BasConstants.YJ_BIG_SCREEN_PATH));
		model.addAttribute("basDataServerPhoneUrl", getBasDataServerUrl(BasConstants.YJ_BIG_SCREEN_PHONE_PATH));
		return "dataScreen/yjBigScreen";
	}
	
	@RequestMapping(value = "mlrBigScreen",method = RequestMethod.GET)
	public String mlrBigScreen(Long id,Model model) {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
		LocalDate now = LocalDate.now();
		model.addAttribute("nowTargetMonth", now.format(pattern));
		model.addAttribute("basDataServerUrl", getBasDataServerUrl(BasConstants.MLR_BIG_SCREEN_PATH));
		model.addAttribute("basDataRangeServerUrl", getBasDataServerUrl(BasConstants.MLR_RANGE_BIG_SCREEN_PATH));
		model.addAttribute("basDataServerPhoneUrl", getBasDataServerUrl(BasConstants.MLR_BIG_SCREEN_PHONE_PATH));
		model.addAttribute("basDataRangePhoneUrl", getBasDataServerUrl(BasConstants.MLR_RANGE_BIG_SCREEN_PHONE_PATH));
		return "dataScreen/mlrBigScreen";
	}
	
	@RequestMapping(value = "outBigScreen",method = RequestMethod.GET)
	public String outBigScreen(Long id,Model model) {
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM");
		LocalDate now = LocalDate.now();
		model.addAttribute("nowTargetMonth", now.format(pattern));
		model.addAttribute("basDataServerUrl", getBasDataServerUrl(BasConstants.OUT_BIG_SCREEN_PATH));
		model.addAttribute("basDataServerPhoneUrl", getBasDataServerUrl(BasConstants.OUT_BIG_SCREEN_PHONE_PATH));
		return "dataScreen/outBigScreen";
	}
	public String getBasDataServerUrl(String path){
		ShiroUser shiroUser = ShiroUtil.getShiroUser();
		long timestamp = System.currentTimeMillis();
		String md5Ticket = Md5Encrypt.encrypt(shiroUser.loginName + timestamp + basDataMd5SecretKey).toLowerCase();
		StringBuilder sbr = new StringBuilder(basDataLoginUrl+path);
		sbr.append("&loginName=").append(shiroUser.loginName);
		sbr.append("&timestamp=").append(timestamp);
		sbr.append("&ticket=").append(md5Ticket);
		return sbr.toString();
	}
	

}
