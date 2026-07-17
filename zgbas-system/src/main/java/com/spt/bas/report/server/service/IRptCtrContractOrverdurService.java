package com.spt.bas.report.server.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractOrverdur;

public interface IRptCtrContractOrverdurService {
	
	/**
	 * 逾期查询
	 * @param vo 多查询条件
	 */
	public Page<RptCtrContractOrverdur> findPageOrverdur(RptCtrContractOrverdur vo);
	
	public List<RptCtrContractOrverdur> findAllOrverdur(RptCtrContractOrverdur vo);
	
	/**
	 * 合计
	 */
	public RptCtrContractOrverdur findPageTotal(RptCtrContractOrverdur vo);
}
