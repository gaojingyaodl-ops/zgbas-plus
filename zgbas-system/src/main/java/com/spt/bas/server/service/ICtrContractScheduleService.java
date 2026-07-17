package com.spt.bas.server.service;

import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.jpa.service.IBaseService;

import org.springframework.data.domain.Page;

import com.spt.bas.client.entity.CtrContractSchedule;

public interface ICtrContractScheduleService extends IBaseService<CtrContractSchedule> {
	
	void doWarehouseScheduleTask();
	
	void doPreSellScheduleTask();
	
	void doBilledScheduleTask();

	Page<CtrContractSchedule> findSchedulePage(PageSearchVo searchVo);

	void doUpdatePerformanceStatusTask();

	void doUnDelieryNotifyTask();

    void refreshContractStatus();

	void startDaDiInvoiceApply();
}

