//package com.spt.bas.web.controller.bas;
//
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.spt.auth.sdk.cache.DictUtil;
//import com.hsoft.admin.sdk.entity.SysDept;
//import com.hsoft.admin.sdk.open.IAdminOpenFacade;
//import com.hsoft.admin.sdk.vo.SysDeptSearchVo;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.web.shiro.ShiroUtil;
//import com.spt.bas.web.util.EasyTreeUtil;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.data.easyui.EasyTreeNode;
//import com.spt.tools.web.util.RenderUtil;
//
///**
// * 机构管理
// * @author huangjian
// *
// */
//@Controller
//@RequestMapping(value = "/bas/dept")
//public class BasDeptController {
//
//	@Autowired
//	private IAdminOpenFacade adminOpenFacade;
//
//	@RequestMapping(value = "")
//	public String index(Model model) {
//		//TODO 初始化deptCd
//		model.addAttribute("allDeptJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.ALL_DEPT)));
//		return "bas/dept";
//	}
//
//	// 初始化树数据
//	@RequestMapping(value = "initTree")
//	public void initTree(HttpServletResponse response, SysDeptSearchVo vo) {
//		SysDeptSearchVo svo = new SysDeptSearchVo();
//		svo.setAppId(ShiroUtil.getCurrAppId());
//		svo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		List<SysDept> deptList = adminOpenFacade.findDeptAll(svo);
//		EasyTreeNode nodes = EasyTreeUtil.getDeptTree(deptList);
//		RenderUtil.renderJson(nodes.getChildren(), response);
//	}
//
//	@RequestMapping(value = "create/{parentId}", method = RequestMethod.GET)
//	public String create(@PathVariable("parentId") Long parentId, Model model) {
//		SysDept dept = new SysDept();
//		dept.setId(0l);
//		dept.setAppId(ShiroUtil.getCurrAppId());
//		dept.setEnableFlg(true);
//		SysDept parent = new SysDept();
//		parent.setId(parentId);
//		dept.setParent(parent);
//		dept.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		model.addAttribute("dept", dept);
//		return "bas/dept-detail";
//	}
//
//	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
//	public String detail(@PathVariable("id") Long id, Model model) {
//		SysDept dept = adminOpenFacade.findDeptById(id);
//		model.addAttribute("dept", dept);
//		return "bas/dept-detail";
//	}
//
//	@RequestMapping(value = "save", method = RequestMethod.POST)
//	public String save(@Valid @ModelAttribute("preload") SysDept dept,HttpServletRequest request, HttpServletResponse response) {
//		String result = "success";
//		String parentIdStr =request.getParameter("parentId");
//		if (StringUtils.isNotBlank(parentIdStr)){
//			Long parentId = Long.valueOf(request.getParameter("parentId"));
//			SysDept parent = adminOpenFacade.findDeptById(parentId);
//			dept.setParent(parent);
//		}
//	//	LogUtil.saveOrUpdate(request, dept, dept.getId());
//		dept = adminOpenFacade.saveDept(dept);
//		result = result + ":" + String.valueOf(dept.getId());
//		RenderUtil.renderText(result, response);
//		return null;
//	}
//
//	@RequestMapping(value = "drag", method = RequestMethod.POST)
//	public String drag(@Valid @ModelAttribute("preload") SysDept dept, HttpServletRequest request) {
//		Long parentId = Long.valueOf(request.getParameter("parentId"));
//		SysDept parent = adminOpenFacade.findDeptById(parentId);
//		dept.setParent(parent);
//		adminOpenFacade.saveDept(dept);
//		return "redirect:/bas/dept/";
//	}
//
//	/** 所有有效的资源 */
//	@RequestMapping(value = "allEable")
//	public void allEable(HttpServletResponse response,SysDeptSearchVo vo) {
//		SysDeptSearchVo svo = new SysDeptSearchVo();
//		svo.setAppId(ShiroUtil.getCurrAppId());
//		svo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		List<SysDept> depts = adminOpenFacade.findDeptAll(svo);
//		EasyTreeNode nodes = EasyTreeUtil.getDeptTree(depts);
//		RenderUtil.renderJson(nodes.getChildren(), response);
//	}
//
//	/**
//	 * 使用@ModelAttribute, 实现Struts2
//	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
//	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
//	 */
//	@ModelAttribute("preload")
//	public SysDept getEntity(@RequestParam(value = "id", required = false) Long id) {
//		if (id != null) {
//			if (id > 0)
//				return adminOpenFacade.findDeptById(id);
//			else
//				return new SysDept();
//		}
//		return null;
//	}
//
//}
