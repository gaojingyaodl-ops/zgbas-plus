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
// Phase 8 (D-P8-01 minimal fix, behavior-equivalent): explicit bean name disambiguates this WX
// service's default name "bsCompanyService" from 35 basServer @Resource sites that inject a field
// named bsCompanyService expecting com.spt.bas.server.service.IBsCompanyService (impl there is
// BsCompanyServiceImpl). Source isolated via separate apps; the monolith's broad com.spt scan lets
// this WX bean shadow the basServer field-name injection. This WX service has NO name-based WX
// caller (injected by WX IBsCompanyService type), so renaming is safe — mirrors FileController /
// BsDictService precedent. No business-semantic change.
@Component("wxBsCompanyService")
@Transactional(readOnly = true)
public class BsCompanyService extends BaseService<BsCompany> implements IBsCompanyService {

    @Autowired
    private BsCompanyDao bsCompanyDao;

    @Override
    public BaseDao<BsCompany> getBaseDao() {
        return bsCompanyDao;
    }



}
