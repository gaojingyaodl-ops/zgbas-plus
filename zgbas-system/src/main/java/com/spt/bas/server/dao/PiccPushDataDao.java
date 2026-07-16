package com.spt.bas.server.dao;

import com.spt.bas.client.entity.PiccPushData;
import com.spt.tools.jpa.dao.BaseDao;

public interface PiccPushDataDao extends BaseDao<PiccPushData> {
    PiccPushData findByContractNoAndApproveType(String contractNo, String approveType);
    
}
