package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.server.entity.WxSmsCheckCode;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WxSmsCheckCodeDao extends BaseDao<WxSmsCheckCode> {
    WxSmsCheckCode findByPhone(String phone);

    List<WxSmsCheckCode> findWxSmsCheckCodesByPhone(String phone);

    WxSmsCheckCode findFirstByPhoneOrderByCreatedDateDesc(String phone);

    @Query(value = "select u from WxSmsCheckCode u where u.phone=?1 order by u.createdDate desc ")
    List<WxSmsCheckCode> findValidCodes(String phone);

}
