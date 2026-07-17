package com.spt.bas.server.service.impl;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.CtrContractTextVo;
import com.spt.bas.client.vo.ExtraBankVo;
import com.spt.bas.server.dao.BsCompanyOurDao;
import com.spt.bas.server.service.IBsCompanyOurService;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@Transactional(readOnly = true)
public class BsCompanyOurServiceImpl extends BaseService<BsCompanyOur> implements IBsCompanyOurService {

    @Autowired
    private BsCompanyOurDao bsCompanyOurDao;

    @Override
    public BaseDao<BsCompanyOur> getBaseDao() {
        return bsCompanyOurDao;
    }

    @Override
    public BsCompanyOur findByCompanyName(String companyName) {
        return bsCompanyOurDao.findByCompanyName(companyName);
    }

    @Override
    public List<BsCompanyOur> findAllEnableOurCompany() {
        return bsCompanyOurDao.findAllEnableOurCompany();
    }
}
