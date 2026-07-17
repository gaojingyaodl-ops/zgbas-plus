package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractPayAndReceiveReport;
import com.spt.bas.report.client.vo.RptPayAndReceiveSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractPayAndReceiveMapper {
	/**
	 * 代采代销明细
	 */
	List<RptCtrContractPayAndReceiveReport> findPagePay(RptPayAndReceiveSearchVo vo);
	
	RptCtrContractPayAndReceiveReport findPayTotalPage(RptPayAndReceiveSearchVo vo);
	
	List<RptCtrContractPayAndReceiveReport> findPageReceive(RptPayAndReceiveSearchVo vo);

	/**
	 * 应收统计
	 * @param vo
	 * @return
	 */
	RptCtrContractPayAndReceiveReport findPageReceiveSum(RptPayAndReceiveSearchVo vo);
	

	
	
}