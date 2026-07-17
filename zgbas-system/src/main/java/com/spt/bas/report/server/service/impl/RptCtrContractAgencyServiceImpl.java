package com.spt.bas.report.server.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractAgencyMapper;
import com.spt.bas.report.server.service.IRptCtrContractAgencyService;
@Component
public class RptCtrContractAgencyServiceImpl implements IRptCtrContractAgencyService {
	@Autowired
	private RptCtrContractAgencyMapper ctrContractAgencyMapper;
	@Override
	public Page<RptCtrContractAgencyReport> findPageAgency(RptAssementSearchVo vo) {
		List<RptCtrContractAgencyReport> list = ctrContractAgencyMapper.findPageAgency(vo);
		List<RptCtrContractAgencyReport> voList = new ArrayList<>();
		for (RptCtrContractAgencyReport report : list) {
			RptCtrContractAgencyReport agencyReport = new RptCtrContractAgencyReport();
			BeanUtils.copyProperties(report, agencyReport);
			//采购发票
			List<RptCtrContractAgencyReport> receiveBillNos = ctrContractAgencyMapper.findReceiveBillNoById(report);
			Stream<String> receiveMap = receiveBillNos.stream().map(RptCtrContractAgencyReport::getReceiveBillNo);
			String reciveNoJoin = String.join(",", receiveMap.collect(Collectors.toList()));
			agencyReport.setReceiveBillNo(reciveNoJoin);
			//销售发票
			List<RptCtrContractAgencyReport> invoiceBillNos = ctrContractAgencyMapper.findInvoiceBillNoById(report);
			Stream<String> invoiceMap = invoiceBillNos.stream().map(RptCtrContractAgencyReport::getInvoiceBillNo);
			String invoiceJoin = String.join(",", invoiceMap.collect(Collectors.toList()));
			agencyReport.setInvoiceBillNo(invoiceJoin);
			//销售数量
			BigDecimal sellTotalNumber = agencyReport.getSellTotalNumber();
			//毛利
			BigDecimal price = agencyReport.getSellPrice().subtract(agencyReport.getBuyPrice());
			BigDecimal profit = sellTotalNumber.multiply(price);
			agencyReport.setProfit(profit);
			voList.add(agencyReport);
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractAgencyReport> pageVo = new PageImpl<>(voList, pageable, vo.getCount());
		return pageVo;
	}

	@Override
	public RptCtrContractAgencyReport findPageAgencyTotal(RptAssementSearchVo vo) {
		vo.setCount(-1);
		RptCtrContractAgencyReport total = ctrContractAgencyMapper.findPageAgencyTotal(vo);
		if (total == null) {
			total = new RptCtrContractAgencyReport();
		}
		return total;
	}

	@Override
	public List<RptCtrContractAgencyReport> findAgencyBySellId(RptAssementSearchVo vo) {
		List<RptCtrContractAgencyReport> list = ctrContractAgencyMapper.findAgencyBySellId(vo);
		List<RptCtrContractAgencyReport> newList = new ArrayList<>();
		for (RptCtrContractAgencyReport agencyReport : list) {
			BigDecimal sellTotalNumber = agencyReport.getSellTotalNumber();
			BigDecimal price = agencyReport.getSellPrice().subtract(agencyReport.getBuyPrice());
			BigDecimal profit = sellTotalNumber.multiply(price);
			agencyReport.setProfit(profit);
			newList.add(agencyReport);
		}
		return newList;
	}

	@Override
	public Page<RptCtrContractAgencyReport> findSecondCalculatePage(RptAssementSearchVo vo) {
		List<RptCtrContractAgencyReport> list = ctrContractAgencyMapper.findSecondCalculatePage(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractAgencyReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
}
