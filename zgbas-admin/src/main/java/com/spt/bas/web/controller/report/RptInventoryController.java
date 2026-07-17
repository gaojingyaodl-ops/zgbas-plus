package com.spt.bas.web.controller.report;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBasContractClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;

/**
 * 库存查询
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/rpt/inventory")
public class RptInventoryController extends PageController<BasContract,BaseVo>{
	@Autowired
	private IBasContractClient contractClient;
	@Autowired
	private IBsCompanyClient companyClient;
	
	@Override
	public BaseClient<BasContract> getService() {
		return contractClient;
	}
	
	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("inventoryTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_INVENTORYTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		List<BsCompany> lstCompany=	companyClient.findAll();
		model.addAttribute("companyJson",JsonUtil.obj2Json(lstCompany));
		return "report/inventory";
	}
	
	@RequestMapping(value = "findInventoryList")
	public void findInventoryList(PageSearchVo queryVo ,HttpServletRequest request, HttpServletResponse response){
		initSearch(queryVo, request);
		Map<String, Object> searm = queryVo.getSearchParams();
		if(searm.containsKey("inventoryType")){
			String inventoryType = (String) searm.get("inventoryType");
			if(inventoryType.equals("N")){
				//未销售
				searm.put("GTM_remainNumber", 0);
			}else if(inventoryType.equals("D")){
				//已销售
				searm.put("EQM_remainNumber", 0);
			}
			searm.remove("inventoryType");	
			queryVo.setSearchParams(searm);
		}
		//已签订采购合同，且已收到货物的，形成库存；
		searm.put("EQS_status", BasConstants.APPROVE_STATUS_D);
		searm.put("EQS_contractType", BasConstants.CONTRACTTYPE_BUY);
		searm.put("EQB_payFlg", true);// 已收货
		PageDown<BasContract> page= contractClient.findPage(queryVo);
		
		JsonEasyUI.renderJson(response, page);
	}
	
}
