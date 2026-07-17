package com.spt.bas.web.controller.pm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.spt.bas.client.remote.IPmProcessStepClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
@Controller
@RequestMapping(value = "/pm/processStep")
public class PmProcessStepController extends SingleCrudControll<PmProcessStep, BaseVo> {
	@Autowired
	private IPmProcessStepClient pmProcessStepClient;
	@Override
	public BaseClient<PmProcessStep> getService() {
		// TODO Auto-generated method stub
		return pmProcessStepClient;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@Override
 	protected void preInsert(PmProcessStep e) {
 		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
 	}
}
