package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

public interface CompanyUserDao extends BaseDao<CompanyUser> {

    @Query(value = "select u from CompanyUser u where u.loginPhone=?1 and u.enableFlg =?2 ")
    CompanyUser findByLoginPhone(String loginPhone, boolean enableFlg);

    @Query(value = "select u from CompanyUser u where u.id=?1 and u.enableFlg =?2 ")
    CompanyUser findByUserid(Long userid, boolean enableFlg);

    CompanyUser findByOpenIdAndEnableFlgTrue(String openId);

}
