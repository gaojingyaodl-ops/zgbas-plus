package com.spt.bas.report.server.service;


import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractMatchingReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;

public interface IRptCtrContractMatchingService {
	
	/**
	 * 撮合考核统计分页
	 * @param vo 多查询条件
	 */
	public Page<RptCtrContractMatchingReport> findPageMatching(RptAssementSearchVo vo);
	
	/**
	 * 合计
	 */
	public RptCtrContractMatchingReport findPageTotal(RptAssementSearchVo vo);
}
