package com.spt.bas.server.dao;

import com.spt.bas.client.entity.BsCompany;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface BsCompanyDao extends BaseDao<BsCompany> {
	@Transactional
	@Modifying
	@Query("update BsCompany c set c.ownerOfAccountId =?2 where c.id=?1 ")
	void updateOwnerOfAccountId(Long id, Long ownerOfAccountId);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.fileId =?2 where c.id=?1 ")
	void updateFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.totalCreditAmount =?2,c.totalTemporaryAmount =null where c.id=?1 ")
	void updateTotalCreditAmountById(Long id, BigDecimal totalCreditAmount);


	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update t_bs_company c set c.credit_score =?2,c.company_grade=?3,c.updated_date = now() where c.id=?1")
	void updateScoreAndGrade(Long id, BigDecimal creditScore,String companyGrade);

	@Transactional
	@Modifying
	@Query("update BsCompany bc set bc.supplierScore =?2,bc.supplierGrade =?3 where bc.id =?1")
	void updateSupplierScoreAndGrade(Long id, BigDecimal supplierScore,String supplierGrade);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.totalCreditAmount =?2,c.usedCreditAmount =?3,c.creditDays=?4,c.creditType=?5 where c.id=?1 ")
	void updateCreditQuote(Long id,BigDecimal totalCreditAmount,BigDecimal usedCreditAmount ,Long creditDays, String creditType);

	@Transactional
	@Modifying
	@Query("update BsCompany bc set bc.piccCreditAmount =?2,bc.piccHaveusedAmount =?3,bc.piccUseAbleaMount =?4 where bc.id =?1")
	void updatePiccInfo(Long id,BigDecimal piccCreditAmount,BigDecimal piccHaveusedAmount,BigDecimal piccUseAbleaMount);

	@Transactional
	@Modifying
	@Query("update BsCompany bc set bc.companyCreditNo=?2,bc.licenseNumber=?2,bc.orgNo=?3,bc.regNo=?4,bc.operationStatus=?5," +
			"bc.startDate=?6,bc.registerDate=?6," +
			"bc.econKind=?7,bc.termStart=?8,bc.termEnd=?9,bc.legalRepresent=?10,bc.checkDate=?11,bc.registerCapital=?12," +
			"bc.belongOrg=?13,bc.address=?14,bc.provinceCode=?15,bc.companyUrl=?16,bc.email=?17,bc.companyPhone=?18," +
			"bc.scope=?19,bc.logoUrl=?20,bc.lastUpdateTime=?21,bc.sourceReg=?22,bc.industryReg=?23,bc.historyNames=?24 " +
			"where bc.id =?1")
	void updateCompanyBasicInfo(Long id,String companyCreditNo,String orgNo,String regNo,String operationStatus,
								String startDate,String econKind,String termStart,String termEnd,String legalRepresent,
								String checkDate,String registerCapital,String belongOrg,String address,String provinceCode,
								String companyUrl,String email,String companyPhone,String scope,String logoUrl,
								String lastUpdateTime,String sourceReg,String industryReg,String historyNames);

	@Query("from BsCompany where enterpriseId = ?1 and enableFlg=true")
	List<BsCompany> findByEnterpriseId(Long enterpriseId);
	
	@Query("from BsCompany where enterpriseId = ?1 and companyType = ?2 and enableFlg=true")
	List<BsCompany>findByEnterpriseIdAndCompanyType(Long enterpriseId,String companyType);

	@Query(" from BsCompany c where c.enableFlg = true and c.companyName =?1 and c.enterpriseId = ?2")
	List<BsCompany> queryCompanyName(String companyName, Long enterpriseId);

	@Query(" from BsCompany c where c.companyName =?1 and c.enterpriseId = 44 and c.enableFlg = true")
	List<BsCompany> findByCompanyName(String companyName);

	@Query("from BsCompany c where c.matchUserId =?1 and DateDiff(c.matchFllowDate,NOW())=0 and c.status ='F' and enableFlg = true")
	List<BsCompany> getCompanyForDate(Long matchUserId);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.enableFlg=true where c.id in ?1")
	void updateByIds(Long[] condition);

	@Query(" from BsCompany c where c.companyName in ?1 and c.enterpriseId = 44 and c.enableFlg = true")
	List<BsCompany> findByCompanyNameIn(List<String> companyNames);

	List<BsCompany> findByContactPhoneAndEnterpriseIdAndEnableFlgTrue(String phone, Long EnterpriseId);


	@Query(nativeQuery = true, value="SELECT *  FROM `t_bs_company` WHERE id=?1 ")
	BsCompany findCompany(Long contractId);

	List<BsCompany> findByDaysRemainingGreaterThan(int day);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.onlineApplyFlg =?2 where c.id in ?1")
    void updateOnLineApplyFlg(Long id, Boolean onlineApplyFlg);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccCreditAmount=?2,c.piccApprovalPeriod=?3,c.applyInsuranceStatus=?4,c.piccLimitEffectDate =?5,c.piccLimitLapseDate =?6,c.piccCompensationRatio=?7,c.piccFlg=?8,c.piccApplyStatus =?9,c.piccThisUpdateFlg=?10,c.piccCode=?11,c.creditCategory=?12,c.piccHaveusedAmount=?13,c.piccUseAbleaMount=?14,c.piccApproveDate=?15 where c.id=?1 ")
	void updatePiccDataNew(Long id, BigDecimal piccCreditAmount, Integer piccApprovalPeriod, String applyInsuranceStatus, Date piccLimitEffectDate, Date piccLimitLapseDate,BigDecimal piccCompensationRatio, Boolean piccFlg, String piccApplyStatus, Boolean piccThisUpdateFlg,String piccCode, String creditCategory,BigDecimal piccHaveusedAmount, BigDecimal piccUseAbleaMount, Date piccApproveDate);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.daDiCreditAmount=?2,c.creditCategory=?3 where c.id=?1 ")
	void updateDaDiAmount(Long id, BigDecimal daDiCreditAmount, String creditCategory);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccCreditAmount=?2,c.piccApprovalPeriod=?3,c.piccLimitEffectDate =?4,c.piccLimitLapseDate =?5,c.piccCompensationRatio=?6,c.piccFlg=?7, c.piccApplyStatus =?8,c.piccThisUpdateFlg=?9,c.piccCode=?10,c.creditCategory=?11,c.piccHaveusedAmount=?12,c.piccUseAbleaMount=?13,c.piccApproveDate=?14 where c.id =?1")
	void updatePiccApplyStatusAndPiccFlgToBlack(Long companyId, BigDecimal piccCreditAmount, Integer piccApprovalPeriod, Date piccLimitEffectDate, Date piccLimitLapseDate,BigDecimal piccCompensationRatio, Boolean piccFlg, String piccApplyStatus, Boolean piccThisUpdateFlg,String piccCode, String creditCategory, BigDecimal piccHaveusedAmount, BigDecimal piccUseAbleaMount, Date piccApproveDate);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.creditRating ='B' where c.piccFlg=true and c.piccCreditAmount=0")
	void updateCreditRatingToBlack();

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccFlg =false,c.piccCreditAmount=0 where c.piccFlg=true")
	void updatePiccFlgToFalse();

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.approveCreditAmount =?2 where c.id =?1")
	void updateApproveCreditAmount(Long companyId, BigDecimal approveCreditAmount);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.usedCreditAmount =?2 where c.id =?1")
	void updateUsedCreditAmount(Long companyId, BigDecimal usedCreditAmount);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.usedCreditAmount =?2, c.approveCreditAmount =?3 where c.id =?1")
	void updateCreditAmount(Long companyId, BigDecimal usedCreditAmount, BigDecimal approveCreditAmount);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.matchUserId = ?1,c.matchUserName = ?2,c.status = 'F',c.matchFllowDate = ?4 where c.matchUserId in ?3")
	void updateCompanyToLeader(Long id,String name,Long matchId,Date date);

	List<BsCompany> findByMatchUserId(Long matchId);

	@Query("select count(*) from BsCompany")
    Integer selectAllCount();

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccApplyStatus =?2 where c.id =?1")
	void updatePiccApplyStatus(Long companyId, String piccApplyStatus);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccApplyStatus =?2, c.remark=?3,c.piccApplyCreditAmountFlg=?4 where c.id =?1")
	void updatePiccApplyStatusAndRemark(Long companyId, String piccApplyStatus, String remark,String piccApplyCreditAmountFlg);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.freedToDeptLeaderCount =?1 where c.id=?2")
	void updateFreedToDeptLeaderCount(Integer freedToDeptLeaderCount,Long companyId);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccFlg =?1,c.piccCreditAmount=0 where c.piccThisUpdateFlg=false")
	void updatePiccFlgByPiccThisUpdateFlg(Boolean piccFlg);

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.piccThisUpdateFlg=false")
	void updatePiccThisUpdateFlg();

	@Transactional
	@Modifying
	@Query("update BsCompany c set c.totalCreditAmount = c.piccCreditAmount where c.totalCreditAmount >= 0 or c.piccCreditAmount >= 0")
	void recoverCompanyCreditAmount();
}
