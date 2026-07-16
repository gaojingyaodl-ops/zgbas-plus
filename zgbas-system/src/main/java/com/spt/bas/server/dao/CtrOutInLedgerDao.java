package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrOutInLedger;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

public interface CtrOutInLedgerDao extends BaseDao<CtrOutInLedger> {

    public List<CtrOutInLedger> findByOperationAndContractNo(String operation, String contractNo);

    public List<CtrOutInLedger> findByContractNo(String contractNo);
}
