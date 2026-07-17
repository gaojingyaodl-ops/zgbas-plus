package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsNotice;
import com.spt.bas.server.dao.BsNoticeDao;
import com.spt.bas.server.service.IBsNoticeService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class BsNoticeServiceimpl extends BaseService<BsNotice> implements IBsNoticeService {
    @Autowired
    private BsNoticeDao bsNoticeDao;

    @Override
    public BaseDao<BsNotice> getBaseDao() {
        return bsNoticeDao;
    }


    @Override
    public BsNotice findLast() {
        return bsNoticeDao.findLast();
    }

    @Override
    public List<BsNotice> findLimit5(String deptId) {
        return bsNoticeDao.findLimit5(deptId);
    }

    @Override
    public List<BsNotice> findLimit() {
        return bsNoticeDao.findLimit();
    }
}
