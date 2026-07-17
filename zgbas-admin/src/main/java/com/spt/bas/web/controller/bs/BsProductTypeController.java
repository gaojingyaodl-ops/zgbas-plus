package com.spt.bas.web.controller.bs;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spt.bas.client.entity.BsProductType;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;

@Controller
@RequestMapping(value = "/bs/productType")
public class BsProductTypeController extends PageController<BsProductType, BaseVo> {
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Override
	public BaseClient<BsProductType> getService() {
		// TODO Auto-generated method stub
		return productTypeClient;
	}
	
	@RequestMapping(value = "tree", method = RequestMethod.GET)
	public void tree(HttpServletResponse response) throws JSONException{
		//获取品名树
		 List<EasyTreeNode> productTree = productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		RenderUtil.renderSuccess(JsonUtil.obj2Json(productTree), response);
	}
	
	

}
