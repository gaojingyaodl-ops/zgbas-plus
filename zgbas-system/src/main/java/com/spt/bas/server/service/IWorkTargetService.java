package com.spt.bas.server.service;

import com.spt.bas.client.entity.WorkTarget;
import com.spt.tools.jpa.service.IBaseService;

public interface IWorkTargetService extends IBaseService<WorkTarget> {
    String findByBranchCdAndTargetMonth(WorkTarget query);
}
