package com.spt.bas.server.service.impl;

import com.spt.auth.sdk.entity.SysAppSdk;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysEnterpriseSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.auth.sdk.vo.UserRoleVo;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.service.IBsDictService;
import com.spt.bas.server.service.ISysInitService;
import com.spt.bas.server.util.DeptNodeUtil;
import com.spt.pm.dao.PmProcessAccessDao;
import com.spt.pm.dao.PmProcessDao;
import com.spt.pm.dao.PmProcessNodeDao;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.entity.PmProcessNode;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.pm.service.IPmProcessService;
import com.spt.tools.core.pinyin.PinyinUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.http.util.HTTPUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class SysInitServiceImpl implements ISysInitService{

	private Logger logger = LoggerFactory.getLogger(SysInitServiceImpl.class);
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Autowired
	private IBsKeySequenceService bsKeySequenceService;
	@Autowired
	private IPmProcessService processService;
	@Autowired
	private IBsDictService bsDictService;
	@Autowired
	private PmProcessNodeDao processNodeDao;
	@Autowired
	private PmProcessDao processDao;
	@Autowired
	private PmProcessAccessDao processAccessDao;

	private final String[] salesman_process = new String[]{BasConstants.PROCESS_APPLY_BUY,BasConstants.PROCESS_APPLY_SELL};
	private final String[] material_process = new String[]{BasConstants.PROCESS_CODE_IN,BasConstants.PROCESS_CODE_OUT};
	private final String[] financial_process = new String[]{BasConstants.PROCESS_CODE_PAY,BasConstants.PROCESS_CODE_RECEIVE,BasConstants.PROCESS_CTR_INVOICE,BasConstants.PROCESS_APPLY_INRECEIVED};

	@Override
	@ServerTransactional
	public void SystemInit(SysInitRequestVo sysVo) {
		try {
			String companyName = sysVo.getCompanyName();
			SysEnterpriseSdk searchVo = new SysEnterpriseSdk();
			searchVo.setName(companyName);
			SysEnterpriseSdk enterprise = authOpenFacade.findEnterpriseByName(searchVo);

			if(enterprise == null){
				//创建企业记录
				enterprise = new SysEnterpriseSdk();
				String code = PinyinUtil.getPinyinFirst(companyName);
				enterprise.setCode(code);
				enterprise.setStatus("0");
				enterprise.setIndustry("SL");
				enterprise.setName(companyName);
				enterprise = authOpenFacade.saveEnterprise(enterprise);

				//企业套账Id
				Long enterpriseId = enterprise.getId();

				//创建组织机构
				SysDeptSdk dept = new SysDeptSdk();
				//获取appId
				//SysAppSdk app = authOpenFacade.findAppByCode(BasConstants.APP_CODE);
				//Long appId = app.getId();

				//dept.setAppId(appId);
				dept.setDeptCd(code);
				dept.setDeptName(companyName);
				dept.setDeptType("company");
				dept.setOrderNum(1);
				dept.setStatus("0");
				dept.setEnterpriseId(enterpriseId);
				dept = authOpenFacade.saveDept(dept);

				//初始化keySequence数据
				bsKeySequenceService.initKeySequence(code.substring(0, 3), enterpriseId);

				//初始化审批流程数据
				processService.initProcess(enterpriseId);

				//初始化业务数据字典
				bsDictService.saveOurCompany(enterprise);

				//初始化时有所有塑料商品的权限
				/*List<BsProductType> productList = productTypeService.findByList("SL");
				for (BsProductType productType : productList) {
					BsProductTypeAccess access = new BsProductTypeAccess();
					access.setEnterpriseId(enterpriseId);
					access.setProductCd(productType.getTypeCode());
					productTypeService.save(productType);
				}*/
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	@ServerTransactional
	public void createUser(SysInitRequestVo sysVo){
		String companyName = sysVo.getCompanyName();
		SysEnterpriseSdk searchVo = new SysEnterpriseSdk();
		searchVo.setName(companyName);
		SysEnterpriseSdk enterprise = authOpenFacade.findEnterpriseByName(searchVo);

		List<SysUserVo> userList = sysVo.getUserList();
		if(enterprise!=null && !userList.isEmpty()){
			//将admin添加到用户列表
			SysUserVo vo = new SysUserVo();
			vo.setLoginName(sysVo.getAdmin());
			vo.setRoleCode("bas_new_admin");
			vo.setPassword("123456");
			vo.setUserName(sysVo.getAdmin());
			userList.add(vo);

			//获取appId
			SysAppSdk app = authOpenFacade.findAppByCode(BasConstants.APP_CODE);
			Long appId = app.getId();
			Long enterpriseId = enterprise.getId();

			//获取组织机构
			DeptSearchVo searchvo = new DeptSearchVo();
			//searchvo.setAppId(appId);
			searchvo.setEnterpriseId(enterpriseId);
			List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(searchvo);
			SysDeptSdk dept = deptList.get(0);

			SaasUserAccountVo accountVo = new SaasUserAccountVo();
			List<SaasUserVo> saasUserList = new ArrayList<>();
			for (SysUserVo sysUserVo : userList) {
				//如果存在该用户则不添加
				UserLoginVo loginVo = new UserLoginVo();
				loginVo.setLoginName(sysUserVo.getLoginName());
				SysUserSdk sysUser = authOpenFacade.findUserByLoginName(loginVo);

				if(sysUser ==null){
					SysUserSdk user = new SysUserSdk();
					//user.setAppId(SdkAppUtils.listAppIdToString(appId));
					user.setDept(dept);
					user.setDeptId(dept.getDeptId());
					user.setOrderNum(1);
					user.setStatus("0");
					user.setEnterpriseId(enterpriseId);
					user.setUserName(sysUserVo.getLoginName());
					user.setPassword(sysUserVo.getPassword());
					user.setNickName(sysUserVo.getUserName());
					user = authOpenFacade.saveUser(user);

					UserRoleVo rolevo = new UserRoleVo();
					rolevo.setId(user.getUserId());
					rolevo.setRoleKey(sysUserVo.getRoleCode());
					authOpenFacade.saveOneUserRole(rolevo);

					/*流程权限配置，1.使管理员可审批2.业务员可发起采购销售撮合等3.财务员可发起收付款，开收票4.物管员可发起出入库*/
					if(sysUserVo.getRoleCode().equals("bas_new_admin")){
						accountVo.setAdmin(user.getUserName());
						accountVo.setAccountId(user.getUserId());

						//把管理员配置为总经理节点的负责人
						PmProcessNode processNode = processNodeDao.findByNodeCodeAndEnterpriseId(BasConstants.PROCESS_NODE_BS_MANAGER, enterpriseId);
						processNode.setNodeUserId(user.getUserId()+"|");
						processNodeDao.save(processNode);

					}else{
						SaasUserVo userVo = new SaasUserVo();
						userVo.setLoginName(user.getUserName());
						userVo.setMappingAccountId(user.getUserId());
						userVo.setPlainPassword(sysUserVo.getPassword());
						saasUserList.add(userVo);

						//各个角色可发起相应申请单
						List<PmProcess> processList = null;
						if(sysUserVo.getRoleCode().equals("bas_new_cyuser")){
							processList = processDao.findByProcessCodeInAndEnterpriseId(material_process, enterpriseId);
						}else if(sysUserVo.getRoleCode().equals("bas_new_fin")){
							processList = processDao.findByProcessCodeInAndEnterpriseId(financial_process, enterpriseId);
						}else {
							processList = processDao.findByProcessCodeInAndEnterpriseId(salesman_process, enterpriseId);
						}
						for (PmProcess process : processList) {
							PmProcessAccess processAccess = new PmProcessAccess();
							processAccess.setEnterpriseId(enterpriseId);
							processAccess.setProcessId(process.getId());
							processAccess.setUserId(user.getUserId());
							processAccess.setUserName(user.getNickName());
							processAccessDao.save(processAccess);
						}

					}
				}

				accountVo.setSaasUserList(saasUserList);
			}

			//同步创建saas账号
			try {
				logger.info(">>>>>>回调saas创建账户<<<<<<", accountVo.getAdmin(),accountVo.getSaasUserList().size());
				HTTPUtility.doPostBody(PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL)+"/open/bs/userAccount/insertSales", accountVo, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<DeptNode> getDeptTree() {
		DeptSearchVo vo = new DeptSearchVo();
		//vo.setAppId(BasConstants.ZG_ENTERPRISE_APPID);
		vo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(vo);
		List<DeptNode> r = DeptNodeUtil.getDeptTree(deptList, true);
		return r;
	}
}
