package com.spt.bas.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyFollow;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.CompanyStatusVo;
import com.spt.bas.server.annotation.ServerTransactional;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.server.dao.BsCompanyFollowDao;
import com.spt.bas.server.dao.CtrContractDao;
import com.spt.bas.server.service.IBsCompanyManageService;
import com.spt.bas.server.service.IBsCompanyShareService;
import com.spt.bas.server.util.BsCompanyLogUtil;
import com.spt.bas.server.util.DeptUtils;
import com.spt.pm.constant.PmConstants;
import com.spt.tools.core.date.DateOperator;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.persistence.WebUtil;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 企业管理实现类
 */
@Component
@Transactional(readOnly = true)
public class BsCompanyManageServiceImpl extends BaseService<BsCompany> implements IBsCompanyManageService {
	@Autowired
	private BsCompanyDao bsCompanyDao;
	@Autowired
	private BsCompanyFollowDao bsCompanyFollowDao;
	@Autowired
	private CtrContractDao ctrContractDao;
	@Autowired
	private IAuthOpenFacade authOpenFacade;
	@Resource
	private DeptUtils deptUtils;
	@Autowired
	private IBsCompanyShareService bsCompanyShareService;


	@Override
	public BaseDao<BsCompany> getBaseDao() {
		return bsCompanyDao;
	}

	@Override
	public Class<BsCompany> getEntityClazz() {
		return BsCompany.class;
	}


	/**
	 *
	 * 1. 客户保留时间规则：
	 *   a. 自己开发的客户自动领用，保留时间 60 天
	 *   b. 领用他人客户后，保留时间 7 天
	 *   c. 维护跟进记录（内容长度≥15个字），刷新保留时间 7 天
	 *   d. 成交后，保留时间重新计算 60 天
	 *   e. 领用人离职后，所有私海客户自动释放
	 *
	 * 2. 客户释放优先级（客户保留时间到期自动释放）：
	 *   a. 如果被共享人近2个月有成交，直接释放给最近成交的共享人
	 *   b. 释放到公海
	 *
	 * 	250819-需求ID ：1001899
	 */
	@Override
	public void updateStatusByTask(String companyName) {
		// 定义线程池
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		List<Future<Void>> futures = new ArrayList<>();
		// 查询私海客户
		String updateStep = "";
		Map<String, Object> map = new HashMap<>();
		Date nowDate = new Date();
		Date compareDate = DateOperator.addDays(nowDate, -60);
		map.put("EQS_status",BasConstants.COMPANY_STATUS_F);
		if (StringUtils.isNotBlank(companyName)) {
			map.put("EQS_companyName",companyName);
		}
		Specification<BsCompany> spec = WebUtil.buildSpecification(map);
		List<BsCompany> companyList = bsCompanyDao.findAll(spec);
		
		// 获取不退回公海企业的所属领用人集合
		List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_GRAY);
		// 获取不退回公海企业的所属部门集合
		List<BsDictData> deptListByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_GRAY_DEPT_ID);
		// 自己开发  保留60天：根据数据字典获取保留的天数
		Long own =Long.valueOf(BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HIGH_SEAS_RETURN_TIME, BasConstants.OWN));
		// 非自己开发 保留7天：根据数据字典获取保留的天数
		Long notOwn =Long.valueOf( BsDictUtil.getValue(BasConstants.ZG_ENTERPRISE_ID, BasConstants.HIGH_SEAS_RETURN_TIME, BasConstants.NOT_OWN));
		for (BsCompany bc : companyList) {
			Future<Void> future = executorService.submit(() -> {
				try {
					processCompany(bc, nowDate, own, notOwn, updateStep, compareDate, listByCategory, deptListByCategory);
					return null;
				} catch (Exception e) {
					logger.error("线程释放私海企业失败：{},异常信息：{}",bc.getCompanyName(),e);
				}
				return null;
			});
			futures.add(future);
		}
		// 等待所有任务完成
		for (Future<Void> future : futures) {
			try {
				future.get(); // 等待每个任务的完成
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Error processing company: ", e);
			}
		}

		// 关闭线程池
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
				executorService.shutdownNow(); // 如果等待超时，强制关闭线程池
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		}

	}


	@Transactional
	public void processCompany(BsCompany bc, Date nowDate, Long own, Long notOwn, String updateStep, Date compareDate,List<BsDictData> listByCategory, List<BsDictData> deptListByCategory) {
		long count = 0;
		long count1 = 0;
		// 开户人id ownerOfAccountId
		Long ownerOfAccountId = bc.getOwnerOfAccountId();
		// 领用人id matchUserId
		Long matchUserId = bc.getMatchUserId();
		if (matchUserId == null) {
			matchUserId = 0L;
		}

		//过滤该企业部门是否配置在特权字典里
		String currCompanyDeptIdStr = bc.getDeptId() == null ? "" : bc.getDeptId().toString();
		boolean exists = deptListByCategory.stream().anyMatch(p -> StringUtils.equals(currCompanyDeptIdStr, p.getDictCd()));
		if (exists) {
			return;
		}
		
		SysUserSdk matchUser = authOpenFacade.findUserById(matchUserId);
		// 离职标识
		Boolean departFlg = false;
		if (Objects.nonNull(matchUser)) {
			if (!StringUtils.equals("0", matchUser.getStatus()) || !StringUtils.equals("0", matchUser.getDelFlag())) {
				// 账号已停用
				departFlg = true;
			}
		} else {
			// 账号不存在
			departFlg = true;
		}

		// 塑料分类：热性塑料（PS） 不自动掉入公海 且 领用人未离职
		if (StringUtils.equals("PS", bc.getPlasticType()) && !departFlg) {
			return;
		}

		// 领用时间
		Date matchFllowDateCompare = bc.getMatchFllowDate();
		
		boolean updateFlg = false;
		logger.info("定时任务1：companyName:{},companyId:{}", bc.getCompanyName(), bc.getId(), bc.getMatchUserId());
		// 查询该私海客户是否有产生交易
		List<CtrContract> ctrContracts = ctrContractDao.findByCompanyId(bc.getId());
		// 是否自己开发客户
		Boolean isSelfDevelop = false;
		// 离职人员需要释放，不考虑保留条件，非离职人员才考虑私海保留逻辑
		if (!departFlg) {
			if (ownerOfAccountId != null && matchUserId != null && ownerOfAccountId.equals(matchUserId)) {
				// 自己开发
				isSelfDevelop = true;
				if (matchFllowDateCompare != null && DateUtil.between(matchFllowDateCompare, nowDate, DateUnit.DAY) <= own) {
					// 自己开发的客户自动领用，保留时间 60 天
					return;
				}
			} else {
				// 非自己开发
				if (matchFllowDateCompare != null && DateUtil.between(matchFllowDateCompare, nowDate, DateUnit.DAY) <= notOwn) {
					// 领用他人客户后，保留时间 7 天
					return;
				}
			}

			// 维护跟进记录（内容长度≥15个字），刷新保留时间 7 天
			BsCompanyFollow companyFollow = bsCompanyFollowDao.findTopByCompanyIdAndCreateUserIdOrderByCreatedDateDesc(bc.getId(), matchUserId);
			if (Objects.nonNull(companyFollow) && DateUtil.between(companyFollow.getCreatedDate(), nowDate, DateUnit.DAY) <= notOwn) {
				String content = companyFollow.getContent();
				Long createUserId = companyFollow.getCreateUserId();
				// 判断跟进记录是否当前领用人并且维护长度大于15
				if (matchUserId != null && createUserId != null && matchUserId.equals(createUserId)
						&& StringUtils.isNotBlank(content) && content.length() >= 15) {
					return;
				}
			}

			if (ctrContracts.isEmpty() && isSelfDevelop) {
				// 自己开发的客户，当前客户没有交易记录，判断领入时间是否超过60天，超过60天重新归入公海
				if (matchFllowDateCompare == null || DateUtil.between(matchFllowDateCompare, nowDate, DateUnit.DAY) > own) {
					updateFlg = true;
					updateStep = "step1";
				}
			} else if (ctrContracts.isEmpty() && !isSelfDevelop) {
				// 非自己开发的客户，当前客户没有交易记录，判断领入时间是否超过7天，超过7天重新归入公海
				if (matchFllowDateCompare == null || DateUtil.between(matchFllowDateCompare, nowDate, DateUnit.DAY) > notOwn) {
					updateFlg = true;
					updateStep = "step2";
				}
			} else {
				List<Long> sellContractIds = ctrContracts.stream().filter(a -> a.getMatchUserId() != null && a.getMatchUserId().equals(bc.getMatchUserId()))
						.filter(a -> BasConstants.CONTRACT_STATUS_S.equals(a.getContractType()))
						.map(a -> a.getId()).collect(Collectors.toList());
				List<Long> buyContractIds = ctrContracts.stream().filter(a -> a.getMatchUserId() != null && a.getMatchUserId().equals(bc.getMatchUserId()))
						.filter(a -> BasConstants.CONTRACT_STATUS_B.equals(a.getContractType()))
						.map(a -> a.getId()).collect(Collectors.toList());

				logger.info("sellContractIds:{}", sellContractIds);
				Specification<CtrContract> spec1;
				Specification<CtrContract> spec2;
				if (!sellContractIds.isEmpty()) {
					Map<String, Object> bcMap = new HashMap<>();
					bcMap.put("INL_id", sellContractIds);
					bcMap.put("GTED_contractTime", compareDate);
					spec1 = WebUtil.buildSpecification(bcMap);
					count = ctrContractDao.count(spec1);
				}
				if (!buyContractIds.isEmpty()) {
					Map<String, Object> bcMap1 = new HashMap<>();
					bcMap1.put("INL_id", buyContractIds);
					bcMap1.put("GTED_contractTime", compareDate);
					spec2 = WebUtil.buildSpecification(bcMap1);
					count1 = ctrContractDao.count(spec2);
				}
				logger.info("count:{}", count + count1);
				if (count + count1 <= 0) {
					updateFlg = true;
					updateStep = "step3";
				}
			}
		} else {
			updateFlg = true;
			updateStep = "step4";
		}
		logger.info("updateFlg:{}", updateFlg);

		//过滤该企业领用人是否配置在特权字典里
		List<BsDictData> collect = listByCategory.stream().filter(p -> bc.getMatchUserId()!=null && p.getDictCd().equals(bc.getMatchUserId().toString())).collect(Collectors.toList());
		if (updateFlg && CollectionUtil.isEmpty(collect)) {
			releaseCompany(bc, updateStep, ctrContracts, compareDate);
		}
	}
	/**
	 * 释放企业逻辑
	 */
	@Transactional
	public void releaseCompany(BsCompany bc, String updateStep,List<CtrContract> ctrContracts,
							   Date compareDate) {
		Boolean updateFlg = false;
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
			if (Objects.nonNull(sysUser) && StringUtils.equals("0", sysUser.getStatus()) && StringUtils.equals("0", sysUser.getDelFlag())) {
				vo.setStatus(BasConstants.COMPANY_STATUS_F);
				vo.setCreateUserId(ctrContract.getMatchUserId());
				handelDeptId(vo, sysUser);
				updateFlg = true;
				logger.info("企业:{}共享人近2个月有成交，直接释放给最近成交的共享人:{}", bc.getCompanyName(), ctrContract.getMatchUserName());
			}
		}
		if (StringUtils.equals(BasConstants.COMPANY_STATUS_N, vo.getStatus())) {
			if (bc.getOwnerOfAccountId() != null) {
				SysUserSdk user = authOpenFacade.findUserById(bc.getOwnerOfAccountId());
				// 设置deptId
				handelDeptId(vo, user);
			}
			updateFlg = true;
		}
		if (updateFlg) {
			updateCompanyStatus(vo);
		}
	}

	public void handelDeptId(CompanyStatusVo vo, SysUserSdk user) {
		if (Objects.nonNull(user) && user.getDeptId() != null) {
			Long deptId;
			SysDeptSdk dept = authOpenFacade.findDeptById(user.getDeptId());
			if (Objects.nonNull(dept)) {
				// 特殊处理 如果是离职人员部门 不修改 部门ID
				if (!BasConstants.LZ_PERSON_DEPT_ID.equals(dept.getDeptId())) {
					if (StringUtils.equals(BasConstants.DEPTTYPE_TEAM, dept.getDeptType())) {
						deptId = dept.getParentId();
					} else {
						deptId = dept.getDeptId();
					}
					if (deptId != null) {
						vo.setDeptId(deptId);
					}
				} else {
//					deptId = null;
				}
			} else {
				deptId = user.getDeptId();
				if (deptId != null) {
					vo.setDeptId(deptId);
				}
			}
		} else {
//			vo.setDeptId(null);
		}
	}

	@ServerTransactional()
	public void updateCompanyStatus(CompanyStatusVo vo) {
		Long companyId = vo.getId();
		BsCompany oldCompany = new BsCompany();
		if (companyId != null) {
			BsCompany company = bsCompanyDao.findOne(companyId);
			BeanUtils.copyProperties(company,oldCompany);
			company.setStatus(vo.getStatus());
			if (vo.getDeptId() != null) {
				company.setDeptId(vo.getDeptId());
			}
			if (BasConstants.COMPANY_STATUS_F.equals(vo.getStatus())) {
				company.setMatchUserId(vo.getCreateUserId());
				company.setMatchFllowDate(new Date());
				if(vo.getCreateUserId() != null){
					SysDeptSdk deptSdk = deptUtils.getDeptByUserIdAndDeptType(vo.getCreateUserId(), PmConstants.NODE_TYPE_DEPT);
					company.setMatchUserDeptId(Objects.nonNull(deptSdk) ? deptSdk.getDeptId() : null);
				}
			}
			//退回公海用户,领用人和时间为空
			if (BasConstants.COMPANY_STATUS_N.equals(vo.getStatus())) {
				company.setMatchUserId(null);
				company.setMatchUserDeptId(null);
				company.setMatchFllowDate(null);

				// 释放到公海 删除共享列表
				bsCompanyShareService.deleteCompanyShare(companyId);
			}
			bsCompanyDao.save(company);
			
			// 退回 领用 历史记录
			BsCompanyLogUtil.saveOrUpdate(null,oldCompany,company,company.getId(),vo.getCreateUserId(),
					vo.getCreateUserName(),company.getId(),vo.getStatus(),null);
		}
	}
	
}
