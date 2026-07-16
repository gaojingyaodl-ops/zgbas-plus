package com.spt.bas.server.dao;

import com.spt.tools.jpa.dao.BaseDao;

import com.spt.bas.client.entity.SealUsage;

public interface SealUsageDao extends BaseDao<SealUsage> {
    /**
     * 根据ContractId和业务类型查找
     * @param contractId
     * @param businessType
     * @return
     */
    SealUsage findByContractIdAndBusinessType(Long contractId, String businessType);

}

