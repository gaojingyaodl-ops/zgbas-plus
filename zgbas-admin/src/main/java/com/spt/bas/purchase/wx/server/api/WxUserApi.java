package com.spt.bas.purchase.wx.server.api;

import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.client.vo.CompanyOnLineApplyVo;
import com.spt.bas.purchase.wx.server.dao.CompanyUserDao;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.server.service.impl.UserService;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.data.service.BaseApi;
import com.spt.tools.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-09 12:41
 */
@RestController
@RequestMapping(value = "purchase/user")
public class WxUserApi extends BaseApi<CompanyUser> {

    @Autowired
    private UserService userService;
    
    @Override
    public IDataService<CompanyUser> getService() {
        return userService;
    }

    @RequestMapping(value = "saveApplyOnLineData")
    public RespVo<CompanyUser> saveApplyOnLineData(@RequestBody CompanyOnLineApplyVo vo){
        return userService.saveApplyOnLineData(vo);
    }

}
