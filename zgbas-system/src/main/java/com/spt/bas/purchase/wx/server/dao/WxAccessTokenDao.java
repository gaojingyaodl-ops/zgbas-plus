package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.server.entity.WxAccessToken;
import com.spt.tools.jpa.dao.BaseDao;

/**
 * <p>
 *  微信用户登录凭证
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 13:03
 */
public interface WxAccessTokenDao extends BaseDao<WxAccessToken>{
    WxAccessToken findByUserId(Long userId);

    void deleteByUserId(String userId);
}
