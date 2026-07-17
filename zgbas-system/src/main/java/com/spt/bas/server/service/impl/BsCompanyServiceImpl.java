package com.spt.bas.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hsoft.file.sdk.remote.FileRemote;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.*;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.remote.IWxUserDetailClient;
import com.spt.bas.report.client.entity.RptCompany;
import com.spt.bas.report.client.entity.RptSupplier;
import com.spt.bas.report.client.remote.IRptCompanyClient;
import com.spt.bas.report.client.remote.IRptSupplierClient;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.IBsCompanyAccountService;
import com.spt.bas.server.service.IBsCompanyOphisService;
import com.spt.bas.server.service.IBsCompanyService;
import com.spt.bas.server.service.ICompanyBusinessExpansionService;
import com.spt.bas.server.util.BsCompanyLogUtil;
import com.spt.bas.server.util.DeptUtils;
import com.spt.pm.constant.PmConstants;
import com.spt.pm.dao.PmApproveDao;
import com.spt.pm.entity.PmApprove;
import com.spt.sign.client.entity.EnterpriseAccount;
import com.spt.sign.client.entity.SignSeal;
import com.spt.sign.client.remote.ISignSealClient;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.core.exception.InvalidParamException;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.prop.PropertiesUtil;
import com.spt.tools.http.util.HTTPUtility;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class BsCompanyServiceImpl extends BaseService<BsCompany> implements IBsCompanyService {
	@Autowired
	private BsCompanyDao bsCompanyDao;
	@Autowired
	private BsCompanyAccountDao bsCompanyAccountDao;
	@Autowired
	private BsCompanyOphisDao bsCompanyOphisDao;
	@Autowired
	private BsCompanyContactsDao bsCompanyContactsDao;
	@Autowired
	private BsCompanyEvaluateDao bsCompanyEvaluateDao;
	@Autowired
	private BsCompanyFollowDao bsCompanyFollowDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private BsCompanyShareDao companyShareDao;
	@Autowired
	private IBsCompanyAccountService bsCompanyAccountService;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Resource
	private DeptUtils deptUtils;
	@Autowired
	private BsCompanyQuotaDao bsCompanyQuotaDao;
	@Autowired
	private PmApproveDao pmApproveDao;
	@Autowired
	private IWxUserDetailClient userDetailDao;
	@Autowired
	private IBsCompanyOphisService bsCompanyOphisService;
	@Autowired
	private BudgetSettlementDao budgetSettlementDao;

	@Autowired
	private IWxUserDetailClient wUserDetailClient;

	@Autowired
	private BsCompanyAllowedDao bsCompanyAllowedDao;

	@Autowired
	private ISignSealClient signSealClient;

	@Autowired
	private RtPushServiceImpl rtPushService;
	@Autowired
	private IRptCompanyClient rptCompanyClient;
	@Autowired
	private IRptSupplierClient rptSupplierClient;
	@Autowired
	private ICompanyBusinessExpansionService companyBusinessExpansionService;
	@Autowired
	private BsProductConfigDao bsProductConfigDao;

	@Override
	public BaseDao<BsCompany> getBaseDao() {
		return bsCompanyDao;
	}

	@Override
	public Class<BsCompany> getEntityClazz() {
		return BsCompany.class;
	}

	@Override
	public List<BsCompany> findByEnterpriseId(Long enterpriseId) {
		return bsCompanyDao.findByEnterpriseId(enterpriseId);
	}
	
	@Override
	public List<BsCompany> findByEnterpriseIdAndCompanyType(Long enterpriseId,String companyType) {
		return bsCompanyDao.findByEnterpriseIdAndCompanyType(enterpriseId,companyType);
	}

	@Override
	@ServerTransactional
	public void updateFileId(Long id, String fileId) {
		bsCompanyDao.updateFileId(id, fileId);
	}

	@Override
	@ServerTransactional
	public void updateScoreAndGrade(Long id, BigDecimal creditScore, String companyGrade) {
		bsCompanyDao.updateScoreAndGrade(id, creditScore,companyGrade);
	}

	@Override
	@ServerTransactional
	public void updateSupplierScoreAndGrade(Long id, BigDecimal supplierScore, String supplierGrade) {
		bsCompanyDao.updateSupplierScoreAndGrade(id, supplierScore,supplierGrade);
	}

	@Override
	@ServerTransactional
	public void updatePiccInfo(Long id,BigDecimal piccCreditAmount,BigDecimal piccHaveusedAmount,BigDecimal piccUseAbleaMount) {
		bsCompanyDao.updatePiccInfo(id, piccCreditAmount,piccHaveusedAmount,piccUseAbleaMount);
	}

	@Override
	@ServerTransactional
	public BsCompany save(BsCompany entity) throws ApplicationException {
		if (entity.getEnterpriseId() == null || entity.getEnterpriseId() == 0) {
			throw new InvalidParamException("enterpriseId");
		}

		if (entity.getCompanyShares() == null && Objects.nonNull(entity.getId())) {
			BsCompany old = getEntity(entity.getId());
			if (Objects.nonNull(old) && Objects.nonNull(old.getCompanyShares())){
				entity.setCompanyShares(old.getCompanyShares());
			}
		}

		List<BsCompany> lstCompany = bsCompanyDao.queryCompanyName(entity.getCompanyName(),entity.getEnterpriseId());
		if (entity.getId() == null || entity.getId() == 0) {
			entity.setEnableFlg(true);
			if (lstCompany.size() > 0) {
				throw new ApplicationException("该企业已经存在");
			}
		}
		return super.save(entity);
	}

	/**
	 * 递归查找子部门
 	 */
	private void findChildDeptIds(List<SysDeptSdk> allDept, Long parentId, List<Long> result) {
		for (SysDeptSdk dept : allDept) {
			if (dept.getParentId().equals(parentId)) {
				result.add(dept.getDeptId());
				findChildDeptIds(allDept, dept.getDeptId(), result); // 递归查找子部门
			}
		}
	}

	@Override
	public Page<BsCompany> findPageCompnay(BsCompanySearchVo queryVo) throws ApplicationException {
		if (queryVo.getEnterpriseId() == null || queryVo.getEnterpriseId() == 0) {
			throw new InvalidParamException("enterpriseId");
		}
		Sort sort = Sort.by(Direction.DESC, "id");
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows(), sort);
		Map<String, Object> searchParams=queryVo.getSearchParams();
		if (searchParams==null) {
			searchParams=new HashMap<>();
			queryVo.setSearchParams(searchParams);
		}
		searchParams.put("EQL_enterpriseId", queryVo.getEnterpriseId());
		searchParams.put("EQB_enableFlg", true);
		String muDeptName = String.valueOf(searchParams.get("muDeptName"));
		searchParams.remove("muDeptName");
		Specification<BsCompany> spec = WebUtil.buildSpecification(queryVo.getSearchParams());
		// 添加一个额外的条件来检查 accessReportFlg 不为空
		if(Objects.nonNull(queryVo.getAccessReportFlgExist())) {
			if(Boolean.TRUE.equals(queryVo.getAccessReportFlgExist())){
				Specification<BsCompany> specAccessReportId = new Specification<BsCompany>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<BsCompany> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						// 检查 accessReportId 不为空
						Predicate notNull = cb.isNotNull(root.get("accessReportFlg"));
						// 检查 accessReportId 不是空字符串
						Predicate notEmpty = cb.equal(root.get("accessReportFlg"), 1);

						return cb.and(notNull, notEmpty);
					}
				};
				spec = Specification.where(spec).and(specAccessReportId);
			} else {
				Specification<BsCompany> specAccessReportId = new Specification<BsCompany>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Predicate toPredicate(Root<BsCompany> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
						// 检查 accessReportId 不为空
						Predicate notNull = cb.isNull(root.get("accessReportFlg"));
						// 检查 accessReportId 不是空字符串
						Predicate notEmpty = cb.equal(root.get("accessReportFlg"), 0);

						return cb.or(notNull, notEmpty);	
					}};
				spec = Specification.where(spec).and(specAccessReportId);
			}
		}

		List<Long> deptLeaderDeptIds = new ArrayList<>();
		Boolean deptLeaderFlg = false;
		try {

			DeptSearchVo deptSearchVo = new DeptSearchVo();
			deptSearchVo.setEnterpriseId(queryVo.getEnterpriseId());
			List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
			// leaderSets：所有的部门负责人ID
			Set<Long> leaderSets = deptAll.stream().map(SysDeptSdk::getLeaderId).collect(Collectors.toSet());

			// 是部门负责人
			if(leaderSets.contains(queryVo.getUserId())) {
				deptLeaderFlg = true;
				// 获取该用户管理的所有部门ID
				List<Long> myDeptIds = authOpenFacade.findMyDeptId(queryVo.getUserId());
				deptLeaderDeptIds = myDeptIds;
			}
			
		} catch (Exception e) {

		}
		// 部门负责人条件
//		Specification<BsCompany> spec_deptLeader = WebUtil.buildSpecification("INS_matchUserId", deptLeaderUserIds);
		Specification<BsCompany> spec_deptLeader = WebUtil.buildSpecification("INS_deptId", deptLeaderDeptIds);
//		spec_deptLeader = Specification.where(spec_deptLeader).and(spec);
		
		
		// 事业部
		if (muDeptName != null && muDeptName != "null" && muDeptName != ""){
			Long deptId  = Long.valueOf(muDeptName.replaceAll("dept",""));
			if (deptId != null && deptId != 0){
				DeptSearchVo deptSearchVo = new DeptSearchVo();
				deptSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
				List<SysDeptSdk> deptAll = authOpenFacade.findDeptAll(deptSearchVo);
				List<Long> result = new ArrayList<>();
				result.add(deptId);
				findChildDeptIds(deptAll,deptId,result);
				logger.info(result.toString());
				Specification<BsCompany> spec_deptId = WebUtil.buildSpecification("INL_deptId", result);
				spec = Specification.where(spec).and(spec_deptId);
			}
		}
		String mode = queryVo.getMode();

		// 共享给我
		Specification<BsCompany> spec_share = new Specification<BsCompany>() {
			private static final long serialVersionUID = -8988010782813258796L;

			@Override
			public Predicate toPredicate(Root<BsCompany> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Subquery<BsCompanyShare> sq = query.subquery(BsCompanyShare.class);
				Root<BsCompany> sqc = sq.correlate(root);
				Join<BsCompany, BsCompanyShare> sqo = sqc.join("companyShares");
				Path<String> expression = sqo.get("sharedUserId");
				Predicate predicate = cb.equal(expression, queryVo.getUserId());
				sq.select(sqo).where(predicate);
				return cb.exists(sq);
			}
		};
		// 我创建
//		Specification<BsCompany> spec_myCreated = WebUtil.buildSpecification("EQL_createUserId", queryVo.getUserId());
		//私海
		Specification<BsCompany> spec_myFollow = WebUtil.buildSpecification("INS_status", new String[] {BasConstants.COMPANY_STATUS_D,BasConstants.COMPANY_STATUS_F});
		// 部门负责人私海
		spec_deptLeader = spec_myFollow.and(spec_deptLeader);
		//公海
		Specification<BsCompany> spec_public = WebUtil.buildSpecification("EQS_status", BasConstants.COMPANY_STATUS_N);

		// 当前所在部门私海
		List<Long> curDeptFollowDeptIds = new ArrayList<>();
		SysUserSdk user = authOpenFacade.findUserById(queryVo.getUserId());
		if (Objects.nonNull(user) && user.getDeptId() != null) {
			SysDeptSdk dept = authOpenFacade.findDeptById(user.getDeptId());
			if (Objects.nonNull(dept)) {
				List<Long> deptIds = new ArrayList<>();
				deptIds.add(dept.getDeptId());
				curDeptFollowDeptIds.add(dept.getDeptId());
				if (StringUtils.equals(BasConstants.DEPTTYPE_TEAM, dept.getDeptType())) {
					deptIds.add(dept.getParentId());
					curDeptFollowDeptIds.add(dept.getParentId());
				}

//				Specification<BsCompany> specIsNull = (root, query, criteriaBuilder) ->
//						criteriaBuilder.isNull(root.get("deptId"));
//				if (deptLeaderFlg) {
//					deptIds.addAll(deptLeaderDeptIds);
//				}
//				Specification<BsCompany> spec_dept = WebUtil.buildSpecification("INL_deptId", deptIds);
//				List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_COMPANY_SEARCH_NOT_PUBLIC);
//				List<Long> notDeptIds = new ArrayList<>();
//				if (CollectionUtils.isNotEmpty(listByCategory)) {
//					for (BsDictData bsDictData : listByCategory) {
//						notDeptIds.add(Long.valueOf(bsDictData.getDictCd()));
//					}
//				}
				// 公海限制区域 （）
//				Specification<BsCompany> spec_not_dept = WebUtil.buildSpecification("NINL_deptId", notDeptIds);

//				spec_dept = Specification.where(spec_dept).or(specIsNull).or(spec_not_dept);
				// 所属部门+所属部门为空 下的公海客户
//				spec_public = Specification.where(spec_public).and(spec_dept);
			}
		}
		Specification<BsCompany> spec_supplier = WebUtil.buildSpecification("EQS_companyType", BasConstants.DICT_TYPE_COMPANYTYPE_T);
		spec_public = Specification.where(spec_public).or(spec_supplier);
		// 所在部门私海
		Specification<BsCompany> spec_deptFollow = WebUtil.buildSpecification("INS_deptId", curDeptFollowDeptIds);
		//我创建，我的私海，共享给我
		Specification<BsCompany> spec_my =Specification.where(spec_myFollow).or(spec_share);
		// 领用人是自己
		Specification<BsCompany> spec_matchUserId = WebUtil.buildSpecification("EQS_matchUserId", queryVo.getUserId());
		String ownerOfAccount = queryVo.getSearchParams().get("EQL_ownerOfAccountId") == null ? null : queryVo.getSearchParams().get("EQL_ownerOfAccountId").toString();
		// M-我的私海 , P-公海数据, MP-我的私海+公海，A-全部
		if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_F)) {
			if(ownerOfAccount==null||ownerOfAccount==""){
				spec = Specification.where(spec);
			}else{
				spec = Specification.where(spec).and(spec_my);
			}
		} else if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_PUBLIC)) {
			// 公海查询
			if (queryVo.getLookAllCompany()) {
				spec = Specification.where(spec).and(spec_public);
			} else {
				if (queryVo.getHhrPerm()) {
					// 合伙人权限 （合伙人不能看供应商和公海，只能看自己的私海）
					spec = Specification.where(spec).and(spec_my).and(spec_matchUserId);
				} else {
					spec = Specification.where(spec).and(spec_public);
				}
			}
		} else if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_MP)) {
			spec_my = Specification.where(spec_my).or(spec_public);
			spec = Specification.where(spec).and(spec_my);
		} else if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_ALL)) {
			// 所有和我有关
			spec = Specification.where(spec);
		}else if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_N)) {
			// 不是我的
			spec = Specification.not(spec_my).and(spec);
		} else if (StringUtils.equals(mode, BasConstants.COMPANY_SEARCH_MODE_MY)) {
			// 私海查询
			if(!queryVo.getLookAllCompany()) {
				spec = Specification.where(spec).and(spec_my).and(spec_matchUserId);
			} else {
				// 查看所有权限
				if(ownerOfAccount==null||ownerOfAccount==""){
					spec = Specification.where(spec);
				}else{
					spec = Specification.where(spec).and(spec_my);
				}
			}
		} else {
			// 判断查看全部的权限
			if(!queryVo.getLookAllCompany()) {
				if (queryVo.getHhrPerm() != null && queryVo.getHhrPerm()) {
					// 合伙人权限 （合伙人不能看供应商和公海，只能看自己的私海）
					spec = Specification.where(spec).and(spec_my).and(spec_matchUserId);
				} else if (queryVo.getLookCurDeptCompanyPrem()) {
					// 查看本部门客户权限 spec_deptFollow
					spec = Specification.where(spec).and(spec_myFollow.and(spec_matchUserId).or(spec_share).or(spec_public).or(spec_deptFollow));
				} else {
					if(deptLeaderFlg) {
						spec = Specification.where(spec).and(spec_myFollow.and(spec_matchUserId).or(spec_share).or(spec_public).or(spec_deptLeader));
					} else {
						spec = Specification.where(spec).and(spec_myFollow.and(spec_matchUserId).or(spec_share).or(spec_public));
					}
				}
			}
		}

		Page<BsCompany> page = getBaseDao().findAll(spec, pageRequest);
		// sort属性无法反序列化，下面代码重新组装page对象，去掉sort属性
		PageRequest pageRequest_new = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<BsCompany> pageVo = new PageImpl<>(page.getContent(), pageRequest_new, page.getTotalElements());
		return pageVo;
	}

	@Override
	public Page<BsCompanyVo> findPageCompnayVo(BsCompanySearchVo queryVo) throws ApplicationException {
		// 展示所有用户
		Page<BsCompany> page = findPageCompnay(queryVo);
		Long userId = queryVo.getUserId();
		// 判断是否为共享客户
		List<Long> companyIds = companyShareDao.findCompanyIdBySharedUserId(userId);
		// 线上化查询处理
		List<UserDetail> userDetailListDB = wUserDetailClient.findAll();
		Map<Long, List<UserDetail>> userDetailMap = new HashMap<>();
		for (UserDetail userDetail:userDetailListDB) {
			List<UserDetail> userDetailList = userDetailMap.get(userDetail.getCompanyId());
			if(userDetailList != null && userDetailList.size() > 0) {
				userDetailList.add(userDetail);
			} else {
				userDetailList = new ArrayList<>();
				userDetailList.add(userDetail);
			}
			userDetailMap.put(userDetail.getCompanyId(),userDetailList);
		}
		List<BsCompanyVo> voList = new ArrayList<>();
		Map<Long, String> companyShareUserNamesMap = buildShareUserNames(page.getContent(), queryVo.getEnterpriseId());
		for (BsCompany company : page.getContent()) {
			BsCompanyVo vo = new BsCompanyVo();
			BeanUtils.copyProperties(company, vo);
			if (companyShareUserNamesMap.containsKey(company.getId())){
				vo.setShareUserNames(companyShareUserNamesMap.get(company.getId()));
			}
			List<UserDetail> userDetail = userDetailMap.get(company.getId());
			if(CollectionUtils.isNotEmpty(userDetail)) {
				UserDetail userDetail1 = userDetail.get(0);
				//cfca开户状态
				UserDetail cfcaApproveStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getCfcaApprovedStatus())).findFirst().orElse(userDetail1);
				vo.setCfcaApprovedStatus(cfcaApproveStatusUser.getCfcaApprovedStatus());
				//资料审核状态
				UserDetail companyApplyStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getCompanyApplyStatus())).findFirst().orElse(userDetail1);
				vo.setCompanyApplyStatus(companyApplyStatusUser.getCompanyApplyStatus());
				//委托授权状态
				UserDetail entrustApplyStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getEntrustApplyStatus())).findFirst().orElse(userDetail1);
				vo.setEntrustApplyStatus(entrustApplyStatusUser.getEntrustApplyStatus());
				//更新时间
				vo.setUpdatedDate(userDetail1.getUpdatedDate());
			}
			if (companyIds != null && companyIds.size() > 0 && companyIds.contains(company.getId())) {
				vo.setShareFlag(true);
			} else {
				vo.setShareFlag(false);
			}
			if (vo.getMatchUserId() != null) {
				DeptSearchVo searchVo = new DeptSearchVo();
				searchVo.setUserId(vo.getMatchUserId());
				searchVo.setDeptType(PmConstants.NODE_TYPE_CENTER);
				searchVo.setEnterpriseId(company.getEnterpriseId());
				try {
					//中心负责人ID
					Long deptLeader = authOpenFacade.findDeptLeader(searchVo);
					searchVo.setDeptType(PmConstants.NODE_TYPE_DEPT);
					//部门负责人ID
					Long teamLeader = authOpenFacade.findDeptLeader(searchVo);
					if (userId.equals(deptLeader) || userId.equals(teamLeader)) {
						vo.setMyDeptFlg(true);
					}
				} catch (Exception e) {

				}
			}
			setCompanyDept(vo);
			BigDecimal totalCreditAmount = vo.getTotalCreditAmount() == null ? BigDecimal.ZERO : vo.getTotalCreditAmount();
			BigDecimal usedCreditAmount = vo.getUsedCreditAmount() == null ? BigDecimal.ZERO : vo.getUsedCreditAmount();
			BigDecimal approveCreditAmount=vo.getApproveCreditAmount() == null ?BigDecimal.ZERO: vo.getApproveCreditAmount();
			if (totalCreditAmount != null && totalCreditAmount.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal remainCreditAmount = totalCreditAmount.subtract(usedCreditAmount).subtract(approveCreditAmount);
				vo.setRemainCreditAmount(remainCreditAmount);
			}
//			WfqH5urlVo wfqH5urlVo = new WfqH5urlVo();
//			wfqH5urlVo.setOrderNo(UUID.randomUUID().toString());
//			wfqH5urlVo.setCompanyName(company.getCompanyName());
//			String wfqH5url = wfqAuthClient.getWfqH5url(wfqH5urlVo);
//			vo.setWfqAuthH5url(wfqH5url);
			List<BsCompanyCredit> companyCreditList = company.getCompanyCreditList();
//			 将 companyCreditList 转换为以 creditType 为键的 Map
			Map<String, List<BsCompanyCredit>> creditTypeMap = companyCreditList.stream()
					.collect(Collectors.groupingBy(BsCompanyCredit::getCreditType));

			vo.setCreditInfo(handelCreditInfo(company.getCompanyCreditList()));
			voList.add(vo);
		}
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<BsCompanyVo> pageVo = new PageImpl<>(voList, pageRequest, page.getTotalElements());
		return pageVo;
	}

	private Map<Long, String> buildShareUserNames(List<BsCompany> companyList, Long enterpriseId){
		Map<Long, String> resultMap = new HashMap<>();
		List<Long> companyIdList = companyList.stream().map(BsCompany::getId).collect(Collectors.toList());
		List<BsCompanyShare> shareList = companyShareDao.findByCompanyIdIn(companyIdList);
		if (CollectionUtils.isNotEmpty(shareList)) {
			List<SysUserSdk> userAll = authOpenFacade.findUserAll(new UserSearchVo(enterpriseId, true));
			Map<Long, String> userMap = userAll.stream().collect(Collectors.toMap(SysUserSdk::getUserId, SysUserSdk::getNickName));
			Map<Long, List<BsCompanyShare>> shareMap = shareList.stream().collect(Collectors.groupingBy(BsCompanyShare::getCompanyId));
			shareMap.forEach((k, v) -> {
				List<String> shareUserNames = new ArrayList<>();
				v.forEach(share->{
					if (Objects.nonNull(share.getSharedUserId()) && userMap.containsKey(share.getSharedUserId())){
						shareUserNames.add(userMap.get(share.getSharedUserId()));
					}
				});
				if (CollectionUtils.isNotEmpty(shareUserNames)) {
					resultMap.put(k, String.join(",", shareUserNames));
				}
			});
		}
		return resultMap;
	}

	public String handelCreditInfo(List<BsCompanyCredit> companyCreditList){
		StringBuilder sb = new StringBuilder();
		// 查询授信额度表
		if (CollectionUtils.isNotEmpty(companyCreditList)) {
			// 过滤出有效状态数据
			companyCreditList = companyCreditList.stream().filter(c -> c.getEnableFlg() != null && c.getEnableFlg()).collect(Collectors.toList());
			Map<String, BsCompanyCredit> companyCreditMap = companyCreditList.stream()
					.collect(Collectors.toMap(BsCompanyCredit::getCreditType, m -> m, (a, b) -> b));
			// 人保额度
			BsCompanyCredit piccCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_0);
			// 大地额度
			BsCompanyCredit daDiCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_1);
			// 中银额度
			BsCompanyCredit zyCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_2);
			// 自主额度
			BsCompanyCredit ziZhuCompanyCredit = companyCreditMap.get(BasConstants.CREDIT_TYPE_9);
			boolean piccFlg = false;

			if (Objects.nonNull(piccCompanyCredit)) {
				piccFlg = true;
				sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_0, piccCompanyCredit));
			}
			boolean daDiFlg = false;
			if (Objects.nonNull(daDiCompanyCredit)) {
				daDiFlg = true;
				if (piccFlg) {
					sb.append("<br>");
				}
				sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_1, daDiCompanyCredit));
			}
			boolean zyFlg = false;
			if (Objects.nonNull(zyCompanyCredit)) {
				zyFlg = true;
				if (piccFlg || daDiFlg) {
					sb.append("<br>");
				}
				sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_2, zyCompanyCredit));
			}
			if (Objects.nonNull(ziZhuCompanyCredit)) {
				if (daDiFlg || piccFlg || zyFlg) {
					sb.append("<br>");
				}
				sb.append(spliceCreditInfo(BasConstants.CREDIT_TYPE_NAME_9, ziZhuCompanyCredit));
			}
		}
		return sb.toString();
	}
	public String spliceCreditInfo(String creditTYpe, BsCompanyCredit companyCredit){
		StringBuilder sb = new StringBuilder();
		// 授信额度
		BigDecimal creditAmount = companyCredit.getRiskAmount();
		BigDecimal creditAmountW = companyCredit.getRiskAmount().divide(new BigDecimal(10000));
		// 已用额度
		BigDecimal usedCreditAmount = companyCredit.getUsedCreditAmount();
		BigDecimal usedCreditAmountW = companyCredit.getUsedCreditAmount().divide(new BigDecimal(10000));
		// 临时额度
		BigDecimal temporaryAmount = companyCredit.getTemporaryAmount();
		BigDecimal temporaryAmountW = companyCredit.getTemporaryAmount().divide(new BigDecimal(10000));
		// 剩余额度
		BigDecimal availableCreditAmountW = (creditAmount.add(temporaryAmount).subtract(usedCreditAmount)).divide(new BigDecimal(10000));

		sb.append(creditTYpe).append(" ").append(creditAmountW).append(" 万");
		sb.append("，已用 ").append(usedCreditAmountW).append(" 万");
		sb.append("，剩余 ").append(availableCreditAmountW).append(" 万");
		sb.append("，临时 ").append(temporaryAmountW).append(" 万");
		return sb.toString();
	}

	/**
	 * 设置企业领用人所属部门展示字段
	 * @param vo
	 */
	private void setCompanyDept(BsCompanyVo vo){
//		SysDeptSdk sysDeptSdk = deptUtils.getDeptByUserIdAndDeptType(vo.getMatchUserId(), PmConstants.NODE_TYPE_DEPT);
//		if (Objects.nonNull(sysDeptSdk)){
//			vo.setMuDeptName(sysDeptSdk.getDeptName());
//		}
		if (vo.getDeptId() != null) {
			SysDeptSdk dept = authOpenFacade.findDeptById(vo.getDeptId());
			if (StringUtils.equals(BasConstants.DEPTTYPE_TEAM,dept.getDeptType()) && dept.getParentId() != null) {
				dept = authOpenFacade.findDeptById(dept.getParentId());
			}
			if (Objects.nonNull(dept)) {
				vo.setDeptName(dept.getDeptName());
			}
		}

	}

	@Override
	public Page<BsCompanyVo> findPageCompnayVoExcel(BsCompanySearchVo queryVo) throws ApplicationException {
		// 展示所有用户
		Page<BsCompany> page = findPageCompnay(queryVo);
		Long userId = queryVo.getUserId();
		// 判断是否为共享客户
		List<Long> companyIds = companyShareDao.findCompanyIdBySharedUserId(userId);
		// 线上化查询处理
		List<UserDetail> userDetailListDB = wUserDetailClient.findAll();
		Map<Long, List<UserDetail>> userDetailMap = new HashMap<>();
		for (UserDetail userDetail:userDetailListDB) {
			List<UserDetail> userDetailList = userDetailMap.get(userDetail.getCompanyId());
			if(userDetailList != null && userDetailList.size() > 0) {
				userDetailList.add(userDetail);
			} else {
				userDetailList = new ArrayList<>();
				userDetailList.add(userDetail);
			}
			userDetailMap.put(userDetail.getCompanyId(),userDetailList);
		}
		UserSearchVo sysDeptSearchVo = new UserSearchVo();
		sysDeptSearchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		List<SysUserSdk> allUser = authOpenFacade.findUserAll(sysDeptSearchVo);
		Map<Long, String> deptMap = allUser.stream()
				.filter(e -> Objects.nonNull(e.getDept()) && StringUtils.isNotBlank(e.getDept().getDeptName()))
				.collect(Collectors.toMap(SysUserSdk::getUserId, e -> e.getDept().getDeptName(), (a, b) -> b));

		List<Long> deptIds = authOpenFacade.findMyDeptId(userId);
		List<SysUserSdk> userSdkList = authOpenFacade.findByDeptIds(deptIds);
		// userIds：当前登录人所负责部门下的所有用户Id,包含当前登录人
		List<Long> userIds = userSdkList.stream().map(SysUserSdk::getUserId).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(userIds)) {
			userIds = new ArrayList<>();
		}
		userIds.add(userId);

		List<BsCompanyVo> voList = new ArrayList<>();
		Map<Long, String> companyShareUserNamesMap = buildShareUserNames(page.getContent(), queryVo.getEnterpriseId());

		boolean leaderExportPermitted = queryVo.getLeaderExportPermitted();
		Map<Long, SysUserSdk> leaderUserMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(userSdkList)) {
			leaderUserMap = userSdkList.stream().collect(Collectors.toMap(SysUserSdk::getUserId, a -> a));
		}
		
		for (BsCompany company : page.getContent()) {
			// 如果拥有区域总导出本区域公海数据权限，则公海数据只保留开户人是本区域的数据，其余数据剔除掉
			if (Boolean.TRUE.equals(leaderExportPermitted)) {
				String status = company.getStatus(); // N 公海
				Long ownerOfAccountId = company.getOwnerOfAccountId(); // 开户人
				// 如果是公海数据，则只保留开户人属于本区域的
				if (StringUtils.equals(BasConstants.COMPANY_STATUS_N, status)) {
					if (ownerOfAccountId != null && leaderUserMap.containsKey(ownerOfAccountId)) {
						// 开户人是本区域的，保留
					} else {
						continue;
					}
				} 
			}
			
			BsCompanyVo vo = new BsCompanyVo();
			BeanUtils.copyProperties(company, vo);
			if (companyShareUserNamesMap.containsKey(company.getId())){
				vo.setShareUserNames(companyShareUserNamesMap.get(company.getId()));
			}
			//线上化查询
			List<UserDetail> userDetail = userDetailMap.get(company.getId());
			if(CollectionUtils.isNotEmpty(userDetail)) {
				UserDetail userDetail1 = userDetail.get(0);
				//cfca开户状态
				UserDetail cfcaApproveStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getCfcaApprovedStatus())).findFirst().orElse(userDetail1);
				vo.setCfcaApprovedStatus(cfcaApproveStatusUser.getCfcaApprovedStatus());
				//资料审核状态
				UserDetail companyApplyStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getCompanyApplyStatus())).findFirst().orElse(userDetail1);
				vo.setCompanyApplyStatus(companyApplyStatusUser.getCompanyApplyStatus());
				//委托授权状态
				UserDetail entrustApplyStatusUser = userDetail.stream().filter(d -> StringUtils.equals("4", d.getEntrustApplyStatus())).findFirst().orElse(userDetail1);
				vo.setEntrustApplyStatus(entrustApplyStatusUser.getEntrustApplyStatus());
				//更新时间
				vo.setUpdatedDate(userDetail1.getUpdatedDate());
			}
			if (companyIds != null && companyIds.size() > 0 && companyIds.contains(company.getId())) {
				vo.setShareFlag(true);
			} else {
				vo.setShareFlag(false);
			}
			if (vo.getMatchUserId() != null) {
				// 判断领用人（vo.getMatchUserId()）是否为当前登录人所负责的人员（userIds）
				if(userIds.contains(vo.getMatchUserId())) {
					vo.setMyDeptFlg(true);
				} else {
					vo.setMyDeptFlg(false);
				}
			}
			setCompanyDept(vo);
			if (vo.getMatchUserId() != null) {
				String deptName = deptMap.getOrDefault(vo.getMatchUserId(),"") ;
				vo.setMuDeptName(deptName);
			}
			BigDecimal totalCreditAmount = vo.getTotalCreditAmount() == null ? BigDecimal.ZERO : vo.getTotalCreditAmount();
			BigDecimal usedCreditAmount = vo.getUsedCreditAmount() == null ? BigDecimal.ZERO : vo.getUsedCreditAmount();
			if (totalCreditAmount != null && totalCreditAmount.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal remainCreditAmount = totalCreditAmount.subtract(usedCreditAmount);
				vo.setRemainCreditAmount(remainCreditAmount);
			}

			// 当前登录人
			Long currentUserId = queryVo.getCurrentUserId();
			Boolean lookAllCompany = queryVo.getLookAllCompany();

			Long matchUserId = vo.getMatchUserId();
			Boolean myDeptFlg = vo.getMyDeptFlg();

			if(!Objects.equals(matchUserId,currentUserId) && !myDeptFlg) {
				String status = vo.getStatus();
				Boolean shareFlag = vo.getShareFlag();
				if(!lookAllCompany && !StringUtils.equals(BasConstants.COMPANY_STATUS_N,status) && !shareFlag) {
					continue;
				}
			}
			voList.add(vo);
		}
		PageRequest pageRequest = PageRequest.of(queryVo.getPage() - 1, queryVo.getRows());
		Page<BsCompanyVo> pageVo = new PageImpl<>(voList, pageRequest, page.getTotalElements());
		return pageVo;
	}

	@Override
	@ServerTransactional()
	public void updateCompanyStatus(CompanyStatusVo vo) {
		Long companyId = vo.getId();
		BsCompany oldCompany = new BsCompany();
		if (companyId != null) {
			BsCompany company = bsCompanyDao.findOne(companyId);
			BeanUtils.copyProperties(company,oldCompany);
			company.setStatus(vo.getStatus());
			if (BasConstants.COMPANY_STATUS_F.equals(vo.getStatus())) {
				company.setMatchUserId(vo.getCreateUserId());
				company.setMatchFllowDate(new Date());
				company.setDeptId(vo.getDeptId());
				if(vo.getCreateUserId() != null){
					SysDeptSdk deptSdk = deptUtils.getDeptByUserIdAndDeptType(vo.getCreateUserId(), PmConstants.NODE_TYPE_DEPT);
					company.setMatchUserDeptId(Objects.nonNull(deptSdk) ? deptSdk.getDeptId() : null);
				}
			}
			if (BasConstants.COMPANY_STATUS_N.equals(vo.getStatus())) {//退回公海用户,领用人和时间为空
				company.setMatchUserId(null);
				company.setMatchUserDeptId(null);
				company.setMatchFllowDate(null);
				if (vo.getDeptId() != null) {
					// 开户人部门
					company.setDeptId(vo.getDeptId());
				} else {
					company.setDeptId(null);
				}
			}
			company.setAssignedUserId(vo.getAssignedUserId());
			company.setAssignedUserName(vo.getAssignedUserName());
			// 开户人ID为空/0l说明是首次领用，将开户人id存储
			if(company.getOwnerOfAccountId() == null || company.getOwnerOfAccountId() == 0l){
				company.setOwnerOfAccountId(vo.getOwnerOfAccountId());
			}
			if(vo.getFreedToDeptLeaderCount() != null) {
				company.setFreedToDeptLeaderCount(vo.getFreedToDeptLeaderCount());
			}
			bsCompanyDao.save(company);
			// 退回 领用 历史记录
			BsCompanyOphisVo opHis = new BsCompanyOphisVo();
			opHis.setCompanyId(companyId);
			opHis.setCreateUserId(vo.getCreateUserId());
			opHis.setCreateUserName(vo.getCreateUserName());
			opHis.setStatus(vo.getStatus());
			opHis.setRemark(vo.getRemark());
			opHis.setEnterpriseId(company.getEnterpriseId());
			BsCompanyLogUtil.saveOrUpdate(null,oldCompany,company,company.getId(),vo.getCreateUserId(),
					vo.getCreateUserName(),company.getId(),vo.getStatus(),null);
		}
	}

	/**
	 * 指派业务员领用客户
	 */
	@Override
	@ServerTransactional
	public void updateStatusByAssigned(CompanyStatusVo vo) {
		BsCompany oldCompany = new BsCompany();
		BsCompany company = bsCompanyDao.findOne(vo.getId());
		BeanUtils.copyProperties(company,oldCompany);
		company.setStatus(vo.getStatus());
		company.setMatchUserId(vo.getMatchUserId());
		if(vo.getMatchUserId() != null){
			SysDeptSdk deptSdk = deptUtils.getDeptByUserIdAndDeptType(vo.getMatchUserId(), PmConstants.NODE_TYPE_DEPT);
			company.setMatchUserDeptId(Objects.nonNull(deptSdk) ? deptSdk.getDeptId() : null);
		}
		company.setAssignedUserId(vo.getAssignedUserId());
		company.setAssignedUserName(vo.getAssignedUserName());
		company.setMatchFllowDate(new Date());
		company.setDeptId(vo.getDeptId());
		bsCompanyDao.save(company);
		// 新增历史记录
		BsCompanyOphisVo opHis = new BsCompanyOphisVo();
		opHis.setCompanyId(company.getId());
		opHis.setCreateUserId(vo.getCreateUserId());
		opHis.setCreateUserName(vo.getCreateUserName());
		opHis.setStatus(vo.getStatus());
		opHis.setOptionType(BasConstants.COMPANY_STATUS_Z);
		opHis.setRemark(vo.getMatchUserName());
		opHis.setEnterpriseId(company.getEnterpriseId());
		bsCompanyOphisService.addCompanyHis(opHis);
		BsCompanyLogUtil.saveOrUpdate(null,oldCompany,company,company.getId(),vo.getCreateUserId(),vo.getCreateUserName(),
				company.getId(),vo.getStatus(),BasConstants.COMPANY_STATUS_Z);
	}

	/**
	 * 私海客户：60天不产生交易和服务，则划入公海
	 *
	 * 2021-12-28 规则修改：
	 * 		1、自己开发的客户，保留私海时间从领用之日起2个月，领入私海2个月没有成交记录，重新归入公海
	 * 		2、非自己开发的客户：当前业务员领入私海超过2天，近2个月没有成交记录，重新归入公海；近2个月有成交记录，保留私海
	 * 	
	 * 	
	 * 	2023-11-07 规则修改
	 * 	1、客户保留时间规则：
	 *   	自己开发的客户自动领用，保留时间 60 天
	 * 		领用他人客户后，保留时间 7 天
	 * 		维护跟进记录（内容长度≥15个字），刷新保留时间 7 天
	 * 		成交后，保留时间重新计算 60 天
	 *  2、客户释放规则：
	 * 		如果被共享人近2个月有成交，直接释放给最近成交的共享人
	 * 		释放给区域总，区域总可以指派，不能分享
	 * 		如果该客户已经被连续三次释放给区域总后，则直接释放到公海
	 *
	 */
	@Override
	@ServerTransactional
	public void updateStatusByTask() {
		// 查询私海客户
		String updateStep = "";
		Map<String, Object> map = new HashMap<>();
		Date nowDate = new Date();
		Date compareDate = DateOperator.addDays(nowDate, -60);
		map.put("EQS_status",BasConstants.COMPANY_STATUS_F);
		Specification<BsCompany> spec = WebUtil.buildSpecification(map);
		List<BsCompany> companyList = bsCompanyDao.findAll(spec);
		DeptSearchVo deptSearchVo = new DeptSearchVo(BasConstants.ZG_ENTERPRISE_ID);
		List<SysDeptSdk> deptAllList = authOpenFacade.findDeptAll(deptSearchVo);
		Map<Long, SysDeptSdk> leaderDeptAllMap = new HashMap<>();
		if(CollectionUtils.isNotEmpty(deptAllList)) {
			// 使用Stream API将deptAllList转换为以leader_id为key的Map，并定义合并函数
			leaderDeptAllMap = deptAllList.stream()
					.collect(Collectors.toMap(SysDeptSdk::getLeaderId, dept -> dept, (existing, replacement) -> existing));
		}


		//获取不退回公海企业的所属领用人集合
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_GRAY);
		// 自己开发
		Long own =Long.valueOf(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HIGH_SEAS_RETURN_TIME, BasConstants.OWN));
		// 非自己开发
		Long notOwn =Long.valueOf( BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HIGH_SEAS_RETURN_TIME, BasConstants.NOT_OWN));
		for (BsCompany bc : companyList) {
			long count = 0;
			long count1 = 0;
			// 开户人id ownerOfAccountId
			Long ownerOfAccountId = bc.getOwnerOfAccountId();
			// 领用人id matchUserId
			Long matchUserId = bc.getMatchUserId();
			boolean isSelfDevelop = false;// 是否自己开发客户
			if(ownerOfAccountId != null && matchUserId != null && ownerOfAccountId.equals(matchUserId)){
				// 自己开发
				isSelfDevelop = true;
				if (bc.getMatchFllowDate() != null
						&& DateUtil.between(bc.getMatchFllowDate(), nowDate, DateUnit.DAY) <= own) {
					continue;
				}
			} else {
				// 非自己开发
				if (bc.getMatchFllowDate() != null
						&& DateUtil.between(bc.getMatchFllowDate(), nowDate, DateUnit.DAY) <= notOwn) {
					continue;
				}
			}
			
			if(matchUserId == null) {
				matchUserId = 0L;
			}
			
			SysDeptSdk deptSdk = leaderDeptAllMap.get(matchUserId);
			if(Objects.nonNull(deptSdk) && deptSdk.getLeaderId() != null) {
				// 判断当前领用人是区域总，则不再进行释放
				continue;
			}
			
			// 维护跟进记录（内容长度≥15个字），刷新保留时间 7 天
			BsCompanyFollow companyFollow = bsCompanyFollowDao.findTopByCompanyIdAndCreateUserIdOrderByCreatedDateDesc(bc.getId(),matchUserId);
			if(Objects.nonNull(companyFollow) && DateUtil.between(companyFollow.getCreatedDate(), nowDate, DateUnit.DAY) <= notOwn) {
				String content = companyFollow.getContent();
				Long createUserId = companyFollow.getCreateUserId();
				// 判断跟进记录是否当前领用人并且维护长度大于15
				if(matchUserId != null && createUserId != null && matchUserId.equals(createUserId) 
						&& StringUtils.isNotBlank(content) && content.length()>15) {
					continue;
				}
			}


			boolean updateFlg = false;
			logger.info("定时任务1：companyName:{},companyId:{}", bc.getCompanyName(), bc.getId(),bc.getMatchUserId());
			// 查询该私海客户是否有产生交易
			List<CtrContract> ctrContracts = ctrContractDao.findByCompanyId(bc.getId());
			if (ctrContracts.isEmpty() && isSelfDevelop) {
				// 自己开发的客户，当前客户没有交易记录，判断领入时间是否超过60天，超过60天重新归入公海
				if (bc.getMatchFllowDate() == null || DateUtil.between(bc.getMatchFllowDate(), nowDate, DateUnit.DAY) > own) {
					updateFlg = true;
					updateStep = "step1";
				}
			} else if (ctrContracts.isEmpty() && !isSelfDevelop) {
				// 非自己开发的客户，当前客户没有交易记录，判断领入时间是否超过2天，超过2天重新归入公海
				if (bc.getMatchFllowDate() == null || DateUtil.between(bc.getMatchFllowDate(), nowDate, DateUnit.DAY) > notOwn) {
					updateFlg = true;
					updateStep = "step2";
				}
			} else {
				// a.getMatchUserId().equals(bc.getMatchUserId())
				List<Long> sellContractIds = ctrContracts.stream().filter(a -> a.getMatchUserId() != null && a.getMatchUserId().equals(bc.getMatchUserId()))
						.filter(a -> BasConstants.CONTRACT_STATUS_S.equals(a.getContractType()))
						.map(a -> a.getId()).collect(Collectors.toList());
				List<Long> buyContractIds = ctrContracts.stream().filter(a -> a.getMatchUserId() != null && a.getMatchUserId().equals(bc.getMatchUserId()))
						.filter(a -> BasConstants.CONTRACT_STATUS_B.equals(a.getContractType()))
						.map(a -> a.getId()).collect(Collectors.toList());

				logger.info("sellContractIds:{}", sellContractIds);
				Specification<BudgetSettlement> spec1;
				Specification<BudgetSettlement> spec2;
				if (!sellContractIds.isEmpty()) {
					Map<String, Object> bcMap = new HashMap<>();
					bcMap.put("INL_sellContractId", sellContractIds);
					bcMap.put("GTED_updatedDate", compareDate);
//					bcMap.put("EQS_budgetStatus", "4");
//					bcMap.put("EQS_budgetFinishStatus", "1");
					spec1 = WebUtil.buildSpecification(bcMap);
					count = budgetSettlementDao.count(spec1);
				}
				if (!buyContractIds.isEmpty()) {
					Map<String, Object> bcMap1 = new HashMap<>();
					bcMap1.put("INL_buyContractId", buyContractIds);
					bcMap1.put("GTED_updatedDate", compareDate);
//					bcMap1.put("EQS_budgetStatus", "4");
//					bcMap1.put("EQS_budgetFinishStatus", "1");
					spec2 = WebUtil.buildSpecification(bcMap1);
					count1 = budgetSettlementDao.count(spec2);
				}
				logger.info("count:{}", count+count1);
				if (count+count1 <= 0) {
					updateFlg = true;
					updateStep = "step3";
				}
			}
			logger.info("updateFlg:{}", updateFlg);

			Integer freedToDeptLeaderCount = bc.getFreedToDeptLeaderCount();
			if(freedToDeptLeaderCount == null) {
				freedToDeptLeaderCount = 0;
			}
			
			//过滤该企业领用人是否配置在特权字典里
			List<BsDictData> collect = listByCategory.stream().filter(p -> bc.getMatchUserId()!=null && p.getDictCd().equals(bc.getMatchUserId().toString())).collect(Collectors.toList());
			if (updateFlg && CollectionUtil.isEmpty(collect)) {
				// 无成交记录且领用人不在特权字典内，划入公海
				CompanyStatusVo vo = new CompanyStatusVo();
				vo.setStatus(BasConstants.COMPANY_STATUS_N);
				vo.setId(bc.getId());
				vo.setCreateUserId(0L);
				vo.setCreateUserName("系统任务");
				vo.setRemark("久未成交，系统自动释放到公海");
				logger.info("系统任务{}久未成交，系统自动释放到公海,updateType:{}", bc.getCompanyName(), updateStep);
				// 如果被共享人近2个月有成交，直接释放给最近成交的共享人
				CtrContract ctrContract = ctrContracts.stream().max(Comparator.comparing(CtrContract::getContractTime)).orElse(null);
				if (Objects.nonNull(ctrContract) && DateOperator.compareDays(compareDate, ctrContract.getContractTime()) >= 0) {
					SysUserSdk sysUser = authOpenFacade.findUserById(ctrContract.getMatchUserId());
					if (Objects.nonNull(sysUser) && StringUtils.equals("0", sysUser.getStatus())) {
						vo.setStatus(BasConstants.COMPANY_STATUS_F);
						vo.setCreateUserId(ctrContract.getMatchUserId());
						vo.setFreedToDeptLeaderCount(0);
						logger.info("企业:{}共享人近2个月有成交，直接释放给最近成交的共享人:{}", bc.getCompanyName(), ctrContract.getMatchUserName());
					}
				} else {
					Boolean toLeaderFlg = false;
					SysUserSdk user = authOpenFacade.findUserById(matchUserId);
					if(Objects.nonNull(user)) {
						List<BsDictData> dictDataList = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_FREED_TO_DEPT_LEADER);
						if(CollectionUtils.isNotEmpty(dictDataList)) {
							Map<String, BsDictData> dictDataMap = dictDataList.stream()
									.collect(Collectors.toMap(BsDictData::getDictCd, dict -> dict, (existing, replacement) -> existing));
							BsDictData bsDictData = dictDataMap.get(user.getDeptId()+"");
							if(Objects.nonNull(bsDictData)) {
								toLeaderFlg = true;
							}
						}
					}
					// 该员工所在的部门是否需要释放给区域总
					if(toLeaderFlg) {
						// 判断是否已释放给区域总三次，是：释放到公海，否：释放给区域总
						if(freedToDeptLeaderCount < 3) {
							DeptSearchVo searchVo = new DeptSearchVo();
							searchVo.setUserId(matchUserId);
							searchVo.setDeptType(PmConstants.NODE_TYPE_DEPT);
							searchVo.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
							vo.setStatus(BasConstants.COMPANY_STATUS_F);
							// deptAllMap: 所有区域总Map
//							SysDeptSdk deptSdk = deptAllMap.get(matchUserId);
							if(Objects.nonNull(deptSdk) && deptSdk.getLeaderId() != null) {
								// 判断当前是否为区域总，是区域总则不往上释放
								vo.setCreateUserId(matchUserId);
							} else {
								Long LeaderId = authOpenFacade.findDeptLeader(searchVo);
								vo.setCreateUserId(LeaderId);
							}
							vo.setFreedToDeptLeaderCount(freedToDeptLeaderCount+1);
						}
					}
					
					
				}
				updateCompanyStatus(vo);
			} else {
				// 有成交记录，释放给区域总次数置为0
				if(freedToDeptLeaderCount > 0) {
					bsCompanyDao.updateFreedToDeptLeaderCount(0,bc.getId());
				}
				
			}
		}
	}

	/**
	 * 自动归入供应商灰名单与终端工厂灰名单
	 * @return
	 */
	@Override
	@ServerTransactional
	public void updateGreyListByTask() {
		Date nowDate = new Date();
		List<BsCompany> companyList = bsCompanyDao.findByEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		//获取不退回灰名单企业的所属领用人集合
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_GRAY);
		for (BsCompany bc : companyList) {
			BsCompany oldCompany = new BsCompany();
			BeanUtils.copyProperties(bc,oldCompany);
			if (!BasConstants.DICT_TYPE_CREDITRATING_W.equals(bc.getCreditRating()) && !BasConstants.DICT_TYPE_CREDITRATING_W.equals(bc.getSupplierRating())) {
				// 如果客户、供应商均不是白名单，则直接跳过
				continue;
			}
//			if ((bc.getMatchFllowDate() != null
//					&& DateUtil.between(bc.getMatchFllowDate(), nowDate, DateUnit.DAY) <= 60)
//					|| BasConstants.DICT_TYPE_CREDITRATING_G.equals(bc.getCreditRating())) {
//				continue;
//			}
			// 查找最近供应商准入申请
//			ApplySupplierAllowed applySupplierAllowed = applySupplierAllowedDao.findTopByCompanyIdOrderByCreatedDateDesc(bc.getId());
//			if (applySupplierAllowed != null && StringUtils.equals(applySupplierAllowed.getSupplierRating(),BasConstants.DICT_TYPE_CREDITRATING_W)
//					&& DateUtil.between(applySupplierAllowed.getCreatedDate(), nowDate, DateUnit.DAY) <= 60) {
//				continue;
//			}
			// 查找最近客户准入申请
			BsCompanyAllowed bsCompanyAllowed = bsCompanyAllowedDao.findTopByCompanyIdOrderByCreatedDateDesc(bc.getId());
			if (bsCompanyAllowed != null && StringUtils.equals(bsCompanyAllowed.getCreditRating(),BasConstants.DICT_TYPE_CREDITRATING_W)
					&& DateUtil.between(bsCompanyAllowed.getCreatedDate(), nowDate, DateUnit.DAY) <= 60) {
				continue;
			}
			boolean updateFlg = false;
			logger.info("定时任务2：companyName:{},companyId:{}", bc.getCompanyName(), bc.getId());
			// 查询该私海客户是否有产生交易
			Date compareDate = DateOperator.addMonthes(nowDate, -2);
			Long companyId = bc.getId();
			Map<String, Object> bcMap = new HashMap<>();
			bcMap.put("EQL_companyId", companyId);
			bcMap.put("GTED_createdDate", compareDate);
			Specification<CtrContract> spec1 = WebUtil.buildSpecification(bcMap);
			long count = ctrContractDao.count(spec1);
			logger.info("count:{}", count);
			if (count <= 0) {
				updateFlg = true;
			}
			logger.info("updateFlg:{}", updateFlg);
			//过滤该企业领用人是否配置在特权字典里
			List<BsDictData> collect = listByCategory.stream().filter(p -> bc.getMatchUserId()!=null && p.getDictCd().equals(bc.getMatchUserId().toString())).collect(Collectors.toList());
			if (updateFlg && CollectionUtil.isEmpty(collect)) {
				// 无成交记录且领用人不在特权字典内，划入供应商和终端工厂的灰名单
				Boolean saveFlg = false;
				String creditRating = bc.getCreditRating();
				String supplierRating = bc.getSupplierRating();
				// 如果本身是灰名单或黑名单不执行修改操作
				if(StringUtils.isNotBlank(creditRating) && StringUtils.equals(BasConstants.DICT_TYPE_CREDITRATING_W,creditRating)) {
					bc.setCreditRating(BasConstants.DICT_TYPE_CREDITRATING_G);
					saveFlg = true;
				}

				// 供应商不自动调整为黑名单 需求（TAPD:1001832）
//				if(StringUtils.isNotBlank(supplierRating) && StringUtils.equals(BasConstants.DICT_TYPE_CREDITRATING_W,supplierRating)) {
//					bc.setSupplierRating(BasConstants.DICT_TYPE_CREDITRATING_G);
//					saveFlg = true;
//				}
				
				if(saveFlg) {
					bsCompanyDao.save(bc);
					CompanyStatusVo vo = new CompanyStatusVo();
					vo.setStatus(BasConstants.DICT_TYPE_CREDITRATING_G);
					vo.setId(bc.getId());
					BsCompanyOphisVo opHis = new BsCompanyOphisVo();
					opHis.setCompanyId(companyId);
					opHis.setCreateUserId(0L);
					opHis.setCreateUserName("系统任务");
					opHis.setStatus(bc.getStatus());
					opHis.setRemark(vo.getRemark());
					opHis.setEnterpriseId(bc.getEnterpriseId());
//				bsCompanyOphisService.addCompanyHis(opHis);
					BsCompanyLogUtil.saveOrUpdate(null,oldCompany,bc,bc.getId(),vo.getCreateUserId(),vo.getCreateUserName(),
							bc.getId(),vo.getStatus(),null);
				}
			}
		}
	}

	/**
	 * 开户人id 刷新历史数据
	 * @return
	 */
	@Override
	@ServerTransactional
	public void updateOwnerOfAccountId() {
		List<BsCompany> companyList = bsCompanyDao.findByEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
		for (BsCompany bc : companyList) {
			Long ownerOfAccountId = bc.getOwnerOfAccountId();
			// 如果开户人id存在,则说明已经刷新过历史数据，则跳过
			if(ownerOfAccountId != null && ownerOfAccountId > 0l) continue;
			try {
				List<BsCompanyOphis> bsCompanyOphisList = bsCompanyOphisDao.findByCompanyId(bc.getId());
				// 企业操作历史表中无记录跳过
				if(bsCompanyOphisList == null || bsCompanyOphisList.size() == 0) continue;
				BsCompanyOphis bsCompanyOphis = bsCompanyOphisList.get(0);
				if(bsCompanyOphis.getCreateUserId() == null) continue;
				bsCompanyDao.updateOwnerOfAccountId(bc.getId(),bsCompanyOphis.getCreateUserId());
				logger.info("开户人id刷新历史数据定时任务：companyName:{},companyId:{}", bc.getCompanyName(), bc.getId(), bsCompanyOphis.getCreateUserId());
			} catch (Exception e) {
				logger.info("刷新开户人id失败:{}",e);
			}


		}
	}

	/**
	 * 超保额度到期自动恢复
	 */
	@Override
	@ServerTransactional
	public void recoverTotalCreditAmount() {
		try {
			Date beforeDate = DateOperator.addDays(new Date(), -7);
			// 查询最近7天的数据
			List<BsCompanyQuota> bsCompanyQuotaList = bsCompanyQuotaDao.findAllByCreatedDateBetween(beforeDate, new Date());
			if(bsCompanyQuotaList != null && bsCompanyQuotaList.size() > 0){
				bsCompanyQuotaList.forEach(c ->{
					// 查询
//					PmApprove pmApprove = pmApproveDao.findApproveNoByApproveId(c.getApproveId());
					PmApprove pmApprove = pmApproveDao.findTopByIdAndCompanyIdAndStatusOrderByIdDesc(c.getApproveId(),c.getCompanyId(), BasConstants.APPROVE_STATUS_D);
					// 是否是完成状态
					if(pmApprove != null && BasConstants.APPROVE_STATUS_D.equals(pmApprove.getStatus())){
						Date lastApproveDate = pmApprove.getLastApproveDate();
						// 额度有效日期 审批完成后3天为有效期
						Date effectiveDate = DateOperator.addDays(lastApproveDate, +3);
						Date newDate = new Date();
						// 超过有效期自动恢复原额度
						if(newDate.compareTo(effectiveDate) > 0){
							BsCompany bsCompany = bsCompanyDao.findOne(c.getCompanyId());
							BigDecimal totalTemporaryAmount = bsCompany.getTotalTemporaryAmount();
							if(bsCompany != null && bsCompany.getTotalCreditAmount() !=null && totalTemporaryAmount != null){
								if(totalTemporaryAmount.compareTo(new BigDecimal(0)) > 0){
									bsCompanyDao.updateTotalCreditAmountById(bsCompany.getId(),totalTemporaryAmount);
									// 更新userDetail金额信息
									UserDetail userDetail = userDetailDao.findByCompanyIdAndIsBindTrueAndEnableFlgTrue(c.getCompanyId());
									if (userDetail != null) {
										userDetail.setTotalCreditAmount(totalTemporaryAmount);
										userDetailDao.save(userDetail);
									}
									logger.info("超保额度到期自动恢复成功companyId：{}",bsCompany.getId());
								}
							}
						}

					}
				});
			}
		} catch (Exception e){
			logger.error("超保额度到期自动恢复失败");
			e.printStackTrace();
		}
	}

	/**企业共享*/
	@Override
	public BsCompanyShare shareCompany(BsCompanyShare vo) throws ApplicationException {
		if (vo.getEnterpriseId() == null || vo.getEnterpriseId() == 0) {
			throw new InvalidParamException("enterpriseId");
		}
		if (vo.getCompanyId() == null || vo.getCompanyId() == 0) {
			throw new InvalidParamException("companyId");
		}
		if (vo.getSharedUserId() == null || vo.getSharedUserId() == 0) {
			throw new InvalidParamException("sharedUserId");
		}
		if (vo.getCreateUserId() == null || vo.getCreateUserId() == 0) {
			throw new InvalidParamException("createUserId");
		}
		BsCompanyShare entity = companyShareDao.findByCompanyIdAndSharedUserId(vo.getCompanyId(), vo.getSharedUserId());
		if (entity != null) {
			return entity;
		}
		entity = companyShareDao.save(vo);
		return entity;
	}

	@Override
	public List<BsCompany> findByCompanyName(String companyName,Long enterpriseId) {
		List<BsCompany> bsCompanyList;
		if (enterpriseId != null && enterpriseId > 0L) {
			bsCompanyList = bsCompanyDao.queryCompanyName(companyName,enterpriseId);
		}else {
			bsCompanyList = bsCompanyDao.findByCompanyName(companyName);
		}
		return bsCompanyList;
	}

	@Override
	public CompanyAccountVo findCompanyAccountVo(Long companyId) {
		CompanyAccountVo vo = new CompanyAccountVo();
		BsCompany company = this.getEntity(companyId);
		if (Objects.isNull(company)){
			return vo;
		}
		BsCompanyAccount account = null;
		BeanUtils.copyProperties(company, vo);
		List<BsCompanyAccount> lstAcct = bsCompanyAccountService.queryCompanyAccount(companyId);
		if (CollectionUtils.isNotEmpty(lstAcct)) {
			account = lstAcct.get(0);
		}
		if (account != null) {
			vo.setBankName(account.getBankName());
			vo.setBankAccount(account.getBankAccount());
			vo.setTaxNo(account.getTaxNo());
		}
		return vo;
	}

	@Override
	@ServerTransactional
	public BsCompany saveCompanyAccountVo(CompanyAccountVo vo) throws ApplicationException {
		logger.info("saveCompanyAccountVo : " + JsonUtil.obj2Json(vo));
		BsCompany company;
		BsCompany oldCompany = new BsCompany();
		Boolean newCompanyFlg = false;
		String optionType = "";
		if(vo.getId()!=null && vo.getId()>0) {
			optionType = BasConstants.COMPANY_STATUS_U;
			company = bsCompanyDao.findOne(vo.getId());
			BeanUtils.copyProperties(company,oldCompany);
			company.setEnableFlg(vo.getEnableFlg());
		}else {
			optionType = BasConstants.COMPANY_STATUS_T;
			newCompanyFlg = true;
			oldCompany = new BsCompany();
			company = new BsCompany();
			company.setEnableFlg(true);
		}
		company.setAccessReportId(vo.getAccessReportId());
		company.setAddress(vo.getAddress());
		company.setBankAccount(vo.getBankAccount());
		company.setBankName(vo.getBankName());
		company.setCompanyArea(vo.getCompanyArea());
		company.setCompanyGrade(vo.getCompanyGrade());
		company.setCompanyName(vo.getCompanyName());
		company.setCompanyPhone(vo.getCompanyPhone());
		company.setCompanyType(vo.getCompanyType());
		company.setCompanyUrl(vo.getCompanyUrl());
		company.setContactName(vo.getContactName());
		company.setContactPhone(vo.getContactPhone());
		company.setCreateUserId(vo.getCreateUserId());
//		company.setCreditRating(vo.getCreditRating());
		company.setEnterpriseId(vo.getEnterpriseId());
		company.setLegalRepresent(vo.getLegalRepresent());
		company.setEmail(vo.getEmail());
		//company.setMatchFllowDate(vo.getMatchFllowDate());
		//company.setMatchUserId(vo.getMatchUserId());
		company.setRegisterCapital(vo.getRegisterCapital());
		company.setStafferNumber(vo.getStafferNumber());
		company.setStatus(vo.getStatus());
		company.setTaxNo(vo.getTaxNo());
//		company.setOnLineFlg(vo.getOnLineFlg());
		company.setCompanyFax(vo.getCompanyFax());

		company.setFileId(vo.getFileId());
		company.setCardFrontId(vo.getCardFrontId());
		company.setCardReverseId(vo.getCardReverseId());
		company.setCorporateCreditId(vo.getCorporateCreditId());
		company.setPersonalCreditId(vo.getPersonalCreditId());
		company.setTrademarkId(vo.getTrademarkId());
		company.setPatentId(vo.getPatentId());
		company.setPersonalGuaranteeId(vo.getPersonalGuaranteeId());
		company.setAssetGuaranteeId(vo.getAssetGuaranteeId());
		company.setAssetsId(vo.getAssetsId());
		company.setCashFlowId(vo.getCashFlowId());
		company.setProfitId(vo.getProfitId());
		company.setAuditReportId(vo.getAuditReportId());
		company.setLandId(vo.getLandId());
		company.setPlantId(vo.getPlantId());
		company.setEquipmentId(vo.getEquipmentId());

		//String companyType = vo.getCompanyType();
		company.setIndustry(vo.getIndustry());
		company.setAllowed(vo.getAllowed());
		company.setCompanyType(vo.getCompanyType());
		company.setCompanyCategory(vo.getCompanyCategory());
		company.setLandType(vo.getLandType());
		company.setPlantType(vo.getPlantType());
		company.setEquipmentType(vo.getEquipmentType());
		company.setAccessReportFlg(vo.getAccessReportFlg());//访厂报告是否通过
		company.setActualGuaranteeFlg(vo.getActualGuaranteeFlg());//实控人担保
		company.setLogisticsSealType(vo.getLogisticsSealType());//物流章类型
		company.setAccessReportUploadDate(vo.getAccessReportUploadDate());

		company.setBuyCommissionFlag(vo.getBuyCommissionFlag());
		company.setMarkSupplierFlag(vo.getMarkSupplierFlag());
		company.setSupplierManagerUserId(vo.getSupplierManagerUserId());
		company.setPlasticType(vo.getPlasticType());
		company.setTemporaryPlasticType(vo.getTemporaryPlasticType());
/*
		if (StringUtils.equals(BasConstants.DICT_TYPE_CREDITRATING_W, vo.getCreditRating())) {
			company.setEnableFlg(true);
		}else {
			company.setEnableFlg(false);
		}
		*/
		//获取企业授信
//		company = getCompanyCredit(company);
//		logger.info("saveCompanyAccountVo : " + JsonUtil.obj2Json(company));

		company = save(company);

//		pushCompanyToUcs(company);

		//更新线上化标识
//		refreshCompanyFlg(company.getId());

		bsCompanyAccountService.saveBatch(vo.getLstInsert(),vo.getLstUpdate(),vo.getLstDelete(),company);
		bsCompanyAccountService.saveBatchAddr(vo.getWlstInsert(),vo.getWlstUpdate(),vo.getWlstDelete(),company);
		//添加新增操作记录
		BsCompanyOphisVo opHis = new BsCompanyOphisVo();
		opHis.setCompanyId(company.getId());
		opHis.setCreateUserId(vo.getCreateUserId());
		opHis.setCreateUserName(vo.getCreateUserName());
		opHis.setStatus(company.getStatus());
		opHis.setOptionType(BasConstants.COMPANY_STATUS_T);
		opHis.setEnterpriseId(company.getEnterpriseId());
//		bsCompanyOphisService.addCompanyHis(opHis);


		BsCompanyLogUtil.saveOrUpdate(null,oldCompany,company,company.getId(),vo.getCreateUserId(),
				vo.getCreateUserName(),company.getId(),company.getStatus(),optionType);

//		refreshCompanyMessage(company,vo.getPermittedFlg());
		if (newCompanyFlg){
			rtPushService.pushCompanyToRt(company);
		}
		return company;
	}


	@Override
	@ServerTransactional
	public void delete(Long id) throws ApplicationException {
		bsCompanyOphisDao.deleteByCompanyId(id);
		bsCompanyContactsDao.deleteByCompanyId(id);
		bsCompanyEvaluateDao.deleteByCompanyId(id);
		bsCompanyFollowDao.deleteByCompanyId(id);
		companyShareDao.deleteByCompanyId(id);
		bsCompanyAccountDao.deleteByCompanyId(id);
		super.delete(id);
	}

	/**
	 * 获取当天业务员领用的客户
	 */
	@Override
	public List<BsCompany> getCompanyForDate(Long matchUserId) {
		return bsCompanyDao.getCompanyForDate(matchUserId);
	}

	@Override
	@ServerTransactional
	public void updateByIds(Long[] condition) {
		bsCompanyDao.updateByIds(condition);
	}

	@Override
	@ServerTransactional
	public void refreshCompanyFlg(Long companyId){
		try {
			BsCompany bsCompany = this.getEntity(companyId);
			String saasUrl = PropertiesUtil.getProperty(BasConstants.SAAS_GATEWAY_URL);
			String url = saasUrl + BasConstants.SAAS_COMPANY_FLG_URL;

			String result = HTTPUtility.doPostBody(url, bsCompany, null);
			JSONObject jsonObj = JSONObject.parseObject(result);
			JSONObject basic = jsonObj.getJSONObject("data");
			String code = jsonObj.getString("code");
			if ("200".equals(code) && basic != null) {
				BsCompany company = new BsCompany();
				company = JsonUtil.json2Object(BsCompany.class, basic.toString());
				bsCompany.setOpenAccountFlg(company.getOpenAccountFlg());
				bsCompany.setOpenAdminFlg(company.getOpenAdminFlg());
				bsCompany.setOpenCfcaFlg(company.getOpenCfcaFlg());
				this.save(bsCompany);
			}
		} catch (Exception e) {
			logger.error("同步线上化标识异常!!!",e.getMessage());
		}
	}

	@Override
	public String getAddressFromUcs(String companyName) {
		String companyAddress = "";
		try {
			UcsCreditReceiveVo vo = new UcsCreditReceiveVo();
			vo.setCompanyName(companyName);
			vo.setAppCode("bps");

			String url = PropertiesUtil.getProperty(BasConstants.UCS_PUSH_URL);
			String pushUrl = url + BasConstants.GET_COMPANY_CREDIT_URL;
			String result = HTTPUtility.doPostBody(pushUrl, vo, null);
			JSONObject jsonObj = JSONObject.parseObject(result);
			String code = jsonObj.getString("code");
			if (code.equals("200")) {
				JSONObject basic = jsonObj.getJSONObject("data");
				if (basic != null) {
					companyAddress = basic.getString("companyAddress");
				}
			}
		} catch (Exception e) {
			logger.error("获取企业地址异常!,{}",e.getMessage());
		}
		return companyAddress;
	}

	@Override
	public BsCompany getCompanyDetail(Long companyId) {
		if (companyId != null) {
			BsCompany entity = this.getEntity(companyId);
			String address = getAddressFromUcs(entity.getCompanyName());
			entity.setAddress(address);
			return entity;
		}
		return null;
	}

	@Override
	public List<BsCompany> findByCompanyNames(String companyNames) {
		List<BsCompany> result = Collections.EMPTY_LIST;
		if (!StringUtils.isEmpty(companyNames)) {
			String[] split = companyNames.split(",");
			List<String> names = Arrays.asList(split);
			return bsCompanyDao.findByCompanyNameIn(names);
		}
		return result;
	}

	@Override
	public BsCompany findByCompanyName(String companyName) {
		List<BsCompany> company = bsCompanyDao.findByCompanyName(companyName);
		if (!company.isEmpty()) {
			return company.get(0);
		}
		return null;
	}

	@Override
	public List<BsCompany> findByContact(String phone) {
		return bsCompanyDao.findByContactPhoneAndEnterpriseIdAndEnableFlgTrue(phone, BasConstants.ZG_ENTERPRISE_ID);
	}

	@Override
	public BsCompany findCompany(Long contractId) {
		return bsCompanyDao.findCompany(contractId);
	}

	//定时任务 更新vip时常
	@Override
	public void doTask() throws ApplicationException {
		List<BsCompany> all = bsCompanyDao.findByDaysRemainingGreaterThan(0);
		for (int i = 0; i < all.size(); i++) {
			BsCompany company = all.get(i);
     if(company.getVipEndDate()!=null){
		 long between = DateUtil.between(company.getVipEndDate(), DateUtil.date(), DateUnit.DAY);
		 if (between==0){
			 company.setRate(new BigDecimal("0.0003"));
		 }
		 company.setDaysRemaining(NumberUtil.parseInt(between + ""));
		 save(company);
	 }

		}

	}

	@Override
	@ServerTransactional
	public void updateOnLineApplyFlg(Long companyId, Boolean onLineApplyFlg) {
		if (Objects.isNull(companyId) || Objects.isNull(onLineApplyFlg)) {
			return;
		}
		bsCompanyDao.updateOnLineApplyFlg(companyId, onLineApplyFlg);
	}

	@Override
	@ServerTransactional
	public void updateCreditQuote(CreditQuoteVo vo) {
		if (Objects.isNull(vo)) {
			return;
		}
		bsCompanyDao.updateCreditQuote(vo.getCompanyId(),vo.getTotalCreditAmount(),vo.getUsedCreditAmount()
				,vo.getCreditDays(),vo.getCreditType());
	}

	@Override
	@ServerTransactional
	public void updatePiccInfo(BsCompanyPiccRequestVo requestVo) {
		if (Objects.isNull(requestVo.getCompanyId()) ) {
			logger.error("updatePiccInfo companyId is null");
			return;
		}
		BsCompany company = bsCompanyDao.findOne(requestVo.getCompanyId());
		if (requestVo.getBaseQuota()!=null){
			company.setBaseQuota(requestVo.getBaseQuota());
		}
		if (requestVo.getApplyInsuranceStatus()!=null){
			company.setApplyInsuranceStatus(requestVo.getApplyInsuranceStatus());
		}
		if (requestVo.getCreditDays()!=null){
			company.setCreditDays(requestVo.getCreditDays());
		}
		if (requestVo.getPiccApprovalPeriod()!=null){
			company.setPiccApprovalPeriod(requestVo.getPiccApprovalPeriod());
		}
		if (requestVo.getPiccCreditAmount()!=null){
			company.setPiccCreditAmount(requestVo.getPiccCreditAmount());
		}
		if (requestVo.getApplyCreditAmount()!=null){
			company.setApplyCreditAmount(requestVo.getApplyCreditAmount());
		}
		if (requestVo.getPiccHaveusedAmount()!=null){
			company.setPiccHaveusedAmount(requestVo.getPiccHaveusedAmount());
		}
		if (requestVo.getPiccUseAbleaMount()!=null){
			company.setPiccUseAbleaMount(requestVo.getPiccUseAbleaMount());
		}
		if(requestVo.getPiccFlg()!=null){
			company.setPiccFlg(requestVo.getPiccFlg());
		}
		bsCompanyDao.save(company);

	}

	/**
	 * 人保授信额度为0的修改为黑名单
	 */
	@Override
	@ServerTransactional
	public void updateCreditRatingToBlack() {
		bsCompanyDao.updateCreditRatingToBlack();

	}

	/**
	 * 人保授信标识设为false额度置为0
	 */
	@Override
	@ServerTransactional
	public void updatePiccFlgToFalse() {
		bsCompanyDao.updatePiccFlgToFalse();

	}

	/**
	 * 查询企业CFCA已开通印章
	 * @param companyId
	 * @return
	 */
	@Override
	public SignSealVo findCompanyCfcaSeal(Long companyId) {
		SignSealVo sealVo = new SignSealVo();
		if (Objects.isNull(companyId)){
			return sealVo;
		}
		try {
			BsCompany bsCompany = this.getEntity(companyId);

			// 查询企业Cfca开户信息
			EnterpriseAccount account =  new EnterpriseAccount();
			account.setEnterpriseName(bsCompany.getCompanyName());
			EnterpriseAccount enterpriseAccount = signSealClient.search(account);
			// 查询企业cfca已开通印章
			if (Objects.isNull(enterpriseAccount)){
				return sealVo;
			}
			List<SignSeal> signSealList = signSealClient.getSignSeal(enterpriseAccount.getUserId());
			if (CollectionUtils.isNotEmpty(signSealList)){
				SignSeal officialSeal = signSealList.stream().filter(seal -> StringUtils.equals("OFC", seal.getSealType())).findFirst().orElse(null);
				SignSeal chapterContractSeal = signSealList.stream().filter(seal -> StringUtils.equals("CTR", seal.getSealType())).findFirst().orElse(null);
				SignSeal logisticsSeal = signSealList.stream().filter(seal -> StringUtils.equals("LGS", seal.getSealType())).findFirst().orElse(null);

				sealVo.setCompanyId(bsCompany.getId());
				sealVo.setCompanyName(bsCompany.getCompanyName());
				sealVo.setOfficialSealId(Objects.nonNull(officialSeal) ? officialSeal.getImageData() : "");
				sealVo.setChapterContractSealId(Objects.nonNull(chapterContractSeal) ? chapterContractSeal.getImageData() : "");
				sealVo.setLogisticsSealId(Objects.nonNull(logisticsSeal) ? logisticsSeal.getImageData() : "");
			}
		} catch (Exception e) {
			logger.error("findCompanyCfcaSeal error", e);
		}
		return sealVo;
	}

	/**
	 * 离职员工名下客户转移给各区域总
	 */
	@Override
	@ServerTransactional
	public void leaveReleasePublic() {
		logger.info("-----> 离职员工名下客户转移给各区域总任务Begin <------");
		UserSearchVo userSearchVo = new UserSearchVo(BasConstants.ZG_ENTERPRISE_ID, null);
		List<SysUserSdk> userList = authOpenFacade.findUserAll(userSearchVo);
		if (CollectionUtils.isEmpty(userList)) {
			logger.info("findDeptAll error，deptSdkList is empty！");
			return;
		}
		List<SysUserSdk> collect = userList.stream().filter(u -> StringUtils.equals("1", u.getStatus())).collect(Collectors.toList());
		collect.forEach(dept -> {
			SysDeptSdk dept1 = dept.getDept();
			final Long userId = dept.getUserId();
			final Date date = new Date();
			try {
				List<BsCompany> entityList = bsCompanyDao.findByMatchUserId(userId);
				entityList.forEach(entity-> addCompanyHis(entity,dept));
				bsCompanyDao.updateCompanyToLeader(dept1.getLeaderId(),dept1.getLeader(),userId,date);
			}catch (Exception e){
				logger.error("leaveReleasePublic error", e);
				logger.info("-----> 转移失败");
			}
		});
//		// 释放离职员工名下客户至公海
//		bsCompanyDao.updateLeaveReleasePublic(leaveUserIdList);
		logger.info("-----> 离职员工名下客户转移给各区域总任务End <------");
	}

	public void addCompanyHis(BsCompany entity,SysUserSdk sysUserSdk){
		//添加新增操作记录
		BsCompanyOphisVo opHis=new BsCompanyOphisVo();
		opHis.setCompanyId(entity.getId());
		opHis.setCreateUserId(entity.getCreateUserId());
		opHis.setCreateUserName(sysUserSdk.getNickName());
		opHis.setStatus(entity.getStatus());
		opHis.setOptionType(BasConstants.COMPANY_STATUS_S);
		opHis.setEnterpriseId(entity.getEnterpriseId());
		opHis.setOperation("2");
		opHis.setTargetName("企业管理");
		BsCompanyOphis his = new BsCompanyOphis();
		BeanUtils.copyProperties(opHis, his);
		SysDeptSdk dept = sysUserSdk.getDept();
		String remark="[离职]"+"["+sysUserSdk.getNickName()+"]"+"转入"+"["+dept.getLeader()+"]";
		his.setRemark(remark);
		bsCompanyOphisDao.save(his);
	}

	/**
	 * 修改人保申请状态
	 * @param vo
	 */
	@Override
	@ServerTransactional()
	public void updatePiccApplyStatus(CompanyStatusVo vo) {
		Long companyId = vo.getId();
		if (companyId != null && StringUtils.isNotBlank(vo.getPiccApplyStatus())) {
			bsCompanyDao.updatePiccApplyStatus(companyId,vo.getPiccApplyStatus());
		}
	}

	/**
	 * 修改人保申请状态
	 * @param vo
	 */
	@Override
	@ServerTransactional()
	public void updatePiccApplyStatusAndRemark(CompanyStatusVo vo) {
		Long companyId = vo.getId();
		if (companyId != null && StringUtils.isNotBlank(vo.getPiccApplyStatus())) {
			bsCompanyDao.updatePiccApplyStatusAndRemark(companyId, vo.getPiccApplyStatus(), vo.getRemark(),vo.getPiccApplyCreditAmountFlg());
		}
	}

	@Override
	@ServerTransactional
	public void syncCompanyBusinessExpansion() {
		List<CompanyBusinessExpansion> insertList = new ArrayList<>();
		List<CompanyBusinessExpansion> updateList = new ArrayList<>();
		List<CompanyBusinessExpansion> deleteList = new ArrayList<>();
		try {
			List<CompanyBusinessExpansion> businessExpansionList = companyBusinessExpansionService.findAll();
			Map<Long, CompanyBusinessExpansion> companyIDToExpansionMap = businessExpansionList.stream()
					.collect(Collectors.toMap(
							CompanyBusinessExpansion::getCompanyId, // 键：根据companyID获取
							expansion -> expansion // 值：直接使用CompanyBusinessExpansion对象
					));
			List<RptCompany> rptCompanyList = rptCompanyClient.selectAllRptCompany();
			if(CollectionUtils.isNotEmpty(rptCompanyList)) {
				for (RptCompany rptCompany : rptCompanyList) {
					if(Objects.isNull(rptCompany.getCompanyId())) {
						continue;
					}

					CompanyBusinessExpansion companyBusinessExpansion = new CompanyBusinessExpansion();
					BeanUtils.copyProperties(rptCompany,companyBusinessExpansion);
					CompanyBusinessExpansion businessExpansion = companyIDToExpansionMap.get(rptCompany.getCompanyId());
					if(Objects.nonNull(businessExpansion)) {
						companyBusinessExpansion.setId(businessExpansion.getId());
						updateList.add(companyBusinessExpansion);
					} else {
						insertList.add(companyBusinessExpansion);
					}
				}
			}
			List<RptSupplier> rptSuppliers = rptSupplierClient.selectAllRptSupplier();
			if(CollectionUtils.isNotEmpty(rptSuppliers)) {
				for (RptSupplier rptSupplier : rptSuppliers) {
					if(Objects.isNull(rptSupplier.getCompanyId())) {
						continue;
					}
					CompanyBusinessExpansion companyBusinessExpansion = new CompanyBusinessExpansion();
					BeanUtils.copyProperties(rptSupplier,companyBusinessExpansion);
					CompanyBusinessExpansion businessExpansion = companyIDToExpansionMap.get(rptSupplier.getCompanyId());
					if(Objects.nonNull(businessExpansion)) {
						companyBusinessExpansion.setId(businessExpansion.getId());
						companyBusinessExpansion.setCreatedDate(businessExpansion.getCreatedDate());
						updateList.add(companyBusinessExpansion);
					} else {
						companyBusinessExpansion.setCreatedDate(new Date());
						insertList.add(companyBusinessExpansion);
					}
				}
			}
			companyBusinessExpansionService.saveBatch(insertList,updateList,deleteList);

		} catch (ApplicationException e) {
			e.printStackTrace();
		}

	}

	@Override
	public BigDecimal getFinancialServiceRate(Long companyId) {
		BsCompany company = bsCompanyDao.findOne(companyId);
		List<ParamByCompanyGrade> paramByCompanyGradeList = getParamByCompanyGrade(BasConstants.ZG_ENTERPRISE_ID);
		if(Objects.nonNull(company) && CollectionUtils.isNotEmpty(paramByCompanyGradeList)) {
			for (ParamByCompanyGrade paramByCompanyGrade : paramByCompanyGradeList) {
				if(StringUtils.equals(company.getCompanyGrade(),paramByCompanyGrade.getCondition())) {
					return paramByCompanyGrade.getServeRate();
				}
			}
		}
		// 默认服务费费率
		return BigDecimal.valueOf(0.0003);
	}

	/**
	 * 获取根据客户等级费率配置项
	 */
	public List<ParamByCompanyGrade> getParamByCompanyGrade(Long enterpriseId) {
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.PARAM_BY_COMPANY_GRADE, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<List<ParamByCompanyGrade>> clazz = new TypeReference<List<ParamByCompanyGrade>>() {
			};
			return JsonUtil.json2Object(clazz, config.getConfigValue());
		}
		return null;
	}

	/**
	 * 恢复企业授信额度为人保批复额度
	 */
	@Override
	@ServerTransactional
	public void recoverCompanyCreditAmount() {
		bsCompanyDao.recoverCompanyCreditAmount();
	}
	
	
}
