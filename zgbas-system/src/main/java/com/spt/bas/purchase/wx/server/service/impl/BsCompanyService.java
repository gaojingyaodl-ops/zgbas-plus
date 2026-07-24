package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.server.dao.BsCompanyDao;
import com.spt.bas.purchase.wx.server.service.IBsCompanyService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  公司
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-16 17:34
 */
@Component
@Transactional(readOnly = true)
public class BsCompanyService extends BaseService<BsCompany> implements IBsCompanyService {

    @Autowired
    private BsCompanyDao bsCompanyDao;

    @Override
    public BaseDao<BsCompany> getBaseDao() {
        return bsCompanyDao;
    }



}
