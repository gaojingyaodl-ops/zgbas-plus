package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.CtrOutInLedger;
import com.spt.bas.server.dao.CtrOutInLedgerDao;
import com.spt.bas.server.service.ICtrLedgerService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CtrLedgerServiceImpl extends BaseService<CtrOutInLedger> implements ICtrLedgerService {

    @Autowired
    private CtrOutInLedgerDao ctrOutInLedgerDao;

    @Override
    public BaseDao<CtrOutInLedger> getBaseDao() {
        return ctrOutInLedgerDao;
    }
}
