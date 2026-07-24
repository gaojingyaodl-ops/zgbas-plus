package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.purchase.wx.server.entity.WxUserTextRead;
import com.spt.tools.jpa.service.IBaseService;

public interface IWxUserTextReadService extends IBaseService<WxUserTextRead> {
    WxUserTextRead findByUserIdAndTextType(Long userId, String textType);

    void saveWxUserTextRead(WxUserTextRead wxUserTextRead);
}
