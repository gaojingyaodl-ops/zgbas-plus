package com.spt.pm.service;

import com.spt.pm.entity.PmProcessStep;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IPmProcessStepService extends IBaseService<PmProcessStep> {
    /**
     * 查询有效数据
     * @return
     */
    List<PmProcessStep> findEnable();

    List<PmProcessStep> findStepByConditionId(Long conditionId);
}

