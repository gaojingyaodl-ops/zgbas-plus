package com.spt.bas.server.service;

import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.vo.*;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IBsProductConfigService extends IBaseService<BsProductConfig> {

	/**
	 * 根据商品代码获取商品配置
	 **/
	BsProductConfigVo getConfigValue(String productCd, Long enterpriseId);

	/**
	 * 查询支付全款日期规则
	 */
	BsPayFullRuleVo getPayFullRule(Long companyId, Long enterpriseId);

	BsInvoiceConfig getBsInvoiceConfig(Long enterpriseId);

	/**
	 * 查询代采赊销中游合同服务费费率
	 */
	List<CalculateInsuranceRates> getDcsxInsurance(Long enterpriseId);

	List<ParamByCompanyGrade> getParamByCompanyGrade(Long enterpriseId);

	/**
	 * 默认利润计算参数
	 */
	CtrCalCulateParam findCtrCalculateParam(Long enterpriseId);

	/**
	 * 动态保费费率取值表达式
	 */
	List<CtrCalCulateInsuranceParam> findCtrCalculateInsuranceRates(Long enterpriseId);


	/**
	 * 批量发起付款申请配置项查询
	 * @param enterpriseId
	 * @return
	 */
	BatchPayApplyParam findBatchPayApplyParam(Long enterpriseId);

	/**
	 * 验证该合同是否可以批量发起付款申请
	 *
	 * @param contractId
	 * @return
	 */
	boolean verifyBatchPayApply(Long contractId);
}

