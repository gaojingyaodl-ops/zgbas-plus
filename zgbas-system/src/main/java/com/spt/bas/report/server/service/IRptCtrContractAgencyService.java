package com.spt.bas.report.server.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;

public interface IRptCtrContractAgencyService {
	
	/**
	 * 代采代销明细
	 * @param vo 多查询条件
	 */
	public Page<RptCtrContractAgencyReport> findPageAgency(RptAssementSearchVo vo);
	
	/**
	 * 合计
	 */
	RptCtrContractAgencyReport findPageAgencyTotal(RptAssementSearchVo vo);
	
	List<RptCtrContractAgencyReport> findAgencyBySellId(RptAssementSearchVo vo);
	
	public Page<RptCtrContractAgencyReport> findSecondCalculatePage(RptAssementSearchVo vo);
}
