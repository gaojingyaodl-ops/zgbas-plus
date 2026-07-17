package com.spt.bas.report.server.service.impl;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import com.spt.bas.report.server.dao.RptCreditAmountMonitorMapper;
import com.spt.bas.report.server.service.IRptCreditAmountMonitorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
public class RptCreditAmountMonitorServiceImpl implements IRptCreditAmountMonitorService {
	@Autowired
	private RptCreditAmountMonitorMapper creditAmountMonitorMapper;
	
	@Override
	public Page<RptCreditAmountMonitor> findCreditAmountMonitorPage(RptCreditAmountMonitorSearchVo searchVo) {
		
		List<RptCreditAmountMonitor> creditAmountMonitorList = creditAmountMonitorMapper.findCreditAmountMonitorList(searchVo);
		if(CollectionUtils.isNotEmpty(creditAmountMonitorList)) {
			for (RptCreditAmountMonitor creditAmountMonitor : creditAmountMonitorList) {
				BigDecimal piccCreditAmount = creditAmountMonitor.getPiccCreditAmount();
				BigDecimal receiveAmount = creditAmountMonitor.getReceiveAmount();
				if(piccCreditAmount != null && piccCreditAmount.compareTo(BigDecimal.ZERO) == 0 
						&& receiveAmount != null && receiveAmount.compareTo(BigDecimal.ZERO) > 0) {
					creditAmountMonitor.setExcessRate(new BigDecimal("100"));
				}
				creditAmountMonitor.setCreditInfo(handelCreditInfo(creditAmountMonitor.getCompanyCreditList()));
			}
		}

		Pageable pageable = PageRequest.of(searchVo.getPage() - 1, searchVo.getRows());
		Page<RptCreditAmountMonitor> pageVo = new PageImpl<>(creditAmountMonitorList, pageable, searchVo.getCount());
		return pageVo;
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
				if (daDiFlg||piccFlg||zyFlg) {
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
		BigDecimal creditAmount = companyCredit.getCreditAmount();
		BigDecimal creditAmountW = companyCredit.getCreditAmount().divide(new BigDecimal(10000));
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

}
