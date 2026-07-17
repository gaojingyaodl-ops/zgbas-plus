package com.spt.bas.server.service.impl;

import com.spt.bas.client.entity.PiccAvailableRecord;
import com.spt.bas.server.dao.PiccAvailableRecordDao;
import com.spt.bas.server.service.IPiccAvailableRecordService;
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
 * @Date: Created in 2021-04-25 15:21
 */
@Component
public class PiccAvailableRecordServiceImpl extends BaseService<PiccAvailableRecord> implements IPiccAvailableRecordService {

    @Autowired
    private PiccAvailableRecordDao piccAvailableRecordDao;

    @Override
    public BaseDao<PiccAvailableRecord> getBaseDao() {
        return piccAvailableRecordDao;
    }



}
