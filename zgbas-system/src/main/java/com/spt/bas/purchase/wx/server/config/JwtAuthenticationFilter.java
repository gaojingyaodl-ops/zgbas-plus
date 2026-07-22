// Phase 4 stub — Phase 5 will overlay with complete source version
// D-06/D-07: @Component removed (registered via FilterRegistrationBean in WxSecurityConfig),
//            CustomConfig/@Autowired removed, initIgnores()/@PostConstruct removed,
//            checkIgnores() simplified to return false (FilterRegistrationBean urlPatterns handle routing).
package com.spt.bas.purchase.wx.server.config;

import cn.hutool.core.util.StrUtil;
import com.spt.bas.purchase.wx.client.entity.UserDetail;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.dao.CompanyUserDao;
import com.spt.bas.purchase.wx.server.dao.UserDetailDao;
import com.spt.bas.purchase.wx.server.exception.SecurityException;
import com.spt.bas.purchase.wx.server.util.JwtUtil;
import com.spt.bas.purchase.wx.server.util.ResponseUtil;
import com.spt.bas.purchase.wx.server.util.UserContext;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 *  jwt 认证过滤器
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-07-25 15:38
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CompanyUserDao companyUserDao;

    @Autowired
    private UserDetailDao userDetailDao;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, SecurityException, IOException {
        // 忽略
        if (checkIgnores(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String jwt = jwtUtil.getJwtFromRequest(httpServletRequest);
        if (StrUtil.isBlank(jwt)) {
            ResponseUtil.renderJson(httpServletResponse, Status.NO_ACCESS_TOKEN, null);
        } else {
            if (StrUtil.isNotBlank(jwt)) {
                try {
                    Claims claims = jwtUtil.parseJWT(jwt);
                    Long userId = Long.valueOf(claims.getId());
                    UserDetail userDetail = userDetailDao.findByUserIdAndEnableFlgTrueAndIsBindTrue(userId);
                    Long companyId = null;
                    if (userDetail != null) {
                        companyId = userDetail.getCompanyId();
                    }
                    UserInfoVo build = UserInfoVo.builder()
                            .userId(userId)
                            .phone(claims.getSubject())
                            .companyId(companyId)
                            .build();
                    UserContext.setUser(build);
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                } catch (SecurityException e) {
                    ResponseUtil.renderJson(httpServletResponse, e);
                }
            } else {
                ResponseUtil.renderJson(httpServletResponse, Status.UNAUTHORIZED, null);
            }
        }
    }

    /**
     * 请求是否不需要进行权限拦截
     * D-07: checkIgnores simplified to return false — FilterRegistrationBean urlPatterns
     * (/wx/*, /ewechat/*, /axq/*) already limit filter scope; no per-request path matching needed.
     * @param request
     * @return always false
     */
    private boolean checkIgnores(HttpServletRequest request) {
        return false;
    }
}
