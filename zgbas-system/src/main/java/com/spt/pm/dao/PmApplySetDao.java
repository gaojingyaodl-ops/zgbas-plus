package com.spt.pm.dao;

import java.util.List;

import com.spt.pm.entity.PmApplySet;
import com.spt.tools.jpa.dao.BaseDao;

public interface PmApplySetDao extends BaseDao<PmApplySet> {
	List<PmApplySet>findByProcessId(Long processId);
}

