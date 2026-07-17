package com.spt.bas.report.server.service;


import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;

public interface IRptCtrContractPayAndReceiveService {
	
	/**
	 *应付
	 * @param vo 多查询条件
	 */
	public Page<RptCtrContractPayAndReceiveReport> findPagePay(RptPayAndReceiveSearchVo vo);
	
	
	/**
	 * 应付明细合计
	 * @param vo
	 * @return
	 */
	RptCtrContractPayAndReceiveReport findPayTotalPage(RptPayAndReceiveSearchVo vo);
	
	/**
	 * 应收
	 */
	public Page<RptCtrContractPayAndReceiveReport> findPageReceive(RptPayAndReceiveSearchVo vo);

	/**
	 * 应收统计
	 * @param vo
	 * @return
	 */
	public RptCtrContractPayAndReceiveReport findPageReceiveDetailSum(RptPayAndReceiveSearchVo vo);
}
