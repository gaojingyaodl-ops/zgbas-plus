// Phase 4 stub — Phase 5 will overlay with complete source version
package com.spt.bas.purchase.wx.server.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.config.JwtConfig;
import com.spt.bas.purchase.wx.server.entity.WxAccessToken;
import com.spt.bas.purchase.wx.server.exception.SecurityException;
import com.spt.bas.purchase.wx.server.service.impl.WxAccessTokenService;
import com.spt.bas.purchase.wx.server.vo.UserInfoVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.http.util.TokenUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName JwtUtil
 * @Author shengong
 * @Date 2020-07-24 22:33
 * @Description TODO
 */
@EnableConfigurationProperties(JwtConfig.class)
@Configuration
@Slf4j
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private WxAccessTokenService wxAccessTokenService;


    /**
     * 创建JWT
     *
     * @param userInfoVo 用户信息
     * @return JWT
     */
    public String createJWT(UserInfoVo userInfoVo) {
        try {
            return createJWT(true, userInfoVo.getUserId(), userInfoVo.getPhone());
        } catch (Exception e) {
            log.error("创建JWT失败 UserInfoVo:{}", userInfoVo, e);
            throw new BaseException(Status.ERROR, "创建JWT失败");
        }
    }

    /**
     * 创建JWT
     *
     * @param rememberMe 记住我
     * @param id         用户id
     * @param loginName    用户名(手机号)
     * @return
     */
    @Transactional
    public String createJWT(Boolean rememberMe, Long id, String loginName) {
        try {
            // 设置过期时间
            Long ttl = rememberMe ? jwtConfig.getRemember() : jwtConfig.getTtl();
            String jwt = TokenUtil.createToken(id.toString(), loginName, null, jwtConfig.getKey(), ttl.intValue());
            // 将生成的jwt保存到数据库中
            WxAccessToken wxAccessToken = wxAccessTokenService.findByUserid(id);
            if (wxAccessToken == null) {
                wxAccessToken = new WxAccessToken();
            }
            wxAccessToken.setUserId(id);
            wxAccessToken.setAccessToken(jwt);
            wxAccessTokenService.save(wxAccessToken);
            return jwt;
        } catch (Exception e) {
            log.error("创建JWT失败 id:{},loginName:{}", id, loginName, e);
            throw new BaseException(Status.ERROR, "创建JWT失败");
        }
    }

    /**
     * 设置jwt过期
     *
     * @param request 请求
     */
    public void invildJWT(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        String username = getUsernameFromJWT(jwt);
        String userid = getUseridFromJWT(jwt);
        wxAccessTokenService.deleteByUserid(userid);
    }

    /**
     * 刷新token
     * @param userid
     * @param phone
     * @return jwt
     * @throws ApplicationException
     */
    public String refreshJWT(Long userid, String phone) {
        return createJWT(true, userid, phone);
    }


    /**
     * 根据jwt获取用户名
     * @param jwt JWT
     * @return 用户名
     */
    public String getUsernameFromJWT(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.getSubject();
    }

    /**
     * 根据jwt获取用户id
     * @param jwt
     * @return userid
     */
    public String getUseridFromJWT(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.getId();
    }

    /**
     * 解析JWT
     * @param jwt JWT
     * @return Claims
     */
    public Claims parseJWT(String jwt) {
        String userId = null;
        try {
            Claims claims;
            // TokenUtil.parseJWT declares throws Exception (UnsupportedEncodingException from "utf-8");
            // wrap to suppress the checked exception — "utf-8" is always supported in Java.
            try {
                claims = TokenUtil.parseJWT(jwt, jwtConfig.getKey());
            } catch (RuntimeException re) {
                throw re; // rethrow JwtException subtypes for outer catch blocks
            } catch (Exception e) {
                log.error("JWT parse system error", e);
                throw new SecurityException(Status.TOKEN_PARSE_ERROR);
            }
            userId = claims.getId();
            // 检验数据库中的JWT是否与当前一致，不一致则代表用户已注销/用户在不同设备登录，均代表JWT已过期
            try {
                String token = wxAccessTokenService.findByUserid(Long.valueOf(userId)).getAccessToken();
                if (!StrUtil.equals(token, jwt)) {
                    throw new SecurityException(Status.TOKEN_OUT_OF_CTRL);
                }
            } catch (Exception e) {
                throw new SecurityException(Status.TOKEN_OUT_OF_CTRL);
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("Token过期,userId : {}", userId);
            throw new SecurityException(Status.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 Token");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (MalformedJwtException e) {
            log.error("Token 无效");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (SignatureException e) {
            log.error("无效的 Token 签名");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Token 参数不存在");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        }
    }

    /**
     * 从 request 的 header或者body 中获取JWT
     * @param request 请求
     * @return jwt
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("token:{}", bearerToken);
        if (StrUtil.isNotBlank(bearerToken)) {
            return bearerToken;
        }
        String jwtFromRequestBody = getJwtFromRequestBody(request);
        if (StrUtil.isNotBlank(jwtFromRequestBody)) {
            return jwtFromRequestBody;
        }
        return null;
    }

    /**
     * 获取userid
     * @param request
     * @return
     */
    public String getUseridFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String userId = null;
        if (StrUtil.isNotBlank(bearerToken)) {
            try {
                return parseJWT(bearerToken).getId();
            } catch (ExpiredJwtException e) {
                log.warn("Token过期,userId : {}", userId);
                throw new SecurityException(Status.TOKEN_EXPIRED);
            } catch (UnsupportedJwtException e) {
                log.error("不支持的 Token");
                throw new SecurityException(Status.TOKEN_PARSE_ERROR);
            } catch (MalformedJwtException e) {
                log.error("Token 无效");
                throw new SecurityException(Status.TOKEN_PARSE_ERROR);
            } catch (SignatureException e) {
                log.error("无效的 Token 签名");
                throw new SecurityException(Status.TOKEN_PARSE_ERROR);
            } catch (IllegalArgumentException e) {
                log.error("Token 参数不存在");
                throw new SecurityException(Status.TOKEN_PARSE_ERROR);
            }
        }
        return null;
    }

    /**
     * 获取userid
     * @param jwt
     * @return
     */
    public String getUseridFromJwt(String jwt) {
        String userId = null;
        try {
            return parseJWT(jwt).getId();
        } catch (ExpiredJwtException e) {
            log.warn("Token过期,userId : {}", userId);
            throw new SecurityException(Status.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 Token");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (MalformedJwtException e) {
            log.error("Token 无效");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (SignatureException e) {
            log.error("无效的 Token 签名");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Token 参数不存在");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        }
    }

    /**
     * 从request中获取jwt
     * @param request
     * @return jwt
     */
    public String getJwtFromRequestBody(HttpServletRequest request) {
        String jwt = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonObject = JSONUtil.parseObj(responseStrBuilder.toString());
            Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                if (StrUtil.equals(next.getKey(), "accessToken")) {
                    jwt = (String) next.getValue();
                }
            }
            IoUtil.close(streamReader);
        } catch (Exception e) {
            return jwt;
        }
        return jwt;
    }
}
