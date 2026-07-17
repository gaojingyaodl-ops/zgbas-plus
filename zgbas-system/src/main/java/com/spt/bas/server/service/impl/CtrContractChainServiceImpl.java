
package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.*;
import com.spt.bas.server.dao.*;
import com.spt.bas.server.service.*;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Transactional(readOnly = true)
public class CtrContractChainServiceImpl extends BaseService<CtrContractChain> implements ICtrContractChainService {


    @Autowired
    private  CtrContractChainDao ctrContractChainDao;
    @Override
    public BaseDao<CtrContractChain> getBaseDao() {
        return ctrContractChainDao;
    }


    @Override
    public CtrContractChain findByContractNo(String contractNo) {
        return ctrContractChainDao.findByContractNo(contractNo);
    }
}
