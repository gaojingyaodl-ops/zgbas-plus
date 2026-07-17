package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractMatchingReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractMatchingMapper {
	/**
	 * 撮合考核统计
	 */
	List<RptCtrContractMatchingReport> findPageMatching(RptAssementSearchVo vo);
	
	/**
	 * 合计
	 */
	RptCtrContractMatchingReport findPageTotal(RptAssementSearchVo vo);
}