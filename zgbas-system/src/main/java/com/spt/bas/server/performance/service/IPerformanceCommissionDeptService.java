package com.spt.bas.server.performance.service;

import com.spt.bas.client.entity.PerformanceCommissionDept;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;

public interface IPerformanceCommissionDeptService extends IBaseService<PerformanceCommissionDept> {

    void initPerformanceCommissionDept(Date commissionDate);

    PerformanceCommissionDept findPerformanceCommissionDept(PerformanceCommissionDept queryVo);
}
