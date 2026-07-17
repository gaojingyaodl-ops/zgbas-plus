package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsSupplyInfo;
import com.spt.bas.server.dao.BsSupplyInfoDao;
import com.spt.bas.server.service.IBsSupplyInfoService;
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
 * @Date: Created in 2020-10-28 12:03
 */
@Component
public class BsSupplyInfoService extends BaseService<BsSupplyInfo> implements IBsSupplyInfoService {
    @Autowired
    private BsSupplyInfoDao supplyInfoDao;
    @Override
    public BaseDao<BsSupplyInfo> getBaseDao() {
        return supplyInfoDao;
    }

    @Override
    public BsSupplyInfo findByWxUserId(Long wxUserId) {
        return supplyInfoDao.findByWxUserId(wxUserId);
    }

    @Override
    public BsSupplyInfo findByCompanyId(Long companyId) {

        return supplyInfoDao.findByCompanyId(companyId);
    }

}
