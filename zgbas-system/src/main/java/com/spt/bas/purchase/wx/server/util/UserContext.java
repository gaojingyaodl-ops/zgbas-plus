package com.spt.bas.purchase.wx.server.util;

import com.spt.bas.purchase.wx.server.vo.CompanyVo;
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
    public static void setUser(UserInfoVo user){
//        log.info("当前线程 --- [{}] --- 设置用户 {} ", Thread.currentThread().getName(), user);
        threadLocal.set(user);
    }

    /**
     * 获取用户信息
     * @return
     */
    public static UserInfoVo getUser() {
                UserInfoVo user = threadLocal.get();
//        log.info("当前线程 --- [{}] --- 获取用户 {} ", Thread.currentThread().getName(), user);
        return user;
    }

    /**
     * 移除用户
     */
    public static void removeUser() {
        threadLocal.remove();
    }

}
