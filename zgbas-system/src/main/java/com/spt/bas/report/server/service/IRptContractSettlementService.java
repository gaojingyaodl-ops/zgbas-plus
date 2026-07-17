package com.spt.bas.report.server.service;


import com.spt.bas.report.client.entity.RptContractSettlementVo;
import com.spt.bas.report.client.vo.RptContractSettlementSearchVo;
import org.springframework.data.domain.Page;

public interface IRptContractSettlementService {

	Page<RptContractSettlementVo> findRptContractSettlementPage(RptContractSettlementSearchVo searchVo);
	RptContractSettlementVo findRptContractSettlementSum(RptContractSettlementSearchVo searchVo);


}
