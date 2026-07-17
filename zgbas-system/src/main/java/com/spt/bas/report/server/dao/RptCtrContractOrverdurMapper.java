package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractOrverdur;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractOrverdurMapper {
	/**
	 * 逾期查询
	 */
	List<RptCtrContractOrverdur> findPageOrverdur(RptCtrContractOrverdur vo);
	
	List<RptCtrContractOrverdur> findAllOrverdur(RptCtrContractOrverdur vo);
	
	/**
	 * 合计
	 */
	RptCtrContractOrverdur findPageTotal(RptCtrContractOrverdur vo);

	List<RptCtrContractOrverdur> findReceivePageOrverdur(RptCtrContractOrverdur vo);

	RptCtrContractOrverdur findReceivePageTotal(RptCtrContractOrverdur vo);
}