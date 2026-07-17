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
 * 资金查询
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/rpt/bank")
public class RptBankController extends PageController<BasContract, BaseVo>{

	@Autowired
	private IBasContractClient contractClient;
	
	@Autowired
	private IBsCompanyClient companyClient;
	
	@Override
	public BaseClient<BasContract> getService() {
		return contractClient;
	}

	@RequestMapping("")
	public String index(Model model){
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		
		model.addAttribute("bankJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.BANK_TYPE)));
		
		List<BsCompany> lstCompany=	companyClient.findAll();
		model.addAttribute("companyJson",JsonUtil.obj2Json(lstCompany));
		
		return "report/bank";
	}
	
	//获取资金列表
	@RequestMapping("/findBnkList")
	public void findBnkList(PageSearchVo queryVo,HttpServletRequest request,HttpServletResponse response){
		initSearch(queryVo, request);
		Map<String,Object> searm=queryVo.getSearchParams();
		if(searm.containsKey("bankType")){
			if(searm.get("bankType").equals("YS")){
				//应收
				searm.put("EQS_contractType", BasConstants.CONTRACTTYPE_SELL);
			}else if(searm.get("bankType").equals("YF")){
				//应付
				searm.put("EQS_contractType", BasConstants.CONTRACTTYPE_BUY);
			}
			searm.remove("bankType");	
			queryVo.setSearchParams(searm);
		}
		searm.put("EQS_status", BasConstants.APPROVE_STATUS_D);
		searm.put("EQB_fondFlg", false);
		PageDown<BasContract> page=  contractClient.findPage(queryVo);
		BasContract e = sumPage(request, response);
		Map<String, Object> footer = null;
		if (e != null) {
			footer = entity2Footer(e);
		}
		
		JsonEasyUI.renderJson(response, page,footer);
	}
	
	
}
