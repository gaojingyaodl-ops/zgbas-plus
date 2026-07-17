package com.spt.bas.web.controller.apply;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.remote.IApplyProductDetailClient;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;

@Controller
@RequestMapping(value = "/apply/productDetail")
public class ApplyProductDetailController extends PageController<ApplyProductDetail, BaseVo>{
	
	@Autowired
	private IApplyProductDetailClient productDetailClient;

	@Override
	public BaseClient<ApplyProductDetail> getService() {
		return productDetailClient;
	}
	
	@RequestMapping(value = "productList")
	public void productList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<ApplyProductDetail> page = productDetailClient.findPage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}	
	

}
