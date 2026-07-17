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

import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractAsseMentMapper;
import com.spt.bas.report.server.service.IRptCtrContractAsseMentService;
@Component
public class RptCtrContractAsseMentServiceImpl implements IRptCtrContractAsseMentService {
	@Autowired
	private RptCtrContractAsseMentMapper ctrContractAsseMentMapper;
	@Override
	public Page<RptCtrContractAsseMentReport> findPageAssessment(RptAssementSearchVo vo) {
		List<RptCtrContractAsseMentReport> list = ctrContractAsseMentMapper.findPageAssessment(vo);
		List<RptCtrContractAsseMentReport> voList = new ArrayList<>();
		for (RptCtrContractAsseMentReport ctrReport : list) {
			RptCtrContractAsseMentReport report = new RptCtrContractAsseMentReport();
			BeanUtils.copyProperties(ctrReport, report);
			//销售员的毛利=数量*25；
			report.setSellMatchProfit(ctrReport.getSellNumber().multiply(new BigDecimal(25)));
			//采购员的毛利=销售额-采购额-销售员毛利-运费；
			report.setBuyMatchProfit(ctrReport.getSellAmount().subtract(ctrReport.getBuyAmount()).subtract(report.getSellMatchProfit()).subtract(report.getTransAmount()));
			voList.add(report);
		}
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractAsseMentReport> pageVo = new PageImpl<>(voList, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptCtrContractAsseMentReport findPageTotal(RptAssementSearchVo vo) {
		vo.setCount(-1);
		 RptCtrContractAsseMentReport total = ctrContractAsseMentMapper.findPageTotal(vo);
		if (total == null) {
			total = new RptCtrContractAsseMentReport();
		}
		return total;
	}

}
