package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyShare;
import com.spt.bas.client.vo.*;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface IBsCompanyService extends IBaseService<BsCompany> {

	void updateFileId(Long id, String fileId);

	void updateScoreAndGrade(Long id, BigDecimal creditScore, String companyGrade);

	void updateSupplierScoreAndGrade(Long id, BigDecimal supplierScore, String supplierGrade);

	void updateCompanyStatus(CompanyStatusVo vo);

	void updatePiccInfo(Long id,BigDecimal piccCreditAmount,BigDecimal piccHaveusedAmount,BigDecimal piccUseAbleaMount);

	/**
	 * 更新私海客户没有成交单划入公海
	 */
	void updateStatusByTask();

	/**
	 * 自动归入供应商灰名单与终端工厂灰名单
	 * @param
	 * @return
	 */
	void updateGreyListByTask();

	/**
	 * 开户人id 刷新历史数据
	 */
	void updateOwnerOfAccountId();

	/**
	 * 超保额度到期自动恢复
	 */
	void recoverTotalCreditAmount();

	List<BsCompany> findByEnterpriseId(Long enterpriseId);
	
	List<BsCompany> findByEnterpriseIdAndCompanyType(Long enterpriseId,String companyType);

	List<BsCompany> findByCompanyName(String companyName, Long enterpriseId);

	Page<BsCompany> findPageCompnay(BsCompanySearchVo queryVo) throws ApplicationException;

	Page<BsCompanyVo> findPageCompnayVo(BsCompanySearchVo queryVo) throws ApplicationException;

	Page<BsCompanyVo> findPageCompnayVoExcel(BsCompanySearchVo queryVo) throws ApplicationException;

	BsCompanyShare shareCompany(BsCompanyShare vo) throws ApplicationException;

	CompanyAccountVo findCompanyAccountVo(Long companyId);

	BsCompany saveCompanyAccountVo(CompanyAccountVo accountVo) throws ApplicationException;

	void updateStatusByAssigned(CompanyStatusVo vo);

	List<BsCompany> getCompanyForDate(Long matchUserId);

	void updateByIds(Long[] condition);

	void refreshCompanyFlg(Long companyId);

	String getAddressFromUcs(String companyName);

	BsCompany getCompanyDetail(Long companyId);

	List<BsCompany> findByCompanyNames(String companyNames);

	BsCompany findByCompanyName(String companyName);

	List<BsCompany> findByContact(String phone);

	BsCompany findCompany(Long contractId);

	//vip定时更新剩余天数已经赊销率
	void doTask() throws ApplicationException;

	void updateOnLineApplyFlg(Long companyId, Boolean onLineApplyFlg);

	void updateCreditQuote(CreditQuoteVo vo);

	/**
	 * 修改人保信息
	 * */
	void updatePiccInfo(BsCompanyPiccRequestVo requestVo);

	/**
	 * 人保授信额度为0的修改为黑名单
	 */
	void updateCreditRatingToBlack();

	/**
	 * 人保授信标识设为false额度置为0
	 */
	void updatePiccFlgToFalse();

	SignSealVo findCompanyCfcaSeal(Long companyId);

	/**
	 * 离职员工名下客户转移给各区域总
	 */
	void leaveReleasePublic();

	void updatePiccApplyStatus(CompanyStatusVo vo);

	void updatePiccApplyStatusAndRemark(CompanyStatusVo vo);

	void syncCompanyBusinessExpansion();

	/**
	 * 根据企业ID获取金融服务费
	 * @return
	 */
	BigDecimal getFinancialServiceRate(Long companyId);

	/**
	 * 恢复企业授信额度为人保批复额度
	 */
	void recoverCompanyCreditAmount();
}
