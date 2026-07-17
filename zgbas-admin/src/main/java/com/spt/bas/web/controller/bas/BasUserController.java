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
//import com.spt.auth.sdk.cache.UserCache;
//import com.hsoft.admin.sdk.entity.SysDept;
//import com.hsoft.admin.sdk.entity.SysRole;
//import com.hsoft.admin.sdk.entity.SysUserSdk;
//import com.hsoft.admin.sdk.open.IAdminOpenFacade;
//import com.hsoft.admin.sdk.vo.SysDeptSearchVo;
//import com.hsoft.admin.sdk.vo.UserRoleVo;
//import com.spt.bas.client.constant.BasConstants;
//import com.spt.bas.web.shiro.ShiroUtil;
//import com.spt.bas.web.util.EasyTreeUtil;
//import com.spt.tools.core.json.JsonUtil;
//import com.spt.tools.data.easyui.EasyTreeNode;
//import com.spt.tools.web.util.RenderUtil;
//
//@Controller
//@RequestMapping("/bas/user")
//public class BasUserController {
//
//	@Autowired
//	private IAdminOpenFacade adminOpenFacade;
//
//	@RequestMapping(value = "")
//	public String index(Model model){
//		model.addAttribute("allRoles", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.ALL_ROLE)));
//		return "bas/user";
//	}
//
//	// 初始化树数据
//	@RequestMapping(value = "initTree")
//	public void initTree(HttpServletResponse response, SysDeptSearchVo vo) {
//		SysDeptSearchVo svo = new SysDeptSearchVo();
//		svo.setAppId(ShiroUtil.getCurrAppId());
//		svo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		List<SysDept> deptList = adminOpenFacade.findDeptAll(svo);
//		EasyTreeNode nodes = EasyTreeUtil.getDeptTree(deptList, true);
//		RenderUtil.renderJson(nodes.getChildren(), response);
//	}
//
//	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
//	public String detail(@PathVariable("id") Long id, Model model) {
//		SysUserSdk user = UserCache.getEntity(id);
//		SysDept dept= adminOpenFacade.findDeptByUserId(id);
//		user.setDept(dept);
//		List<SysRole> roles = adminOpenFacade.findRoleByUserId(id);
//		/*StringBuffer roleCds = new StringBuffer();
//		for (SysRole sysRole : roles) {
//			roleCds.append(sysRole.getRoleCd()+",");
//		}*/
//		model.addAttribute("roleCd", roles.get(0).getRoleCd());
//		model.addAttribute("user", user);
//		return "bas/user-detail";
//	}
//
//	@RequestMapping(value = "create/{deptId}", method = RequestMethod.GET)
//	public String create(@PathVariable("deptId") Long deptId, Model model) {
//		SysUserSdk user = new SysUserSdk();
//		user.setId(0l);
//		user.setAppId(ShiroUtil.getCurrAppId());
//		user.setEnableFlg(true);
//		SysDept dept =new SysDept();
//		dept.setId(deptId);
//		user.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		user.setDept(dept);
//		model.addAttribute("user", user);
//		return "bas/user-detail";
//	}
//
//	@RequestMapping(value = "allEableDept")
//	public void allEableDept(HttpServletResponse response) {
//		SysDeptSearchVo svo = new SysDeptSearchVo();
//		svo.setAppId(ShiroUtil.getCurrAppId());
//		svo.setEnterpriseId(ShiroUtil.getEnterpriseId());
//		List<SysDept> depts = adminOpenFacade.findDeptAll(svo);
//		EasyTreeNode nodes = EasyTreeUtil.getDeptTree(depts);
//		RenderUtil.renderJson(nodes.getChildren(), response);
//	}
//
//	@RequestMapping(value = "save", method = RequestMethod.POST)
//	public String save(@Valid @ModelAttribute("preload") SysUserSdk user, @RequestParam("deptId") Long deptId,HttpServletRequest request,HttpServletResponse response) {
//		String result = "success";
//		//LogUtil.saveOrUpdate(request, user, user.getId());
//		user = adminOpenFacade.saveUser(user);
//
//		//保存用户角色
//		String role = request.getParameter("roleCd");
//		if(StringUtils.isNotBlank(role)){
//			UserRoleVo vo = new UserRoleVo();
//			vo.setId(user.getId());
//			vo.setRoleCd(role);
//			adminOpenFacade.saveUserRole(vo);
//		}
//		result = result + ":" + String.valueOf(user.getId());
//		RenderUtil.renderText(result, response);
//		return null;
//	}
//
//	@ModelAttribute("preload")
//	public SysUserSdk getEntity(@RequestParam(value = "id", required = false) Long id) {
//		if (id != null) {
//			if (id > 0)
//				return adminOpenFacade.findUserById(id);
//			else
//				return new SysUserSdk();
//		}
//		return null;
//	}
//
//}
