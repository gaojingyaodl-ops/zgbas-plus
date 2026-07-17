package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyEntrust;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyEntrustService extends IBaseService<ApplyEntrust> {
    void startFlow(String bizEntityJson, Long companyId) throws ApplicationException;

    /**
     * 根据公司名称查询是否用经办人
     * 不能根据companyId 查询,历史数据中没有保存companyId
     * @param companyName 公司名词
     * @return true-已经绑定过，false-没有绑定过
     */
    Boolean findIsHaveEntrustUserByCompanyName(String companyName);
}
