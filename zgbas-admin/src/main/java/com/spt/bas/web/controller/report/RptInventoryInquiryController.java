package com.spt.bas.web.controller.report;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;


@Controller
@RequestMapping(value = "/rpt/invoiceInquiry")
public class RptInventoryInquiryController extends PageController<BasContract,BaseVo>{
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
		model.addAttribute("approveStatusJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
		model.addAttribute("contractTypeJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTTYPE)));
		model.addAttribute("contractStatusJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACTSTATUS)));
		List<BsCompany> lstCompany=	companyClient.findAll();
		model.addAttribute("companyJson",JsonUtil.obj2Json(lstCompany));
		return "report/invoiceInquiry";
	}
	
	
	@RequestMapping(value = "findByStatus")
	public void findByStatus(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Map<String, Object> searchParams = searchVo.getSearchParams();
		searchParams.put("EQS_status", BasConstants.APPROVE_STATUS_D);
		searchParams.put("INS_contractStatus", new String[] { BasConstants.CONTRACTSTATUS_F1,
				BasConstants.CONTRACTSTATUS_F2, BasConstants.CONTRACTSTATUS_G1, BasConstants.CONTRACTSTATUS_G2 });
		searchVo.setSearchParams(searchParams);
		
		Page<BasContract> page = getService().findPage(searchVo);
		Map<String, Object> footer = null;
		
		JsonEasyUI.renderJson(response, page, footer);
		
		
	}
	

}
