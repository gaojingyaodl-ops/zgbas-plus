package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractReceiveDetailReport;
import com.spt.bas.report.client.vo.RptContractDateSearchVo;
import com.spt.bas.report.client.vo.RptContractDateVo;
import com.spt.bas.report.client.vo.RptReceiveDetailSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractReceiveDetailMapper {
	/**
	 * 收款明细
	 */	
	List<RptCtrContractReceiveDetailReport> findPageReceiveDetail(RptReceiveDetailSearchVo vo);

	
	/**
	 * 统计
	 * @param searchVo
	 * @return
	 */
	RptCtrContractReceiveDetailReport findPageReceiveDetailSum(RptReceiveDetailSearchVo searchVo);

	List<RptContractDateVo> selectSellReceiveDateList(RptContractDateSearchVo searchVo);
	
	
}