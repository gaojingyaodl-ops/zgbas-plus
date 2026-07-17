package com.spt.bas.web.controller.bs;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spt.bas.client.remote.IBsProductTypeAccessClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.vo.BsProductTypeAccessSaveVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.web.util.RenderUtil;

/**
 * 商品权限配置
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/bs/productTypeAccess")
public class BsProductTypeAccessController {
	@Autowired
	private IBsProductTypeAccessClient productTypeAccessClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@RequestMapping(value = "")
	public String index(Model model) {
//		List<BsProductTypeAccess> lstAccess = productTypeAccessClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		List<EasyTreeNode> productTree = productTypeClient.findAllProductTreeSelect(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		return "bs/productTypeAccess";
	}
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(BsProductTypeAccessSaveVo vo,HttpServletRequest request, HttpServletResponse response) {
		try{
			vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
			productTypeAccessClient.saveAccess(vo);
			RenderUtil.renderSuccess("success", response);
		}catch(Exception e){
			logger.error("saveAccess",e);
			RenderUtil.renderFailure("fail", response);
		}
		
	}
	@RequestMapping(value = "push2Hq", method = RequestMethod.POST)
	public void push2Hq(BsProductTypeAccessSaveVo vo,HttpServletRequest request, HttpServletResponse response) {
		String msg = productTypeClient.push2Hq(ShiroUtil.getEnterpriseId());
		if(StringUtils.equals("success",msg)){
			RenderUtil.renderSuccess(msg, response);
		} else {
			RenderUtil.renderFailure(msg, response);
		}
	}

	@RequestMapping(value = "reFresh", method = RequestMethod.GET)
	public void reFresh(HttpServletResponse response) {
		try{
			productTypeAccessClient.reFreshCache();
			RenderUtil.renderSuccess("success", response);
		}catch(Exception e){
			logger.error("saveAccess",e);
			RenderUtil.renderFailure("fail", response);
		}
	}

}
