package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
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

@Controller
@RequestMapping(value = "/apply/presell")
public class ApplyPresellController extends PageController<ApplyPresell, BaseVo>{
	
	@Autowired
	private IApplyPresellClient applyPresellClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBasBrandClient brandClient;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Autowired
	private IBsCompanyClient bsCompanyClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplyPresell> getService() {
		return applyPresellClient;
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id,PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
		ApplyPresell entity = getEntity(id);
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		// 销售方式
		model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
				BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));	
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
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 质量标准
		model.addAttribute("qualityStandardJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
		// 交货时间的补充字段
		model.addAttribute("attachDeliveryTimeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
		// 预售-业务类型
		model.addAttribute("businessJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PRESELL_BUSINESS)));
		model.addAttribute("onLineFlg",getOnLineFlg(entity));
		// 定金比例
		model.addAttribute("contractReceiveRateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
				
		model.addAttribute("psv", permissionVo);
		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		String businessType = entity.getBusinessType();
		model.addAttribute("onLineFlg",getOnLineFlg(entity));
		// 付款方式
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_SX, businessType)) {
			return "apply/preSx-sell-content";
		}else if (StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_HK, businessType)) {
			return "apply/preHk-sell-content";
		}
		return "apply/presell-content";
	}
	
	/**
	 * 附件编辑修改
	 * @param vo
	 * @param response
	 */
	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo,
			HttpServletResponse response) {
		try {
			applyPresellClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	
	@ModelAttribute("preload")
	public ApplyPresell getEntity(@RequestParam(value = "id", required = false) Long id) {
//		if (id != null) {
//			if (id > 0)
//				return getService().getEntity(id);
//			else {
//				ApplyPresell entity = new ApplyPresell();
//				entity.setId(0l);
//				entity.setStatus(BasConstants.APPROVE_STATUS_N);
//				entity.setReceiveTime(new Date());
//				entity.setReceiveBondTime(new Date());
//				entity.setDeliveryTime(new Date());
//				return entity;
//			}
//		}
//		return null;
		ApplyPresell entity = new ApplyPresell();
		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		entity.setReceiveBondTime(new Date());
		entity.setReceiveTime(new Date());
		entity.setDeliveryTime(new Date());
		entity.setId(0L);
		if (id != null) {
			if(id == 0L) {
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_XS);
			}else if(id == 1L) {
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_SX_SX);
			}else if(id == 2L){
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_SX_HK);
			}else {
				entity = getService().getEntity(id);
			}
		}
		return entity;
	}

	private Boolean getOnLineFlg(ApplyPresell sell) {
    	Boolean onLineFLg = false;
    	Long companyId = sell.getCompanyId();
    	if (companyId != null) {
    		BsCompany bsCompany = bsCompanyClient.getEntity(companyId);
    		onLineFLg = bsCompany.getOnLineFlg();
    	}
    	return onLineFLg;
    }
}
