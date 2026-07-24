package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.client.entity.BsCompany;
import com.spt.tools.jpa.dao.BaseDao;

public interface BsCompanyDao extends BaseDao<BsCompany> {

    BsCompany findByContactPhoneAndEnableFlgTrue(String phone);

    BsCompany findByIdAndEnableFlgTrue(Long id);

    BsCompany findByCompanyNameLikeAndEnableFlgTrue(String companyName);

    BsCompany findBsCompanyByCompanyNameAndEnableFlgTrue(String companyName);


}
