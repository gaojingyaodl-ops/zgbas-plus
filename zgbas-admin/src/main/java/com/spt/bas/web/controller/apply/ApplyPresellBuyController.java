package com.spt.bas.web.controller.apply;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.BsCompanyVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/apply/presell/buy")
public class ApplyPresellBuyController extends PageController<ApplyBuy, BaseVo> {
	@Autowired
	private IApplyBuyClient buyClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBasBrandClient brandClient;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Autowired
	private IBsCompanyAccountClient bsCompanyAccountClient;
	@Resource
	private WebParamUtils webParamUtils;
	
	@Override
	public BaseClient<ApplyBuy> getService() {
		return buyClient;
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyBuy entity = getEntity(id);
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		// 结算方式
		model.addAttribute("deliveryModeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_DELIVERYMODE)));
		model.addAttribute("entity", entity);
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		//获取品名树
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(productTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId())));
		List<BasBrand> lstBrand = brandClient.findAll();
		model.addAttribute("brandJson", JsonUtil.obj2Json(lstBrand));
		List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
		model.addAttribute("ourCompanyJson",
				JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 交货时间的补充字段
		model.addAttribute("arrivalTimeExtJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
		// 质量标准
		model.addAttribute("qualityStandardJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
		// 开票时间
		model.addAttribute("invoiceDateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_INVOICEDATE)));
		// 包装规格
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
		// 定金比例
		model.addAttribute("contractBondRateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
				
		model.addAttribute("psv", permissionVo);
		String businessType = entity.getBusinessType();
		model.addAttribute("business", getBusiness(businessType));
		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		model.addAttribute("defaultFlg", getDefaultFlg(entity));
		return "apply/presellBuy-content";
	}
	
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			buyClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@RequestMapping(value = "print/{id}", method = RequestMethod.GET)
	public String  print(@PathVariable(value = "id") Long id,Model model){
		ApproveFormPrintVo vo = buyClient.printApplyBuy(id);
		model.addAttribute("printContext", vo.getContent());
		return "apply/applyBuyPrint";
	}
	@ModelAttribute("preload")
	public ApplyBuy getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyBuy entity = new ApplyBuy();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_CG);
				entity.setPayBondTime(new Date());
				entity.setPayFullTime(new Date());
				return entity;
			}
		}
		return null;
	}
	
	 private String getBusiness(String businessType) {
	    	String business = "";
	    	if (businessType != null) {
	    		String value = businessType.split("-")[0];
	    		business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, value);
	    	}
	    	return business;
	 }
	 private Boolean getDefaultFlg(ApplyBuy buy) {
	    	Boolean defaultFlg = false;
	    	if (buy != null && buy.getCompanyId() != null) {
	    		BsCompanyVo vo = new BsCompanyVo();
	    		vo.setId(buy.getCompanyId());
	    		vo.setEnterpriseId(buy.getEnterpriseId());
	    		BsCompanyAccount account = bsCompanyAccountClient.findDefaultAccount(vo);
	    		if (account != null && account.getBankAccount() != null) {
	    			String bankAccount = account.getBankAccount();
	    			String bankName = account.getBankName();
	    			if (StringUtils.equals(bankName, buy.getReceiveBank()) && StringUtils.equals(bankAccount, buy.getReceiveAccount())) {
	    				defaultFlg = true;
	    			}
	    		}
	    	}else {
	    		defaultFlg = true;
	    	}
	    	return defaultFlg;
	    }
}
