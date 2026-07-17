package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractChain;
import com.spt.tools.jpa.service.IBaseService;


public interface ICtrContractChainService extends IBaseService<CtrContractChain> {

    CtrContractChain findByContractNo(String contractNo);

}

