package com.spt.bas.purchase.wx.server.listener;

import com.spt.bas.purchase.wx.server.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  web 请求监听器
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-24 11:19
 */
@Component
@Slf4j
public class RequestListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
//        HttpServletRequest request  = (HttpServletRequest) sre.getServletRequest();
//        log.info("当前线程 --- [{}] --- 销毁请求：{}", Thread.currentThread().getName() , request.getRequestURI());

        /**
         * 移除当前线程绑定的用户数据，防止内存泄露
         */
        UserContext.removeUser();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
//        HttpServletRequest request  = (HttpServletRequest) sre.getServletRequest();
//        log.info("当前线程 --- [{}] --- 创建请求：{}", Thread.currentThread().getName() ,request.getRequestURI());
    }
}
