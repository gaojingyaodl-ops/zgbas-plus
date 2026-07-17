package com.spt.pm.service;

import com.spt.pm.entity.PmProcessCondition;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IPmProcessConditionService extends IBaseService<PmProcessCondition> {
	List<PmProcessCondition> findConditionsByProcessId(Long processId);
}

