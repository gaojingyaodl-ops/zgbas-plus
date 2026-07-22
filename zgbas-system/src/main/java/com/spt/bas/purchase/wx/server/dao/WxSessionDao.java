package com.spt.bas.purchase.wx.server.dao;

import com.spt.bas.purchase.wx.server.entity.WxSession;
import com.spt.tools.jpa.dao.BaseDao;

public interface WxSessionDao extends BaseDao<WxSession> {
    WxSession findByOpenId(String openId);
}
