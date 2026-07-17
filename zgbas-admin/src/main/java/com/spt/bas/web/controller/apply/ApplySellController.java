package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplySellPayModeVo;
import com.spt.bas.client.vo.ApproveFormPrintVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 申请-销售
 * 
 * @author
 *
 */
@Controller
@RequestMapping(value = "/apply/sell")
public class ApplySellController extends PageController<ApplySell, BaseVo> {

	@Autowired
	private IApplySellClient sellClient;
	@Autowired
	private IBsProductTypeClient bsProductTypeClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private ICtrContractClient ctrContractClient;
	@Autowired
	private ICtrProductClient productClient;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplySell> getService() {
		return sellClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model) {
		ApplySell entity = getEntity(id);
		if (entity.getId() == null) {
			entity.setId(0L);
		}
		String businessType = entity.getBusinessType();
		// 产品类型
		model.addAttribute("productType",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		// 收款方式
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("entity", entity);
		// 货品树
		List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
		model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
		model.addAttribute("productJson", JsonUtil.obj2Json(productTypeClient.findAll()));
		// 厂商
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		// 我方抬头
		model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
				BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
		// 质量标准
		model.addAttribute("qualityStandardJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
		// 交货方式
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		// 包装规格-全部
		model.addAttribute("packingSpecificaJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_IMPORTBUYPACKING)));
		// 定金比例
		model.addAttribute("contractReceiveRateJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
		//仓库
		List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
		String url = "";
		String attachDeliveryTimeJson = "";
		String deliveryMode = "";
		List<ApplySellPayModeVo> modeList = null;
		if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType)
				|| StringUtils.equals(BasConstants.BUSINESS_TYPE_SY_XS, businessType)) {
			url = "apply/sell-content";
			attachDeliveryTimeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME));
			deliveryMode = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_DELIVERYMODE));
		}else if(StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_SX, businessType)) {
			url = "apply/sell-sx-content";
			attachDeliveryTimeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYSX));
			deliveryMode = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SX_DELIVERYMODE));
		}else if(StringUtils.equals(BasConstants.BUSINESS_TYPE_SX_HK, businessType)) {
			url = "apply/sell-hk-content";
			attachDeliveryTimeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYSX));
			deliveryMode = JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_HK_DELIVERYMODE));
		}
		// 付款方式
		model.addAttribute("payModeJson",JsonUtil.obj2Json(modeList));
		// 交货时间的补充字段
		model.addAttribute("attachDeliveryTimeJson", attachDeliveryTimeJson);
		// 结算方式
		model.addAttribute("deliveryMode",deliveryMode);	
		model.addAttribute("psv", permissionVo);
		model.addAttribute("contractAttr",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.STOCK__CONTRACT_ATTR)));
		model.addAttribute("business",getBusiness(businessType));
		return url;
	}

	@RequestMapping(value = "print/{id}", method = RequestMethod.GET)
	public String print(@PathVariable(value = "id") Long id, Model model) {
		ApproveFormPrintVo printApplySell = sellClient.printApplySell(id);
		model.addAttribute("printContext", printApplySell.getContent());
		return "apply/applySellPrint";
	}

	@ModelAttribute("preload")
	public ApplySell getEntity(@RequestParam(value = "id", required = false) Long id) {
		ApplySell entity = new ApplySell();
		entity.setStatus(BasConstants.APPROVE_STATUS_N);
		entity.setReceiveBondTime(new Date());
		entity.setReceiveFullTime(new Date());
		entity.setDeliveryTime(new Date());
		entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_XS);
		if (id != null) {
			if(id == 0L) {
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_ZY_XS);
			}else if(id == 1L) {
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_SX_SX);
			}else if(id == 2L){
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_SX_HK);
			}else if(id == 3L){
				entity.setBusinessType(BasConstants.BUSINESS_TYPE_SY_XS);
			}else {
				entity = getService().getEntity(id);
			}
		}
		return entity;
	}

	@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			sellClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}
	/**查询最大可销售数量*/
//	@RequestMapping(value = "findDealNumber")
//	public void findDealNumber(@RequestParam("productCd") String productCode,
//			@RequestParam("brandNumber") String brandNumber, @RequestParam("factoryId") String factoryId,
//			@RequestParam("warehouseName") String warehouseName, @RequestParam("productAttr") String productAttr,
//			HttpServletResponse response) {
//		ApplySellWarehouseVo vo = new ApplySellWarehouseVo();
//		vo.setProductCode(productCode);
//		vo.setBrandNumber(brandNumber);
//		vo.setFactoryId(Long.parseLong(factoryId));
//		vo.setWarehouseName(warehouseName);
//		vo.setProductAttr(productAttr);
//		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		StockVo stockVo = stockClient.findDealNumber(vo);
//		BigDecimal maxNumber = stockVo.getRealNumber();
//		RenderUtil.renderJson(maxNumber, response);
//	}

	@PostMapping(value = "findByStockDetailId")
	@ResponseBody
	public Map<String, Object> findByStockDetailId(@RequestParam("stockDetailId") Long stockDetailId) {
		CtrContract contract = ctrContractClient.findByStockDetailId(stockDetailId);
		List<CtrProduct> productList = productClient.findByOutCtrContractId(contract.getId());
		Map<String, Object> map = new HashMap<>();
		map.put("contract", contract);
		map.put("productList", productList);
		return map;
	}
	
	@PostMapping(value = "findByContractId")
	@ResponseBody
	public Map<String, Object> findByContractId(@RequestParam("contractId") Long contractId){
		ApplySell applySell = sellClient.findByContractId(contractId);
		CtrContract contract = ctrContractClient.findByContractId(contractId);
		Map<String, Object> map = new HashMap<>();
		map.put("realOurCompanyName", applySell.getRealOurCompanyName());
		map.put("companyId", contract.getCompanyId());
		map.put("companyName", contract.getCompanyName());
		map.put("qualityStandard", contract.getQualityStandard());
		return map;
	}
    
	private String getBusiness(String businessType) {
    	String business = "";
    	if (businessType != null) {
    		String value = businessType.split("-")[0];
    		business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, value);
    	}
    	return business;
    }
    
//	private Boolean getOnLineFlg(ApplySell sell) {
//    	Boolean onLineFLg = false;
//    	Long companyId = sell.getCompanyId();
//    	if (companyId != null) {
//    		BsCompany bsCompany = bsCompanyClient.getEntity(companyId);
//    		onLineFLg = bsCompany.getOnLineFlg();
//    	}
//    	return onLineFLg;
//    }
}
