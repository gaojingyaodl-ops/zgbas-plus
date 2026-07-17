package com.spt.bas.report.server.dao;

import java.util.List;

import com.spt.bas.report.client.entity.RptCtrContractAgencyReport;
import com.spt.bas.report.client.vo.RptAssementSearchVo;
import com.spt.tools.mybatis.annotation.MyBatisDao;

@MyBatisDao
public interface RptCtrContractAgencyMapper {
	/**
	 * 代采代销明细
	 */
	List<RptCtrContractAgencyReport> findPageAgency(RptAssementSearchVo vo);
	
	/**
	 * 带采代销合计
	 * @param vo
	 * @return
	 */
	RptCtrContractAgencyReport findPageAgencyTotal(RptAssementSearchVo vo);
	
	List<RptCtrContractAgencyReport> findReceiveBillNoById(RptCtrContractAgencyReport vo);
	
	List<RptCtrContractAgencyReport> findInvoiceBillNoById(RptCtrContractAgencyReport vo);
	
	RptCtrContractAgencyReport findLastBill(RptCtrContractAgencyReport vo);
	
	List<RptCtrContractAgencyReport> findAgencyBySellId(RptAssementSearchVo vo);
	
	List<RptCtrContractAgencyReport> findSecondCalculatePage(RptAssementSearchVo vo);
	
}