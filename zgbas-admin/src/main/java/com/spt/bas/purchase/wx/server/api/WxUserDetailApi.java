package com.spt.bas.purchase.wx.server.api;

import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.server.dao.CompanyUserDao;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.client.entity.CompanyUser;
import com.spt.bas.purchase.wx.server.service.impl.UserDetailService;
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
@RequestMapping(value = "purchase/userDetail")
public class WxUserDetailApi extends BaseApi<UserDetail> {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private UserDetailDao userDetailDao;

    @Autowired
    private CompanyUserDao companyUserDao;

    @Override
    public IDataService<UserDetail> getService() {
        return userDetailService;
    }

    @RequestMapping(value = "findByCompanyIdAndIsBindTrue")
    public UserDetail findByCompanyIdAndIsBindTrue(@RequestBody Long companyId){
        return userDetailDao.findByCompanyIdAndIsBindTrue(companyId);
    }

    @RequestMapping(value = "findByCompanyIdAndEnableFlgTrue")
     List<UserDetail>  findByCompanyIdAndEnableFlgTrue(@RequestParam("companyId") Long companyId) {

        return userDetailDao.findByCompanyIdAndEnableFlgTrue(companyId);
    }

    @RequestMapping(value = "findByCompanyIdAndIsBindTrueAndEnableFlgTrue")
    UserDetail findByCompanyIdAndIsBindTrueAndEnableFlgTrue(@RequestBody Long companyId) {
        return userDetailDao.findByCompanyIdAndIsBindTrueAndEnableFlgTrue(companyId);
    }

    @RequestMapping(value = "findByUserId")
    UserDetail findByUserId(@RequestBody Long userId) {
        return userDetailDao.findByUserIdAndEnableFlgTrue(userId);
    }

    @RequestMapping(value = "getUserPhone")
    String getUserPhone(@RequestBody Long userId){
        CompanyUser usr = companyUserDao.findByUserid(userId, true);
        if (usr != null) {
            return usr.getLoginPhone();
        }
        return null;
    }

    @PostMapping(value = "findByContactPhone")
    UserDetail findByContactPhone(@RequestBody String phone){
        CompanyUser companyUser = companyUserDao.findByLoginPhone(phone, true);
        if (companyUser != null) {
            return userDetailDao.findByUserIdAndEnableFlgTrue(companyUser.getId());
        }
        return null;
    }

}
