package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ClaimRecovery;
import com.spt.bas.server.dao.ClaimRecoveryDao;
import com.spt.bas.server.service.IClaimRecoveryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-03-03 11:12
 */
@Component
public class ClaimRecoveryServiceImpl extends BaseService<ClaimRecovery> implements IClaimRecoveryService {
    @Autowired
    private ClaimRecoveryDao claimRecoveryDao;
    @Override
    public BaseDao<ClaimRecovery> getBaseDao() {
        return claimRecoveryDao;
    }

    @Override
    public ClaimRecovery findByCorpSerialNo(String corpSerialNo) {
        return claimRecoveryDao.findByCorpSerialNo(corpSerialNo);
    }

}
