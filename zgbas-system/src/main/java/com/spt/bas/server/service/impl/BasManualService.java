package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.BasManual;
import com.spt.bas.server.dao.BasManualDao;
import com.spt.bas.server.service.IBasManualService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasManualService extends BaseService<BasManual> implements IBasManualService {
    @Autowired
    private BasManualDao basManualDao;
    @Override
    public BaseDao<BasManual> getBaseDao() {
        return basManualDao;
    }

    @Override
    public List<BasManual> findAllEnable() {
        return basManualDao.findAllByEnableFlgTrue();
    }
}
