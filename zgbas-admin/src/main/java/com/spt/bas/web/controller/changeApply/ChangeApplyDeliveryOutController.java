package com.spt.bas.web.controller.changeApply;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/changeApply/deliveryOut")
public class ChangeApplyDeliveryOutController extends PageController<ApplyDeliveryOutAdjust, BaseVo>{

	@Autowired
	private IApplyDeliveryOutAdjustClient applyDeliveryOutAdjustClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Autowired
	private IStockDetailClient stockDetailClient; 
	@Autowired
	private IApplyDeliveryInClient applyDeliveryInClient;
	
	@Override
	public BaseClient<ApplyDeliveryOutAdjust> getService() {
		return applyDeliveryOutAdjustClient;
	}

	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model) {
		ApplyDeliveryOutAdjust entity = getEntity(id);
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		//产品类型
		List<SysDictDataSdk> list = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT);
		List<SysDictDataSdk> attrList = new ArrayList<SysDictDataSdk>();
		for(SysDictDataSdk dict:list){
			if(BasConstants.STOCK_PRODUCT_ATTR_N.equals(dict.getDictCd())){
				attrList.add(dict);
				break;
			}
		}
		model.addAttribute("productType",
				JsonUtil.obj2Json(attrList));
		model.addAttribute("entity", entity);
		model.addAttribute("productJson",
				JsonUtil.obj2Json(productTypeClient.findAll()));
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		model.addAttribute("entity", entity);
		List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
		//交货方式
		model.addAttribute("deliveryMode",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		return "changeApply/deliveryOut-content";
	}
	
	@ModelAttribute("preload")
	public ApplyDeliveryOutAdjust getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0)
				return getService().getEntity(id);
			else {
				ApplyDeliveryOutAdjust entity = new ApplyDeliveryOutAdjust();
				entity.setId(0l);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
	
	/*@RequestMapping(value = "updateFileId", method = RequestMethod.POST)
	public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
		try {
			applyDeliveryOutClient.updateFileId(vo);
			RenderUtil.renderSuccess("success", response);
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}*/
	
	@RequestMapping(value = "findWarehouseName", method = RequestMethod.POST)
	public void findWarehouse(String warehouseName,HttpServletResponse response) {
		StockDetail stockDetail = stockDetailClient.findWarehouseName(warehouseName);
		RenderUtil.renderJson(stockDetail,response);
	}
	
	@RequestMapping(value = "findDeliveryInContractId", method = RequestMethod.POST)
	public void findDeliveryInContractId(Long buyContractId,HttpServletResponse response) {
		List<ApplyDeliveryIn> applyDeliveryInList = applyDeliveryInClient.findDeliveryInContractId(buyContractId);
		if(applyDeliveryInList!=null&&applyDeliveryInList.size()>0){
			RenderUtil.renderJson(applyDeliveryInList.get(0),response);
		}else{
			RenderUtil.renderFailure("fail", response);
		}
	}
	
	@RequestMapping(value="findApplyDeliveryOut", method = RequestMethod.GET)
	public String findDeliveryOutByStatus(Model model){
		model.addAttribute("statusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("deliveryModeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_DELIVERYMODE)));
		model.addAttribute("deliveryTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		return "changeApply/deliveryOut-choose";
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
}
