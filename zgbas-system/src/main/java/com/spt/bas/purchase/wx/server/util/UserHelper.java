package com.spt.bas.purchase.wx.server.util;

import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-24 11:21
 */
@Slf4j
public class UserHelper {

    /**
     * 获取当前用户
     * @return
     */
    public static UserInfoVo getUser() {
        UserInfoVo sysUser = UserContext.getUser();
        if (null == sysUser) {
            throw new BaseException(Status.UNAUTHORIZED);
        }
        return sysUser;
    }

    /**
     * 获取当前用户id
     * @return
     */
    public static Long getCurUserId() {
        return getUser().getUserId();
    }

    /**
     * 获取当前用户绑定的公司id
     * @return
     */
    public static Long getCurBindCompanyId() {
        return getUser().getCompanyId();
    }

}
