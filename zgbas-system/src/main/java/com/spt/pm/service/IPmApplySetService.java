package com.spt.pm.service;

import java.util.List;

import com.spt.pm.entity.PmApplySet;
import com.spt.tools.jpa.service.IBaseService;

public interface IPmApplySetService extends IBaseService<PmApplySet> {
	List<PmApplySet>findByProcessId(Long processId);
}

