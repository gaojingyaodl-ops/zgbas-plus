package com.spt.bas.report.server.service;


import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;

import java.util.List;

public interface IRptCtrContractReceiveDetailService {
	
	/**
	 * 收款明细
	 */
	public Page<RptCtrContractReceiveDetailReport> findPageReceiveDetail(RptReceiveDetailSearchVo vo);

	/**
	 * 统计
	 * @param searchVo
	 * @return
	 */
	public RptCtrContractReceiveDetailReport findPageReceiveDetailSum(RptReceiveDetailSearchVo searchVo);

	/**
	 * 查询销售收款日期
	 * @param searchVo
	 * @return
	 */
	List<RptContractDateVo> selectSellReceiveDateList(RptContractDateSearchVo searchVo);
}
