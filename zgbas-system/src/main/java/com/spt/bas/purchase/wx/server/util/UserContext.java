// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.util;

import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-24 11:22
 */
@Slf4j
public class UserContext {
    private static ThreadLocal<UserInfoVo> threadLocal = new ThreadLocal<>();

    /**
     * 设置用户信息
     * @param user
     */
    public static void setUser(UserInfoVo user) {
        threadLocal.set(user);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserInfoVo getUser() {
        UserInfoVo user = threadLocal.get();
        return user;
    }

    /**
     * 移除用户
     */
    public static void removeUser() {
        threadLocal.remove();
    }
}
