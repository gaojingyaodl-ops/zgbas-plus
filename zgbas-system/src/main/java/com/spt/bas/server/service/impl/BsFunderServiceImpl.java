package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.server.dao.BsFunderDao;
import com.spt.bas.server.service.IBsFunderService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BsFunderServiceImpl extends BaseService<BsFunder> implements IBsFunderService {
    @Autowired
    private BsFunderDao bsFunderDao;
    @Override
    public BaseDao<BsFunder> getBaseDao() {
        return bsFunderDao;
    }

    @Override
    public List<BsFunder> findAllByUserId(Long userId) {
        return bsFunderDao.findAllByUserId(userId);
    }
}
