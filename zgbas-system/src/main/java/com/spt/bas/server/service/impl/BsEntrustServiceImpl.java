package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsEntrust;
import com.spt.bas.server.dao.BsEntrustDao;
import com.spt.bas.server.service.IBsEntrustService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-28 11:09
 */
@Component
public class BsEntrustServiceImpl extends BaseService<BsEntrust> implements IBsEntrustService {
    @Autowired
    private BsEntrustDao bsEntrustDao;
    @Override
    public BaseDao<BsEntrust> getBaseDao() {
        return bsEntrustDao;
    }

    @Override
    public BsEntrust findByWxUserId(Long wxUserId) {
        List<BsEntrust> entrustList = bsEntrustDao.findByWxUserId(wxUserId);
        return CollectionUtils.isNotEmpty(entrustList) ? entrustList.get(0) : null;
    }

    @Override
    public List<BsEntrust> findByCompanyId(Long companyId) {
        return bsEntrustDao.findByCompanyId(companyId);
    }

}
