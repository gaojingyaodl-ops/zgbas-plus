package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.server.dao.WxSmsCheckCodeDao;
import com.spt.bas.purchase.wx.server.entity.WxSmsCheckCode;
import com.spt.bas.purchase.wx.server.service.IWxSmsCheckCodeService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  短信验证码
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-15 18:03
 */
@Component
@Transactional(readOnly = true)
public class WxSmsCheckCodeService extends BaseService<WxSmsCheckCode> implements IWxSmsCheckCodeService {

    private static Logger logger = LoggerFactory.getLogger(WxSmsCheckCodeService.class);

    @Autowired
    private WxSmsCheckCodeDao wxSmsCheckCodeDao;


    @Override
    public BaseDao<WxSmsCheckCode> getBaseDao() {
        return wxSmsCheckCodeDao;
    }




}
