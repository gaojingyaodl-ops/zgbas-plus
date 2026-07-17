package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractSellOnCreditReport;
import com.spt.bas.report.client.vo.RptSellOnCreditSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractSellOnCreditMapper;
import com.spt.bas.report.server.service.IRptCtrContractSellOnCreditService;
@Component
public class RptCtrContractSellOnCreditServiceImpl implements IRptCtrContractSellOnCreditService{
	@Autowired
	private RptCtrContractSellOnCreditMapper ctrContractSellOnCreditMapper;

	@Override
	public Page<RptCtrContractSellOnCreditReport> findPageSellOnCredit(RptSellOnCreditSearchVo vo) {
		List<RptCtrContractSellOnCreditReport> list = ctrContractSellOnCreditMapper.findPageReceiveDetail(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractSellOnCreditReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
}
