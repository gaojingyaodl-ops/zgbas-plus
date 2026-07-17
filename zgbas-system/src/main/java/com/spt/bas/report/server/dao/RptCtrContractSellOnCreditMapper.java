package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractSellOnCreditReport;
import com.spt.bas.report.client.vo.RptSellOnCreditSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractSellOnCreditMapper {
	/**
	 * 赊销合同
	 * @param vo
	 * @return
	 */
	List<RptCtrContractSellOnCreditReport> findPageReceiveDetail(RptSellOnCreditSearchVo vo);
	

	
	
}