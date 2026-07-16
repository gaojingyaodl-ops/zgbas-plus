package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractChain;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface CtrContractChainDao extends BaseDao<CtrContractChain> {


    CtrContractChain findByContractNo(String contractNo);
    
    List<CtrContractChain> findByApproveId(Long approveId);

}

