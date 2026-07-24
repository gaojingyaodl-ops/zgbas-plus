package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.client.entity.BsCompanyIndustry;
import com.spt.tools.jpa.dao.BaseDao;

import java.util.List;

/**
 * 行业类型
 */
public interface CompanyIndustryDao extends BaseDao<BsCompanyIndustry> {

    List<BsCompanyIndustry> findByGrand(Integer grand);


}
