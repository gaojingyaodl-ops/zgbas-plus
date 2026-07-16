package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import com.spt.bas.client.entity.CtrContractSchedule;

public interface CtrContractScheduleDao extends BaseDao<CtrContractSchedule> {

	public CtrContractSchedule findByContractIdAndScheduleType(Long contractId, String scheduleType);
}

