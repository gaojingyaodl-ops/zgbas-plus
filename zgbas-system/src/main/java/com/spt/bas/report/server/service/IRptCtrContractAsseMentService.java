package com.spt.bas.report.server.service;


import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;

public interface IRptCtrContractAsseMentService {
	
	/**
	 * 自营考核统计分页
	 * @param vo 多查询条件
	 */
	public Page<RptCtrContractAsseMentReport> findPageAssessment(RptAssementSearchVo vo);
	
	/**
	 * 合计
	 */
	public RptCtrContractAsseMentReport findPageTotal(RptAssementSearchVo vo);
}
