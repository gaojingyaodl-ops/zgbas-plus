package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.server.dao.WxUserTextReadDao;
import com.spt.bas.purchase.wx.server.entity.WxUserTextRead;
import com.spt.bas.purchase.wx.server.service.IWxUserTextReadService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WxUserTextReadServiceImpl extends BaseService<WxUserTextRead> implements IWxUserTextReadService {

    @Autowired
    private WxUserTextReadDao wxUserTextReadDao;

    @Override
    public BaseDao<WxUserTextRead> getBaseDao() {
        return wxUserTextReadDao;
    }

    @Override
    public WxUserTextRead findByUserIdAndTextType(Long userId, String textType) {
        return wxUserTextReadDao.findByUserIdAndTextType(userId, textType);
    }

    @Override
    public void saveWxUserTextRead(WxUserTextRead wxUserTextRead) {
        wxUserTextReadDao.save(wxUserTextRead);
    }
}
