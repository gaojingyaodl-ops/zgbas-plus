package com.spt.bas.server.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyMatch;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.server.service.ICtrProductService;
import com.spt.pm.service.IBsKeySequenceService;
import com.spt.tools.core.util.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author MoonLight
 */
public class BasBusinessUtil {
	private static final String DEFAULT_BUSINESS_PREFIX = "SPT";
	private static final String ZY_BUSINESS_PREFIX = "ZGZ";
	private static final String HG_PREFIX = "HG_";
	private static final String SL_PREFIX = "SL_";
	private static final IBsKeySequenceService keySequenceService = SpringContextHolder.getBean(IBsKeySequenceService.class);
	private static final ICtrProductService ctrProductService = SpringContextHolder.getBean(ICtrProductService.class);
	private static final Logger log = LoggerFactory.getLogger(BasBusinessUtil.class);

	public static String composeContractNoSuffix(Long enterpriseId) {
		return  keySequenceService.getNextKey(BasConstants.KEY_COMMON_NO_SUFFIX, enterpriseId);
	}

	public static String composeContractNo(Long enterpriseId, String deptAbbr, String applyType) {
		String suffix = keySequenceService.getNextKey(BasConstants.KEY_COMMON_NO_SUFFIX, enterpriseId, deptAbbr);
        return  DEFAULT_BUSINESS_PREFIX + applyType + suffix;
	}

	public static String composeVirtualContractNo(String contractNo, String virtualBuyType, String contractType) {
		return virtualBuyType + contractType + contractNo.replaceAll("\\D", "");
	}

	public static String composeContractNoZy(Long enterpriseId, String applyType) {
		if (applyType.equals(BasConstants.APPLY_TYPE_A) || applyType.equals(BasConstants.APPLY_TYPE_F)) {
			applyType = BasConstants.APPLY_TYPE_B;
		}
		if (applyType.equals(BasConstants.APPLY_TYPE_L)) {
			applyType = BasConstants.APPLY_TYPE_S;
		}
		String suffix = keySequenceService.getNextKey(BasConstants.KEY_COMMON_NO_SUFFIX, enterpriseId);
		return  ZY_BUSINESS_PREFIX + applyType + suffix;
	}

	public static String buildSellContractNo(String buyContractNo) {
		if (StringUtils.isBlank(buyContractNo)) {
			return "";
		}
		String replace = StrUtil.replace(buyContractNo, "SPTB", "SPTS");
		replace = StrUtil.replace(replace, "KCB", "KCS");
		return StrUtil.replace(replace, "XYB", "XYS");
	}

	public static String buildBuyContractNo(String sellContractNo) {
		if (StringUtils.isBlank(sellContractNo)) {
			return "";
		}
		String replace = StrUtil.replace(sellContractNo, "SPTS", "SPTB");
		replace = StrUtil.replace(replace, "KCS", "KCB");
		return StrUtil.replace(replace, "XYS", "XYB");
	}

	public static String buildSpecialFLKBuyContractNo(String buyContractNo) {
		if (StringUtils.isBlank(buyContractNo)) {
			return "";
		}
		String replace = StrUtil.replace(buyContractNo, "SPTB", "SPT1");
		replace = StrUtil.replace(replace, "KCB", "KC1");
		return StrUtil.replace(replace, "XYB", "XY1");
	}

	public static String buildMiddleToSell(String contractNo) {
		if (StringUtils.isBlank(contractNo)) {
			return "";
		}
		String replace = StrUtil.replace(contractNo, "SPTX", "SPTS");
		replace = StrUtil.replace(replace, "KCX", "KCS");
		return StrUtil.replace(replace, "XYX", "XYS");
	}

	public static boolean verifySpecialChainQGYS(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
		String ourCompanyName = match.getOurCompanyName();
		return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDGT, ourCompanyName);
	}

	public static boolean verifySpecialChainFLK(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
        return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_FLK, buyOurCompanyName);
    }

	public static boolean verifySpecialChainZJKR(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
		return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_ZJKR, buyOurCompanyName);
	}

	public static boolean verifySpecialChainQGSGX(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
		return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_QDZG, buyOurCompanyName);
	}

	public static boolean verifySpecialChainSHZG(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
		return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName) && StringUtils.equals(BasConstants.COMPANY_NAME_SHZG, buyOurCompanyName);
	}

	public static boolean verifySpecialChainZSNB(ApplyMatch match){
		if (Objects.isNull(match)){
			return false;
		}
		String buyOurCompanyName = match.getBuyOurCompanyName();
		String sellOurCompanyName = match.getSellOurCompanyName();
		return !StringUtils.equals(buyOurCompanyName, sellOurCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_ZJWS, buyOurCompanyName)
				&& StringUtils.equals(BasConstants.COMPANY_NAME_WSNB, sellOurCompanyName);
	}

	public static Map<String, Object> buildConditionDefaultMap(CtrContract contract) {
		Map<String, Object> resultMap = new HashMap<>();
		if (Objects.isNull(contract)) {
			return resultMap;
		}
		try {
			String contractNo = contract.getContractNo();
			Boolean matchCreditFlg = contract.getMatchCreditFlg();
			String businessType = contract.getBusinessType();
			log.info("buildConditionDefaultMap contractNo:{}", contractNo);
			List<CtrProduct> productList = ctrProductService.findByContractId(contract.getId());
			if (CollectionUtils.isNotEmpty(productList)) {
				// 化工标识
				resultMap.put("hgFlag", productList.stream().anyMatch(p -> p.getProductCd().startsWith(HG_PREFIX)));
				// 塑料标识
				resultMap.put("slFlag", productList.stream().anyMatch(p -> p.getProductCd().startsWith(SL_PREFIX)));
			}
			// 合同类型
			resultMap.put("contractType", contract.getContractType());
			// 自营标识
			resultMap.put("zyFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_CG, businessType)
					|| StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_XS, businessType));
			// 代采标识
			resultMap.put("dcFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.FALSE.equals(matchCreditFlg));
			// 赊销标识
			resultMap.put("sxFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType) && Boolean.TRUE.equals(matchCreditFlg));
			// 托盘标识
			resultMap.put("tpFlag", StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_TP, businessType));
			// 协议合同标识
			resultMap.put("xyFlag", contractNo.startsWith(BasConstants.STOCK_VIRTUAL_XY));
			// 库存合同标识
			resultMap.put("kcFlag", contractNo.startsWith(BasConstants.STOCK_VIRTUAL_KC));
			// KUB库存采购合同标识
			resultMap.put("kubFlag", contractNo.startsWith("KUB"));
			// KUX库存采购合同标识
			resultMap.put("kuxFlag", contractNo.startsWith("KUX"));
			// 合同业务员部门ID
			resultMap.put("matchDeptId", contract.getDeptId());
		} catch (Exception e) {
			log.error("buildConditionDefaultMap error", e);
		}
		return resultMap;
	}

	public static Date offsetDayOneSeconds(Date targetDate, int offDays) {
		return DateUtil.offsetDay(targetDate, offDays).offset(DateField.HOUR, 23).offset(DateField.MINUTE, 59).offset(DateField.SECOND, 59);
	}

	public static Date getMaxDate(Date date1, Date date2){
		if (date1 == null && date2 == null) {
			return null;
		}
		if (date1 == null) {
			return date2;
		}
		if (date2 == null) {
			return date1;
		}
		return (date1.after(date2)) ? date1 : date2;
	}
}
