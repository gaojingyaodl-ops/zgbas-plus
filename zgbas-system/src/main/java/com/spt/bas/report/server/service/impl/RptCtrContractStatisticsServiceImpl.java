package com.spt.bas.report.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.bas.report.server.dao.RptCtrContractStatisticsMapper;
import com.spt.bas.report.server.service.IRptCtrContractStatisticsService;
import com.spt.tools.core.bean.PageSearchVo;
@Component
public class RptCtrContractStatisticsServiceImpl implements IRptCtrContractStatisticsService {
	@Autowired
	private RptCtrContractStatisticsMapper ctrContractStatisticsmapper;
	
	// 进销存统计采购合同
	@Override
	public Page<RptCtrContractStatistics> findBuyCtrContract(RptStatisticsVo vo) {
		List<RptCtrContractStatistics> list = ctrContractStatisticsmapper.findBuyCtrContract(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractStatistics> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	// 进销存统计汇总
	@Override
	public RptCtrContractStatistics getContractStatistics(RptStatisticsVo vo) {
		vo.setCount(-1);
		RptCtrContractStatistics statistics = ctrContractStatisticsmapper.getContractStatistics(vo);
		if (statistics == null) {
			statistics = new RptCtrContractStatistics();
		}
		return statistics;
	}
	
	// 进销存统计销售合同
	@Override
	public Page<RptCtrContractStatistics> findSaleCtrContract(PageSearchVo searchVo) {
		int intId = (int) searchVo.getSearchParams().get("productId");
		Long productId = new Long((long) intId);
		List<RptCtrContractStatistics> list = ctrContractStatisticsmapper.findSaleCtrContract(productId);
		PageRequest pageRequest = PageRequest.of(searchVo.getPage()-1, searchVo.getRows());
		Page<RptCtrContractStatistics> pageVo = new PageImpl<>(list, pageRequest, null == list ? 0 : list.size());
		return pageVo;
	}
	
	// 采购统计
	@Override
	public Page<RptCtrContractStatistics> BuyOrSellStatistics(RptStatisticsVo vo) {
		List<RptCtrContractStatistics> list = new ArrayList<>();
		if ("companyName".equals(vo.getStatisticsType())) {
			list = ctrContractStatisticsmapper.findContractGroupByCompany(vo);
		}  
		if ("productName".equals(vo.getStatisticsType())) {
			list = ctrContractStatisticsmapper.findContractGroupByProduct(vo);
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractStatistics> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
}
