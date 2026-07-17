/**
 *
 */
package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.PartnerCompany;
import com.spt.bas.client.entity.PartnerUser;
import com.spt.bas.client.remote.IPartnerCompanyClient;
import com.spt.bas.client.remote.IPartnerUserClient;
import com.spt.bas.client.remote.IPmProcessAccessClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.tools.core.encrypt.Digests;
import com.spt.tools.core.encrypt.Encodes;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangjian
 *
 */
@Controller
@RequestMapping(value = "/bs/partnerCompany")
public class BsPartnerCompanyController extends PageController<PartnerCompany, BaseVo> {
	@Autowired
	private IPartnerCompanyClient iPartnerCompanyClient;
	@Autowired
	private IPartnerUserClient iPartnerUserClient;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IPmProcessAccessClient iPmProcessAccessClient;
	@Autowired
	private IPmProcessClient iPmProcessClient;

	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	@RequestMapping(value = "")
	public String index(Model model) {

		model.addAttribute("enableFlgs",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		return "bs/partnerCompany";
	}

	@RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable("id") Long id, Model model) {
		PartnerCompany entity ;
		if (id != null && id != 0) {
			entity = iPartnerCompanyClient.getEntity(id);
		} else {
			entity = new PartnerCompany();
			entity.setId(0L);
			entity.setEnableFlg(true);
		}
		model.addAttribute("entity", entity);
		model.addAttribute("enableFlgJson",JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DictType.COMM_ENABLE_BOOLEAN)));
		return "bs/partnerCompany-detail";
	}


	@Override
	public BaseClient<PartnerCompany> getService() {
		return iPartnerCompanyClient;
	}

	@RequestMapping(value = "listUser/{id}")
	public void listUser(@PathVariable("id") Long id, HttpServletResponse response) {
		if (id != null && id > 0) {
			List<PartnerUser> byCompanyId = iPartnerUserClient.getByCompanyId(id);
			JsonEasyUI.renderListJson(response, byCompanyId);
		} else {
			JsonEasyUI.renderListJson(response, new ArrayList<>(0));
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public void save(@Valid @ModelAttribute("partnerCompany") PartnerCompany partnerCompany, HttpServletRequest request, HttpServletResponse response) {
		partnerCompany = getService().save(partnerCompany);
		RenderUtil.renderSuccess(partnerCompany.getId() + "", response);
	}


//	@RequestMapping(value = "savePartnerUser")
//	public void savePartnerUser(String userList,HttpServletRequest request,HttpServletResponse response) {
//		try{
//			List<PartnerUser> partnerUsers = JsonUtil.json2List(PartnerUser.class, userList);
//
//			// 部门ID
//			Long deptId = null;
//			// AppId
//			Long appId =  null;
//			// enterpriseId
//			Long enterpriseId = null;
//			List<SysDept> all = deptClient.findAll();
//			for (SysDept dept:all) {
//				String deptName = dept.getDeptName();
//				if (deptName.equals("事业合伙人")){
//					deptId=dept.getId();
//					appId = dept.getAppId();
//					enterpriseId = dept.getEnterpriseId();
//				}
//			}
//			StringBuffer stringBuffer = new StringBuffer();
//			for (PartnerUser partnerUser : partnerUsers) {
//				SysUserSdk SysUserSdk = new SysUserSdk(deptId,enterpriseId);
//				if (partnerUser.getSysUserSdkId() != null){
//					SysUserSdk.setId(partnerUser.getSysUserSdkId());
//				}
//				String loginName = partnerUser.getLoginName();
//				SysUserSdk byName = SysUserSdkClient.findByName(loginName);
//
//				if (Objects.nonNull(byName)){
//					if(!Objects.equals(byName.getId(),partnerUser.getSysUserSdkId())){
//						throw new Exception();
//					}
//				}
//				SysUserSdk.setUserType("P");
//				SysUserSdk.setUserStatus("O");
//				SysUserSdk.setName(partnerUser.getName());
//				SysUserSdk.setLoginName(partnerUser.getLoginName());
//				SysUserSdk.setMobile(partnerUser.getMobile());
//				SysUserSdk.setEnableFlg(partnerUser.getEnableFlg());
//				SysUserSdk.setAppId(appId);
//
//				byte[] salt = Digests.generateSalt(SALT_SIZE);
//				String strSalt = Encodes.encodeHex(salt);
//				String password = getEncodePwd("123456", salt);
//				SysUserSdk.setPassword(password);
//				SysUserSdk.setSalt(strSalt);
//
//
//				SysUserSdk save = SysUserSdkClient.save(SysUserSdk);
//
//				if (save != null){
//					Long SysUserSdkId = save.getId();
//					// 记录用户Id，用于添加角色
//					stringBuffer.append(SysUserSdkId);
//					stringBuffer.append(",");
//
//
//					if (partnerUser.getSysUserSdkId() != null && partnerUser.getSysUserSdkId() != 0){
//						PartnerUser byUserId = iPartnerUserClient.getByUserId(partnerUser.getSysUserSdkId());
//						partnerUser.setId(byUserId.getId());
//						partnerUser.setSysUserSdkId(byUserId.getSysUserSdkId());
//					}else{
//						partnerUser.setSysUserSdkId(SysUserSdkId);
//						// 给用户添加流程权限
//						String pmProcessList = iPmProcessClient.initPmProcessList();
//						PmProcessAccessVo pmVo = new PmProcessAccessVo();
//						pmVo.setEnterpriseId(enterpriseId);
//						pmVo.setUserName(partnerUser.getName());
//						pmVo.setUserId(SysUserSdkId);
//						pmVo.setProcessIds(pmProcessList);
//						iPmProcessAccessClient.saveByUser(pmVo);
//					}
//					iPartnerUserClient.save(partnerUser);
//
//				}
//
//			}
//			// 添加角色
//			SysRole byRoleCd = iSysRoleClient.findByRoleCd("zgbas_new_partners");
//			if (byRoleCd != null){
//				String SysUserSdkIdList = stringBuffer.toString();
//				iSysRoleClient.saveUsers(byRoleCd.getId(),SysUserSdkIdList);
//			}
//			RenderUtil.renderSuccess("success", response);
//
//		}catch (Exception e){
//			logger.error("generateSignature:", e);
//			String msg = "该登录名已被占用!";
//			if (e.getCause() != null) {
//				JSONObject jsonObject = JSONObject.parseObject(e.getCause().getMessage());
//				msg = jsonObject.getString("message");
//			}
//			RenderUtil.renderFailure(msg, response);
//		}
//	}
//
//	@RequestMapping(value = "deleteData/{id}")
//	public void deleteData(@PathVariable("id") Long id, HttpServletResponse response) {
//		PartnerUser entity = iPartnerUserClient.getEntity(id);
//		if (entity != null) {
//			Long SysUserSdkId = entity.getSysUserSdkId();
//			SysUserSdk SysUserSdk = SysUserSdkClient.getEntity(SysUserSdkId);
//			SysUserSdk.setEnableFlg(false);
//			SysUserSdkClient.save(SysUserSdk);
//			try {
//				entity.setEnableFlg(false);
//				entity.setDelFlg(true);
//				iPartnerUserClient.save(entity);
//				RenderUtil.renderSuccess("删除成功", response);
//			} catch (Exception e) {
//				e.printStackTrace();
//				RenderUtil.renderFailure("出现错误", response);
//			}
//		}
//
//	}
//	@RequestMapping(value = "deleteCompany/{id}")
//	public void deleteCompany(@PathVariable("id") Long id, HttpServletResponse response) {
//		List<PartnerUser> byCompanyId = iPartnerUserClient.getByCompanyId(id);
//		Boolean delAll = true;
//		if (byCompanyId.size()>0){
//			for (PartnerUser partnerUser:byCompanyId) {
//				Long SysUserSdkId = partnerUser.getSysUserSdkId();
//				SysUserSdk SysUserSdk = SysUserSdkClient.getEntity(SysUserSdkId);
//				SysUserSdk.setEnableFlg(false);
//				SysUserSdkClient.save(SysUserSdk);
//				try {
//					partnerUser.setEnableFlg(false);
//					partnerUser.setDelFlg(true);
//					iPartnerUserClient.save(partnerUser);
//					delAll = true;
//				} catch (Exception e) {
//					e.printStackTrace();
//					delAll = false;
//				}
//			}
//		}
//		if (delAll){
//			iPartnerCompanyClient.delete(id);
//			RenderUtil.renderSuccess("删除成功", response);
//		}else{
//			RenderUtil.renderFailure("出现错误", response);
//		}
//	}



	private String getEncodePwd(String plainPwd, byte[] salt) {
		byte[] hashPassword = Digests.sha1(plainPwd.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(hashPassword);
	}


}
