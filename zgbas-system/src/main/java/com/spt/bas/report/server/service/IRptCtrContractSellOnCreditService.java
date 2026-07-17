package com.spt.bas.report.server.service;


import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractSellOnCreditReport;
import com.spt.bas.report.client.vo.RptSellOnCreditSearchVo;

public interface IRptCtrContractSellOnCreditService {
	
	/**
	 * 赊销合同
	 */
	public Page<RptCtrContractSellOnCreditReport> findPageSellOnCredit(RptSellOnCreditSearchVo vo);

	
}
