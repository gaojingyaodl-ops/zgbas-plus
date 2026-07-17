package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.ClaimBuyer;
import com.spt.bas.server.dao.ClaimBuyerDao;
import com.spt.bas.server.service.IClaimBuyerService;
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
public class ClaimBuyerServiceImpl extends BaseService<ClaimBuyer> implements IClaimBuyerService {
    @Autowired
    private ClaimBuyerDao claimBuyerDao;
    @Override
    public BaseDao<ClaimBuyer> getBaseDao() {
        return claimBuyerDao;
    }

    @Override
    public ClaimBuyer findBycorpSerialNo(String corpSerialNo) {
        return claimBuyerDao.findByCorpSerialNo(corpSerialNo);
    }

}
