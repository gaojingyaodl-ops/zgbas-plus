/**
 * 
 */
package com.spt.bas.web.controller.pm;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IPmProcessNodeClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.vo.PmProcessNodeRefVo;
import com.spt.pm.vo.PmProcessNodeRespVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程节点
 * @author wlddh
 *
 */
@Controller
@RequestMapping(value = "/pm/processNode")
public class PmProcessNodeController extends SingleCrudControll<PmProcessNode, BaseVo> {

	@Autowired
	private IPmProcessNodeClient processNodeClient;
	@Resource
	private WebParamUtils webParamUtils;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	
	@Override
	public BaseClient<PmProcessNode> getService() {
		return processNodeClient;
	}

	@RequestMapping(value = "")
	public String index(Model model) {
		model.addAttribute("enableFlgs",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		model.addAttribute("nodeTypeJson",
				JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_NODETYPE)));
		//获取部门负责人数据
		EasyTreeNode nodes = webParamUtils.getDeptEasyTreeNode(true);
		model.addAttribute("deptTree",JsonUtil.obj2Json(nodes.getChildren()));
		DeptSearchVo deptSearchVo = new DeptSearchVo();
		deptSearchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
		EasyTreeNode treeNodes = EasyTreeUtil2.getDeptTree(deptList, true, true);
		model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(treeNodes.getChildren()));
		return "pm/processNode";
	}
	@RequestMapping(value = "/nodeList")
	@ResponseBody
	public List<PmProcessNode> nodeList(HttpServletRequest request, HttpServletResponse response){
		//流程节点
		PageSearchVo searchVo = new PageSearchVo();
		searchVo.setRows(1000);
		Map<String,Object> searchParams = new HashMap<String,Object>();
		searchParams.put("EQB_enableFlg", true);
		searchVo.setSearchParams(searchParams);
		Page<PmProcessNode> nodePage = this.findPage(searchVo, request, response);
		return nodePage.getContent();
	}

	/**
	 * 新的分页查询接口，返回带有节点引用统计信息的数据
	 */
	@RequestMapping(value = "/newList")
	@ResponseBody
	public void newList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
		initSearch(searchVo, request);
		PageDown<PmProcessNodeRespVo> nodePage = processNodeClient.findNodePage(searchVo);
		JsonEasyUI.renderJson(response, nodePage);
	}
	
	@Override
	public Map<String, Object> getDefaultFilter() {
		Map<String, Object> map = Maps.newHashMap();
		map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
		return map;
	}
	
	@Override
	protected void preInsert(PmProcessNode e) {
		e.setEnterpriseId(ShiroUtil.getEnterpriseId());
	}
}
