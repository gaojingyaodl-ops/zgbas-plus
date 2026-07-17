package com.spt.bas.web.controller.apply;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.remote.IApplyDeliveryClient;
import com.spt.bas.client.remote.IBasBrandClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;


/**
 * 提货申请
 * @author zhangyanping
 *
 */
@Controller
@RequestMapping(value = "/apply/delivery")
public class ApplyDeliveryController  extends PageController<ApplyDelivery, BaseVo>{

	
	
	@Autowired
	private IApplyDeliveryClient applyDeliveryClient;
	@Autowired
	private IBsCompanyClient companyClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBasBrandClient brandClient;
	
	
	@Override
	public BaseClient<ApplyDelivery> getService() {
		// TODO Auto-generated method stub
		return applyDeliveryClient;
	}
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model) {
		ApplyDelivery entity = getEntity(id);
		model.addAttribute("entity",entity);
		
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		List<BsCompany> lstCompany = companyClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("companyJson", JsonUtil.obj2Json(lstCompany));
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		//获取品名树
		model.addAttribute("productDeliveryJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		List<BasBrand> lstBrand = brandClient.findAll();
		model.addAttribute("productJson",
				JsonUtil.obj2Json(productTypeClient.findAll()));
		model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
		return "apply/delivery-content";
	}
	
	@ModelAttribute("preload")
	public ApplyDelivery getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				ApplyDelivery entity = new ApplyDelivery();
				entity.setId(0l);
//				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
}
