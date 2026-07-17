package com.spt.bas.server.ctr.service;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrServiceContract;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrServiceContractService extends IBaseService<CtrServiceContract> {

    void updateFileId(Long id, String fileId);

    CtrServiceContract addServiceContract(CtrContract sellContract, ApplyMatchDetail detail) throws ApplicationException;
}
