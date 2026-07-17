package com.spt.bas.web.controller.ctr;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.CtrContractSchedule;
import com.spt.bas.client.remote.ICtrContractScheduleClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 风控待办事项
 */
 @Controller
 @RequestMapping(value = "/ctrContract/schedule")
public class CtrContractScheduleController extends SingleCrudControll<CtrContractSchedule, BaseVo>{
	@Autowired
	private ICtrContractScheduleClient ctrContractScheduleClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;

	@Override
	public BaseClient<CtrContractSchedule> getService() {
		return ctrContractScheduleClient;
	}

	@RequestMapping(value = "")
	public String index(Model model,HttpServletRequest request) {
		DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
		model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
		// 待办状态
		model.addAttribute("scheduleStatusJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_SCHEDULE_STATUS)));
		return "ctr/schedule";
	}

	@RequestMapping(value = "findSchedulePage")
	public void findSchedulePage(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
		initSearch(searchVo, request);
		Page<CtrContractSchedule> page = ctrContractScheduleClient.findSchedulePage(searchVo);
		JsonEasyUI.renderJson(response, page);
	}

	@RequestMapping(value = "toDo", method = RequestMethod.POST)
	public void toDo(@RequestParam(value="id") Long id,HttpServletResponse response) {
		try {
			if (id != null && id != 0L) {
				CtrContractSchedule entity = ctrContractScheduleClient.getEntity(id);
				entity.setStatus(BasConstants.SCHEDULE_STATUS_D);
				entity.setDisposeUserName(ShiroUtil.getCurrentUserName());
				ctrContractScheduleClient.save(entity);
				RenderUtil.renderSuccess("success", response);
			}
		} catch (Exception e) {
			logger.error("errorId:", e);
			RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
		}
	}


	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}

	@Override
	protected void preInsert(CtrContractSchedule e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}


}
