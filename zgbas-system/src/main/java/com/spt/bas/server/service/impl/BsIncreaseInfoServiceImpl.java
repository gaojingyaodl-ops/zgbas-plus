package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsIncreaseInfo;
import com.spt.bas.server.dao.BsIncreaseInfoDao;
import com.spt.bas.server.service.IBsIncreaseInfoService;
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
 * @Date: Created in 2021-01-27 09:49
 */
@Component
public class BsIncreaseInfoServiceImpl extends BaseService<BsIncreaseInfo> implements IBsIncreaseInfoService {
    @Autowired
    private BsIncreaseInfoDao increaseInfoDao;

    @Override
    public BaseDao<BsIncreaseInfo> getBaseDao() {
        return increaseInfoDao;
    }

    @Override
    public BsIncreaseInfo findByCompanyId(Long companyId) {
        return increaseInfoDao.findByCompanyId(companyId);
    }

}
