package com.spt.bas.report.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractPayAndReceiveMapper;
import com.spt.bas.report.server.service.IRptCtrContractPayAndReceiveService;
@Component
public class RptCtrContractPayAndReceiveServiceImpl implements IRptCtrContractPayAndReceiveService {
	@Autowired
	private RptCtrContractPayAndReceiveMapper ctrContractPayAndReceiveMapper;

	@Override
	public Page<RptCtrContractPayAndReceiveReport> findPagePay(RptPayAndReceiveSearchVo vo) {
		
		List<RptCtrContractPayAndReceiveReport> list = ctrContractPayAndReceiveMapper.findPagePay(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractPayAndReceiveReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public Page<RptCtrContractPayAndReceiveReport> findPageReceive(RptPayAndReceiveSearchVo vo) {
		
		List<RptCtrContractPayAndReceiveReport> list = ctrContractPayAndReceiveMapper.findPageReceive(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractPayAndReceiveReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}
	@Override
	public RptCtrContractPayAndReceiveReport findPageReceiveDetailSum(RptPayAndReceiveSearchVo vo) {
		vo.setCount(-1);
		RptCtrContractPayAndReceiveReport  total =  ctrContractPayAndReceiveMapper.findPageReceiveSum(vo);
				if(total==null){
					total = new RptCtrContractPayAndReceiveReport();
				}
				return total;
			}
	@Override
	public RptCtrContractPayAndReceiveReport findPayTotalPage(RptPayAndReceiveSearchVo vo) {
		vo.setCount(-1);
		RptCtrContractPayAndReceiveReport total = ctrContractPayAndReceiveMapper.findPayTotalPage(vo);
		if (total == null) {
			total = new RptCtrContractPayAndReceiveReport();
		}
		return total;
	}
	
	
}
