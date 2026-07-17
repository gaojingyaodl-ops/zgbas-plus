package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptApplyInternalReport;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptApplyInternalMapper {
	
	/**
	 * 内部交易报表
	 */
	List<RptApplyInternalReport> findPageInternalBuy(RptApplyInternalReport vo);
}
