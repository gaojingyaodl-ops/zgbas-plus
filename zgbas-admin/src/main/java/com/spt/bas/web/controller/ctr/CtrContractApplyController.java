package com.spt.bas.web.controller.ctr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.remote.ICtrContractApplyClient;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;

@Controller
@RequestMapping(value = "/ctr/contractApply")
public class CtrContractApplyController extends PageController<CtrContractApply, BaseVo> {

	@Autowired
	private ICtrContractApplyClient  ctrContractApplyClient;
	@Override
	public BaseClient<CtrContractApply> getService() {
		return ctrContractApplyClient;
	}
	
	@RequestMapping(value ="/findByContractId", method = RequestMethod.GET)
	public void findByContractId(HttpServletRequest request,HttpServletResponse response){
		String contractId = request.getParameter("contractId");
		CtrContractApply ctrApply=ctrContractApplyClient.findByContractId(Long.parseLong(contractId));
		RenderUtil.renderJson(ctrApply, response);
	}

}
