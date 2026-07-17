package com.spt.bas.server.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsProductConfig;
import com.spt.bas.client.entity.CtrContractApply;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.vo.*;
import com.spt.bas.server.dao.BsProductConfigDao;
import com.spt.bas.server.service.IBsProductConfigService;
import com.spt.bas.server.service.ICtrContractApplyService;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class BsProductConfigServiceImpl extends BaseService<BsProductConfig> implements IBsProductConfigService {
	@Autowired
	private BsProductConfigDao bsProductConfigDao;
	@Autowired
	private ICtrProductService ctrProductService;
	@Autowired
	private ICtrContractApplyService ctrContractApplyService;
	
	@Override
	public BaseDao<BsProductConfig> getBaseDao() {
		return bsProductConfigDao;
	}
	
	@Override
	public Class<BsProductConfig> getEntityClazz() {
		return BsProductConfig.class;
	}
	
	@Override
	public BsProductConfigVo getConfigValue(String productCd, Long enterpriseId) {
		if (StringUtils.isNotBlank(productCd)) {
			String configKey = "";
			if(productCd.startsWith("HG_OT_WH")){
				//万华-配置项
				configKey = BasConstants.CONFIG_KEY_FS_WH;
			}else if (productCd.startsWith(BasConstants.PRODUCT_INDUSTRY_SL)) {
				//塑料-配置项
				configKey = BasConstants.CONFIG_KEY_FS_SL;
			}else if(productCd.startsWith(BasConstants.PRODUCT_INDUSTRY_HG)) {
				//化工-配置项
				configKey = BasConstants.CONFIG_KEY_FS_HG;
			}else {
				//默认-配置项
				configKey = BasConstants.CONFIG_KEY_FS_DF;
			}
			BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(configKey, enterpriseId);
			if (config != null){
				BsProductConfigVo configVo = JsonUtil.json2Object(BsProductConfigVo.class, config.getConfigValue());
				return configVo;
			}
		}
		return null;
	}

	/**
	 * 查询支付全款日期规则
	 * @param companyId
	 * @param enterpriseId
	 * @return
	 */
	@Override
	public BsPayFullRuleVo getPayFullRule(Long companyId, Long enterpriseId) {
		if (Objects.isNull(companyId) || Objects.isNull(enterpriseId)) {
			return null;
		}
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.CONFIG_KEY_PAY_FULL_RULE, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<List<BsPayFullRuleVo>> reference = new TypeReference<List<BsPayFullRuleVo>>() {
			};
			List<BsPayFullRuleVo> ruleList = JsonUtil.json2Object(reference, config.getConfigValue());
			if (CollectionUtils.isNotEmpty(ruleList)) {
				return ruleList.stream().filter(rule -> companyId.equals(rule.getCompanyId())).findFirst().orElse(null);
			}
		}
		return null;
	}

	/**
	 * 查询乙二醇自营业务自动发起开收票配置
	 * @param enterpriseId
	 * @return
	 */
	@Override
	public BsInvoiceConfig getBsInvoiceConfig(Long enterpriseId) {
		if (Objects.isNull(enterpriseId)) {
			return null;
		}
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.AUTO_INVOICE_CONFIG, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<BsInvoiceConfig> reference = new TypeReference<BsInvoiceConfig>() {
			};
			return JsonUtil.json2Object(reference, config.getConfigValue());
		}
		return null;
	}

	/**
	 * 查询代采赊销中游合同服务费费率
	 * @return
	 */
	@Override
	public List<CalculateInsuranceRates> getDcsxInsurance(Long enterpriseId) {
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.CONFIG_KEY_DCSX_INSURANCE_RATE_PARAM, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<List<CalculateInsuranceRates>> clazz = new TypeReference<List<CalculateInsuranceRates>>() {
			};
			return JsonUtil.json2Object(clazz, config.getConfigValue());
		}
		return null;
	}

	/**
	 * 根据企业等级获取服务费率、违约费率
	 * @return
	 */
	@Override
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
	 * 查询提成计算默认参数
	 * @return
	 */
	@Override
	public CtrCalCulateParam findCtrCalculateParam(Long enterpriseId) {
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.CALCULATE_CONFIG_KEY, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<CtrCalCulateParam> clazz = new TypeReference<CtrCalCulateParam>() {
			};
			return JsonUtil.json2Object(clazz, config.getConfigValue());
		}
		return null;
	}

	/**
	 * 动态保费费率取值表达式
	 * @return
	 */
	@Override
	public List<CtrCalCulateInsuranceParam> findCtrCalculateInsuranceRates(Long enterpriseId) {
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.CALCUALTE_INSURANCE_RATE_KEY, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<List<CtrCalCulateInsuranceParam>> clazz = new TypeReference<List<CtrCalCulateInsuranceParam>>() {
			};
			return JsonUtil.json2Object(clazz, config.getConfigValue());
		}
		return null;
	}

	@Override
	public BatchPayApplyParam findBatchPayApplyParam(Long enterpriseId) {
		BsProductConfig config = bsProductConfigDao.findByConfigKeyAndEnterpriseId(BasConstants.BATCH_PAY_APPLY_CONFIG, enterpriseId);
		if (Objects.nonNull(config) && StringUtils.isNotBlank(config.getConfigValue())) {
			TypeReference<BatchPayApplyParam> clazz = new TypeReference<BatchPayApplyParam>() {
			};
			return JsonUtil.json2Object(clazz, config.getConfigValue());
		}
		return null;
	}

	/**
	 * 验证该合同是否可以批量发起付款申请
	 *
	 * @param contractId
	 * @return
	 */
	@Override
	public boolean verifyBatchPayApply(Long contractId) {
		if (Objects.isNull(contractId) || contractId == 0L) {
			return false;
		}
		List<CtrProduct> productList = ctrProductService.findByContractId(contractId);
		if (CollectionUtils.isEmpty(productList) || productList.size() > 1) {
			return false;
		}
		CtrContractApply contractApply = ctrContractApplyService.findByContractId(contractId);
		if (Objects.isNull(contractApply) || contractApply.getApplyPayAmount().compareTo(BigDecimal.ZERO) > 0){
			return false;
		}
		CtrProduct ctrProduct = productList.get(0);
		BigDecimal dealNumber = ctrProduct.getDealNumber();
		String productCd = ctrProduct.getProductCd();
		Long enterpriseId = ctrProduct.getEnterpriseId();
		BatchPayApplyParam param = findBatchPayApplyParam(enterpriseId);
		if (Objects.isNull(param)) {
			return false;
		}
		BigDecimal batchNumber = param.getBatchNumber();
		List<String> batchProductCd = param.getBatchProductCd();
		if (dealNumber.compareTo(batchNumber) > 0 && batchProductCd.contains(productCd)) {
			return true;
		}
		return false;
	}
}

