package com.spt.bas.purchase.wx.server.service.impl;

import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.server.service.IUserDetailService;
import com.spt.tools.jpa.dao.BaseDao;
import com.spt.tools.jpa.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 15:24
 */
@Component
@Transactional
public class UserDetailService extends BaseService<UserDetail> implements IUserDetailService {

    @Autowired
    private UserDetailDao userDetailDao;

    @Override
    public BaseDao<UserDetail> getBaseDao() {
        return userDetailDao;
    }


}
