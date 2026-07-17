package com.spt.bas.report.server.service;

import org.springframework.data.domain.Page;

import com.spt.bas.report.client.entity.RptApplyDeliveryReport;
import com.spt.bas.report.client.vo.RptApplyDeliverySearchVo;

public interface IRptApplyDeliveryReportService {
	
	Page<RptApplyDeliveryReport> findApplyDeliveryPage(RptApplyDeliverySearchVo searchVo);
}
