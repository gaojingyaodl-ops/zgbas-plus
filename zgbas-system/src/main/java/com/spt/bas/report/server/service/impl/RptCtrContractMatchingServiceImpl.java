package com.spt.bas.report.server.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractMatchingReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractMatchingMapper;
import com.spt.bas.report.server.service.IRptCtrContractMatchingService;
@Component
public class RptCtrContractMatchingServiceImpl implements IRptCtrContractMatchingService {
	@Autowired
	private RptCtrContractMatchingMapper ctrContractMatchMapper;
	@Override
	public Page<RptCtrContractMatchingReport> findPageMatching(RptAssementSearchVo vo) {
		List<RptCtrContractMatchingReport> list = ctrContractMatchMapper.findPageMatching(vo);
		List<RptCtrContractMatchingReport> voList = new ArrayList<>();
		for (RptCtrContractMatchingReport report : list) {
			RptCtrContractMatchingReport match = new RptCtrContractMatchingReport();
			BeanUtils.copyProperties(report, match);
			
			BigDecimal tranAmount = report.getBuyTransportAmount().multiply(report.getSellNumber()).divide(report.getBuyNumber()).add(report.getSellTransportAmount());
			//运费 采购合同运费*销售数量/采购合同数量+销售合同运费
			match.setTransportAmount(tranAmount);
			//差额
			match.setBalance((report.getSellPrice().subtract(report.getBuyPrice())).multiply(report.getSellNumber()));
			//毛利
			match.setProfit(match.getBalance().subtract(tranAmount));
			//奖励金额
			match.setBountyAmoun(match.getProfit().multiply(new BigDecimal(0.4)));
			//奖励
			match.setBounty(match.getBountyAmoun().multiply(new BigDecimal(0.5)));
			voList.add(match);
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractMatchingReport> pageVo = new PageImpl<>(voList, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptCtrContractMatchingReport findPageTotal(RptAssementSearchVo vo) {
		RptCtrContractMatchingReport total = ctrContractMatchMapper.findPageTotal(vo);
		if(total==null){
			total = new RptCtrContractMatchingReport();
		}
		return total;
	}

}
