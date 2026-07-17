package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.tools.core.bean.PageSearchVo;

public interface IRptCtrContractStatisticsService {
	/**
	 * 进销存统计采购合同
	 * @param vo 多查询条件
	 * @return 采购合同分页
	 */
	public Page<RptCtrContractStatistics> findBuyCtrContract(RptStatisticsVo vo);
	/**
	 * 进销存统计汇总
	 * @param vo 多查询条件
	 * @return 统计结果
	 */
	public RptCtrContractStatistics getContractStatistics(RptStatisticsVo vo);
	/**
	 * 进销存统计销售合同
	 * @param 
	 * @return 销售合同分页
	 */
	public Page<RptCtrContractStatistics> findSaleCtrContract(PageSearchVo searchVo);
	/**
	 * 采购统计
	 * @param vo
	 * @return
	 */
	public Page<RptCtrContractStatistics> BuyOrSellStatistics(RptStatisticsVo vo);
	
}
