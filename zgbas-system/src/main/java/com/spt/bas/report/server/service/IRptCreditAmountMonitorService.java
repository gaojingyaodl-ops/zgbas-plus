package com.spt.bas.report.server.service;


import com.spt.bas.report.client.entity.RptCreditAmountMonitor;
import com.spt.bas.report.client.vo.RptCreditAmountMonitorSearchVo;
import org.springframework.data.domain.Page;

public interface IRptCreditAmountMonitorService {

	Page<RptCreditAmountMonitor> findCreditAmountMonitorPage(RptCreditAmountMonitorSearchVo searchVo);
	

}
