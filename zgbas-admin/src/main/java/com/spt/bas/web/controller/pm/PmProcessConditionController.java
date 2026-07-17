package com.spt.bas.web.controller.pm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.spt.bas.client.remote.IPmProcessConditionClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcessCondition;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
@Controller
@RequestMapping(value = "/pm/processCondition")
public class PmProcessConditionController extends SingleCrudControll<PmProcessCondition, BaseVo> {
	@Autowired
	private IPmProcessConditionClient processConditionClient;
	@Override
	public BaseClient<PmProcessCondition> getService() {
		// TODO Auto-generated method stub
		return processConditionClient;
	}
	

	@Override
	protected void preInsert(PmProcessCondition e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
