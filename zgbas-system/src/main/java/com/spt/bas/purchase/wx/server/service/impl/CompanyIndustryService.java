package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.client.entity.BsCompanyIndustry;
import com.spt.bas.server.dao.BsCompanyIndustryDao;
import com.spt.bas.purchase.wx.server.service.ICompanyIndustryService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  行业类型
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-29 13:41
 */
@Component
public class CompanyIndustryService extends BaseService<BsCompanyIndustry> implements ICompanyIndustryService {

    @Autowired
    private BsCompanyIndustryDao companyIndustryDao;

    @Override
    public BaseDao<BsCompanyIndustry> getBaseDao() {
        return companyIndustryDao;
    }

    
}
