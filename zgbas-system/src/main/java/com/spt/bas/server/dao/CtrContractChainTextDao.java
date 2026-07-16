package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractChainText;
import com.spt.bas.client.entity.CtrContractText;
import com.spt.tools.jpa.dao.BaseDao;

public interface CtrContractChainTextDao extends BaseDao<CtrContractChainText> {

    CtrContractChainText findByCtrContractIdAndContractType(Long contractId, String contractType);
}

