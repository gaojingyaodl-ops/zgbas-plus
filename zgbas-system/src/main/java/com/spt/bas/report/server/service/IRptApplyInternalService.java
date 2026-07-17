package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptApplyInternalReport;
public interface IRptApplyInternalService {
	
	/**
	 * 内部交易page
	 * @param vo 多查询条件
	 */
	public Page<RptApplyInternalReport> findPageInternalBuy(RptApplyInternalReport vo);
}
