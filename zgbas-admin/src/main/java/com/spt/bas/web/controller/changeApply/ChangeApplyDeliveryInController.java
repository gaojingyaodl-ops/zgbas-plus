package com.spt.bas.web.controller.changeApply;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyDeliveryInAdjust;
import com.spt.bas.client.entity.BsFactory;
import com.spt.bas.client.entity.BsWarehouse;
import com.spt.bas.client.remote.IApplyDeliveryInAdjustClient;
import com.spt.bas.client.remote.IBsFactoryClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IBsWarehouseClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/changeApply/deliveryIn")
public class ChangeApplyDeliveryInController extends PageController<ApplyDeliveryInAdjust, BaseVo>{
	@Autowired
	private IApplyDeliveryInAdjustClient applyDeliveryInAdjustClient;
	@Autowired
	private IBsProductTypeClient productTypeClient;
	@Autowired
	private IBsFactoryClient factoryClient;
	@Autowired
	private IBsWarehouseClient warehouseClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<ApplyDeliveryInAdjust> getService() {
		return applyDeliveryInAdjustClient;
	}
	
	@RequestMapping(value = "content/{id}", method = RequestMethod.GET)
	public String content(@PathVariable("id") Long id, Model model,PmPermissionVo permissionVo) {
		ApplyDeliveryInAdjust entity = getEntity(id);
		model.addAttribute("productTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT)));
		model.addAttribute("payTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
		model.addAttribute("deliveryTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		List<BsWarehouse> warehouseList = warehouseClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("warehouseJson", JsonUtil.obj2Json(warehouseList));
		model.addAttribute("entity", entity);
		model.addAttribute("productJson",
				JsonUtil.obj2Json(productTypeClient.findAll()));
		List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
		model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));
		//处理审批中部分控件可编辑
		permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
						
		model.addAttribute("psv", permissionVo);
		return "changeApply/deliveryIn-content";
	}
	
	@ModelAttribute("preload")
	public ApplyDeliveryInAdjust getEntity(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			if (id > 0) {
				return getService().getEntity(id);
			} else {
				ApplyDeliveryInAdjust entity = new ApplyDeliveryInAdjust();
				entity.setId(0L);
				entity.setStatus(BasConstants.APPROVE_STATUS_N);
				return entity;
			}
		}
		return null;
	}
	
	@RequestMapping(value="findApplyDeliveryIn", method = RequestMethod.GET)
	public String findDeliveryOutByStatus(Model model){
		model.addAttribute("statusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("deliveryTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
		return "changeApply/deliveryIn-choose";
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

}
