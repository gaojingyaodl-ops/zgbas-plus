package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractAsseMentReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractAsseMentMapper {
	/**
	 * 自营考核统计
	 * @param vo 多查询条件
	 * @param startrow 开始行
	 * @param rows 行数
	 * @return 自营考核集合
	 */
	List<RptCtrContractAsseMentReport> findPageAssessment(RptAssementSearchVo vo);
	
	/**
	 * 合计
	 */
	RptCtrContractAsseMentReport findPageTotal(RptAssementSearchVo vo);
}