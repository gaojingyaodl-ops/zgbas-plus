// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.server.dao.WxAccessTokenDao;
import com.spt.bas.purchase.wx.server.entity.WxAccessToken;
import com.spt.bas.purchase.wx.server.service.IWxAccessTokenService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  微信用户登录凭证
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 13:05
 */
@Component
@Transactional(readOnly = true)
public class WxAccessTokenService extends BaseService<WxAccessToken> implements IWxAccessTokenService {

    private static Logger logger = LoggerFactory.getLogger(WxAccessTokenService.class);

    @Autowired
    private WxAccessTokenDao wxAccessTokenDao;

    @Override
    public BaseDao<WxAccessToken> getBaseDao() {
        return wxAccessTokenDao;
    }

    @Override
    public WxAccessToken findByUserid(Long userId) {
        return wxAccessTokenDao.findByUserId(userId);
    }

    @Override
    public void deleteByUserid(String userId) {
        wxAccessTokenDao.deleteByUserId(userId);
    }
}
