package com.spt.bas.web.controller.pm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.spt.bas.client.remote.IPmApplySetClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.remote.IPmProcessStepClient;
import com.spt.bas.client.vo.api.RespVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApplySet;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;

/**
 * 流程表单配置信息
 * @author zhangyanping
 *
 */
@Controller
@RequestMapping(value = "/pm/applySet")
public class PmApplySetController extends SingleCrudControll<PmApplySet, BaseVo>{
	@Autowired
	private IPmApplySetClient pmApplySetClient;
	@Autowired
	private IPmProcessClient processClient;
	@Autowired
	private IPmProcessStepClient ProcessStepClient;

	@Override
	public BaseClient<PmApplySet> getService() {
		// TODO Auto-generated method stub
		return pmApplySetClient;
	}


	@RequestMapping(value = "")
	public String index(Model model) {
		Long enterpriseId = ShiroUtil.getEnterpriseId();
		List<PmProcess> list = processClient.findByEnterpriseIdAndEnableFlgTrue(enterpriseId);
		model.addAttribute("processJson", JsonUtil.obj2Json(list));
		List<PmProcessStep> steplist = ProcessStepClient.findEnable();
		model.addAttribute("stepListJson", JsonUtil.obj2Json(steplist));
		return "pm/pmApplySet";
	}

	@RequestMapping(value = "/applylist",method=RequestMethod.POST)
	@ResponseBody
	public void applylist(PageSearchVo queryVo,HttpServletRequest request,HttpServletResponse response){
		initSearch(queryVo, request);
		PageDown<PmApplySet> page=  pmApplySetClient.findPage(queryVo);
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value="findStep",method=RequestMethod.POST)
	public void findStep(Long processId,HttpServletResponse response){
		List<PmProcessStep> list = new ArrayList<PmProcessStep>();
		if(processId!=null){
			PageSearchVo queryVo = new PageSearchVo();
			queryVo.setRows(50);
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQL_processId", processId);
			queryVo.setSearchParams(searchParams);
			PageDown<PmProcessStep> page = ProcessStepClient.findPage(queryVo);
			list = page.getContent();
		}
		RenderUtil.renderJson(list,response);
	}


	@RequestMapping(value = "/getaApplylist",method=RequestMethod.POST)
	@ResponseBody
	public PageDown<PmApplySet> getaApplylist(PageSearchVo queryVo,HttpServletRequest request,HttpServletResponse response){
		initSearch(queryVo, request);
		PageDown<PmApplySet> page=  pmApplySetClient.findPage(queryVo);
		return page;
	}

	@RequestMapping(value = "findByParm",method=RequestMethod.POST)
	@ResponseBody
	public RespVo<?> findByParm(@RequestParam("processId") Long processId,@RequestParam("stepId") Long stepId,
			@RequestParam("fieldName") String fieldName,HttpServletRequest request,HttpServletResponse response){
		RespVo<?> resp = new RespVo<>();
		PageSearchVo queryVo = new PageSearchVo();
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQL_processId", processId);
		searchParams.put("EQL_stepId", stepId);
		searchParams.put("EQS_fieldName", fieldName);
		searchParams.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		queryVo.setSearchParams(searchParams);
		PageDown<PmApplySet> page = pmApplySetClient.findPage(queryVo);
		if(page.getContent().size()>0){
			resp.setMessage("fail");
		}else{
			resp.setMessage("success");
		}
		return resp;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(PmApplySet e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
