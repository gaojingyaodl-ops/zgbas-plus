package com.spt.bas.web.controller.pm;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.spt.bas.client.remote.IPmProcessAccessClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/pm/processAccess")
public class PmProcessAccessController extends SingleCrudControll<PmProcessAccess, BaseVo>{
	@Autowired
	private IPmProcessAccessClient processAccessClient;
	@Autowired
	private IPmProcessClient processClient;
	@Resource
	private WebParamUtils webParamUtils;

	@Override
	public BaseClient<PmProcessAccess> getService() {
		return processAccessClient;
	}

	@RequestMapping(value = "")
	public String index(Model model){
		return "pm/processAccess";
	}

	@RequestMapping(value = "byUser")
	public String byUser(Model model){
		return "pm/accessByUser";
	}

	@RequestMapping(value = "byProcess")
	public String byProcess(Model model){
		return "pm/accessByProcess";
	}

	@RequestMapping(value = "initUserTree")
	public void initUserTree(@RequestParam(value = "processId", required = false) Long processId, HttpServletResponse response){
		//获取部门负责人数据
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		List<EasyTreeNode> cList = nodes.getChildren();
		if(processId!=null){
			List<PmProcessAccess> accessList = processAccessClient.findByProcessId(processId);
			Set<Long> userIdset = new HashSet<>();
			for(PmProcessAccess acc:accessList) {
				userIdset.add(acc.getUserId());
			}
			if (!accessList.isEmpty()) {
				initCheckedNode(cList, userIdset);
			}
		}
		RenderUtil.renderJson(cList, response);
	}

	@RequestMapping(value = "initProcessTree")
	public void initProcessTree(@RequestParam(value = "userId", required = false) Long userId, HttpServletResponse response){
		PmProcessSearchVo searchVo=new PmProcessSearchVo();
		searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		searchVo.setViewFlg(true);
		EasyTreeNode nodes = processClient.getAllProcess(searchVo);
		if(userId!=null){
			List<PmProcessAccess> accessList = processAccessClient.findByUserId(userId);
			if (!accessList.isEmpty()) {
				getCheckedProcess(nodes, accessList);
			}
		}
		List<EasyTreeNode> list = new ArrayList<EasyTreeNode>();
		list.add(nodes);
		RenderUtil.renderJson(list, response);
	}

	@RequestMapping(value = "saveByProcess")
	public void saveByProcess(HttpServletRequest request,HttpServletResponse response){
		String userStr = request.getParameter("userStr");
		List<PmProcessAccess> list = JSON.parseArray(userStr, PmProcessAccess.class);
		for (PmProcessAccess pmProcessAccess : list) {
			pmProcessAccess.setEnterpriseId(ShiroUtil.getEnterpriseId());
		}
		processAccessClient.saveChanges(list);
		RenderUtil.renderSuccess("success", response);
	}

	@PostMapping(value = "saveByUser")
	public void saveByUser(PmProcessAccessVo vo ,HttpServletRequest request,HttpServletResponse response){
		vo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		processAccessClient.saveByUser(vo);
		RenderUtil.renderSuccess("success", response);
	}

	private List<EasyTreeNode> initCheckedNode(List<EasyTreeNode> clist , Set<Long>  userIdset){
		if(clist.size()>0){
			for (EasyTreeNode dNode : clist) {
				List<EasyTreeNode> dlist = dNode.getChildren();
				if(dlist.size()>0){
					this.initCheckedNode(dlist,userIdset);
				}else{
					if(dNode.getId().indexOf("user")>=0){
						Long userId = Long.parseLong(dNode.getId().substring(4));
						if (userIdset.contains(userId)) {
							dNode.setChecked(true);
						}
					}
				}
			}
		}
		return clist;
	}

	private EasyTreeNode getCheckedProcess(EasyTreeNode nodes,List<PmProcessAccess> accessList){
		for (EasyTreeNode node : nodes.getChildren()) {
			boolean isContains = CollectionUtil.contain(accessList, "processId", Long.valueOf(node.getId()));
			if(isContains){
				node.setChecked(true);
			}
		}
		return nodes;
	}

	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	@Override
	protected void preInsert(PmProcessAccess e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
