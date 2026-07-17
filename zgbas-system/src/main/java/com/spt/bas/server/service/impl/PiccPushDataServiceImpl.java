package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.PiccPushData;
import com.spt.bas.server.dao.PiccPushDataDao;
import com.spt.bas.server.service.IPiccPushDataService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-03-04 15:30
 */
@Component
@Slf4j
public class PiccPushDataServiceImpl extends BaseService<PiccPushData> implements IPiccPushDataService {
    @Autowired
    private PiccPushDataDao piccPushDataDao;

    @Override
    public BaseDao<PiccPushData> getBaseDao() {
        return piccPushDataDao;
    }

    @Override
    public PiccPushData findContractNoSx(String contractNo) {
        return piccPushDataDao.findByContractNoAndApproveType(contractNo,"0");
    }

    @Override
    public PiccPushData findContractNoRecovery(String contractNo) {
        return piccPushDataDao.findByContractNoAndApproveType(contractNo, "1");
    }

}
