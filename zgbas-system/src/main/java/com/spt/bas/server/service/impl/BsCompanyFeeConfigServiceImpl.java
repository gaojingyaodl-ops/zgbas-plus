package com.spt.bas.server.service.impl;


import com.spt.bas.client.entity.BsCompanyFeeConfig;
import com.spt.bas.server.dao.BsCompanyFeeConfigDao;
import com.spt.bas.server.service.BsCompanyFeeConfigService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BsCompanyFeeConfigServiceImpl extends BaseService<BsCompanyFeeConfig> implements BsCompanyFeeConfigService {

    @Autowired
    private BsCompanyFeeConfigDao bsCompanyFeeConfigDao;

    @Override
    public BaseDao<BsCompanyFeeConfig> getBaseDao() {
        return bsCompanyFeeConfigDao;
    }
}
