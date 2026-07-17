package com.spt.bas.report.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spt.bas.report.client.entity.RptCtrContractStatistics;
import com.spt.bas.report.client.vo.RptStatisticsVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractStatisticsMapper {
	/**
	 * 进销存统计采购合同
	 * @param vo 多查询条件
	 * @param startrow 开始行
	 * @param rows 行数
	 * @return 采购合同集合
	 */
	List<RptCtrContractStatistics> findBuyCtrContract(RptStatisticsVo vo);
	/**
	 * 进销存统计汇总
	 * @param vo 多查询条件
	 * @return 统计结果
	 */
	RptCtrContractStatistics getContractStatistics(RptStatisticsVo vo);
	/**
	 * 根据合同详情ID查询销售详情
	 * @param productId 采购详情ID
	 * @return 销售详情集合
	 */
	List<RptCtrContractStatistics> findSaleCtrContract(@Param("productId") Long productId);
	/**
	 * 采购统计根据品名统计
	 * @param vo
	 * @return
	 */
	List<RptCtrContractStatistics> findContractGroupByProduct(RptStatisticsVo vo);
	/**
	 * 采购统计根据客户统计
	 * @param vo
	 * @return
	 */
	List<RptCtrContractStatistics> findContractGroupByCompany(RptStatisticsVo vo);
}