package com.spt.bas.report.server.service.impl;

import java.util.List;

import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;
import com.spt.bas.report.server.dao.RptCtrContractReceiveDetailMapper;
import com.spt.bas.report.server.service.IRptCtrContractReceiveDetailService;
@Component
public class RptCtrContractReceiveDetailServiceImpl implements IRptCtrContractReceiveDetailService{
	@Autowired
	private RptCtrContractReceiveDetailMapper ctrContractReceiveDetailMapper;

	@Override
	public Page<RptCtrContractReceiveDetailReport> findPageReceiveDetail(RptReceiveDetailSearchVo vo) {
		List<RptCtrContractReceiveDetailReport> list = ctrContractReceiveDetailMapper.findPageReceiveDetail(vo);
		Pageable pageable = PageRequest.of(vo.getPage() - 1, vo.getRows());
		Page<RptCtrContractReceiveDetailReport> pageVo = new PageImpl<>(list, pageable, vo.getCount());
		return pageVo;
	}

	@Override
	public RptCtrContractReceiveDetailReport findPageReceiveDetailSum(RptReceiveDetailSearchVo searchVo) {
		//searchVo.setCount(-1);
		RptCtrContractReceiveDetailReport  total =  ctrContractReceiveDetailMapper.findPageReceiveDetailSum(searchVo);
		if(total==null){
			total = new RptCtrContractReceiveDetailReport();
		}
		return total;
	}

	/**
	 * 查询销售收款日期
	 * @param searchVo
	 * @return
	 */
	@Override
	public List<RptContractDateVo> selectSellReceiveDateList(RptContractDateSearchVo searchVo) {
		return ctrContractReceiveDetailMapper.selectSellReceiveDateList(searchVo);
	}
}
