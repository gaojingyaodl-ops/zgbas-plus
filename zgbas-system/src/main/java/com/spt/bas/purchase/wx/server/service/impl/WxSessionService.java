package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.server.dao.WxSessionDao;
import com.spt.bas.purchase.wx.server.entity.WxSession;
import com.spt.bas.purchase.wx.server.service.IWxSessionService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  微信登录信息表 保存用户openId和session_key
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 11:42
 */
@Component
public class WxSessionService extends BaseService<WxSession> implements IWxSessionService {

    @Autowired
    private WxSessionDao wxSessionDao;

    @Override
    public BaseDao<WxSession> getBaseDao() {
        return wxSessionDao;
    }

}
