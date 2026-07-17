package com.spt.bas.server.performance.service;

import com.spt.bas.client.entity.PerformanceCommissionUser;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;

public interface IPerformanceCommissionUserService extends IBaseService<PerformanceCommissionUser> {

    void initPerformanceCommissionUser(Date commissionDate, Long userId);

    PerformanceCommissionUser findPerformanceCommissionUser(PerformanceCommissionUser queryVo);
}
