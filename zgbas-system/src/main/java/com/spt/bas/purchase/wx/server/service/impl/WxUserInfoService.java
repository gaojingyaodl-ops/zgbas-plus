package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.server.dao.WxUserInfoDao;
import com.spt.bas.purchase.wx.server.entity.WxUserInfo;
import com.spt.bas.purchase.wx.server.service.IWxUserInfoService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  微信用户详细信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-18 10:48
 */
@Component
public class WxUserInfoService extends BaseService<WxUserInfo> implements IWxUserInfoService {

    @Autowired
    private WxUserInfoDao wxUserInfoDao;

    @Override
    public BaseDao<WxUserInfo> getBaseDao() {
        return wxUserInfoDao;
    }


}
