package com.spt.bas.web.controller.trade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.spt.auth.sdk.entity.SysMenuSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.MenuTableSearchVo;
import com.spt.bas.client.vo.FactMenuVo;
import com.spt.bas.web.controller.trade.VO.TradeMenuVO;
import com.spt.bas.web.controller.trade.VO.TradeRespVO;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.web.util.RenderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * <p>
 * 核心供应链系统跳转采销系统入口
 * @version 1.0.0
 * @date 2025/6/24 16:14
 */
@Controller
@RequestMapping(value = "/bas/trade")
@Slf4j
public class BasTradeController {

    @Value("${trade.app.secret}")
    private String secretKey;
    @Value("${trade.login.url}")
    private String TRADE_LOGIN_URL;
    @Autowired
    IAuthOpenFacade authOpenFacade;


    /**
     * 管理者考核
     */
    @RequestMapping("/menu")
    public void getMenu(HttpServletResponse resp) {
        String userName = ShiroUtil.getShiroUser().loginName;
        Long userId = ShiroUtil.getShiroUser().id;
        Map<String, Object> param = new HashMap<>();
        param.put("loginName", userName);
        param.put("userId", userId);
        String ticket = SecureUtil.aes(secretKey.getBytes()).encryptHex(JSONUtil.toJsonStr(param));
        StringBuilder sb = new StringBuilder(TRADE_LOGIN_URL + "/admin-api/system/oauth2/get-trade-menu");
        sb.append("?loginName=").append(userName);
        sb.append("&timestamp=").append(System.currentTimeMillis());
        sb.append("&ticket=").append(ticket);
        HttpResponse response = HttpUtil.createGet(sb.toString()).execute();
        if (Objects.isNull(response)) {
            Assert.isNull(response, "请求采销中心失败！");
        }
        String body = response.body();
        log.info("请求结果：{}", body);
        TradeRespVO<List<TradeMenuVO>> result = JSONUtil.toBean(body, new TypeReference<TradeRespVO<List<TradeMenuVO>>>() {
        }, false);
        List<TradeMenuVO> menuVOList = result.getData();
        if (CollUtil.isNotEmpty(menuVOList)) {
            for (TradeMenuVO menuVO : menuVOList) {
                menuVO.setUrl(generateTicketUrl(menuVO.getPath()));
            }
        }
        RenderUtil.renderJson(JsonUtil.obj2Json(menuVOList), resp);
    }

    private List<FactMenuVo> getMenuList(String menuName) {
        MenuTableSearchVo searchVo = new MenuTableSearchVo();
        searchVo.setMenuName(menuName);
        searchVo.setAppCode("bis");
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        List<SysMenuSdk> menus = authOpenFacade.findMenuByMenuName(searchVo);
        List<FactMenuVo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(menus)) {
            result = menus.stream().map(e -> {
                FactMenuVo factMenuVo = new FactMenuVo();
                factMenuVo.setName(e.getMenuName());
                factMenuVo.setIcon(e.getIcon());
                factMenuVo.setUrl(generateTicketUrl(e.getRouterPath()));
                return factMenuVo;
            }).collect(Collectors.toList());
        }
        return result;
    }


    ///**
    // * 行政预算
    // */
    //@RequestMapping("/productCategory")
    //public String productCategory(Model model) {
    //    List<FactMenuVo> menuList = getMenuList("行政预算");
    //    model.addAttribute("menuList", menuList);
    //    return "fact/common";
    //}
    //
    ///**
    // * 档案管理
    // */
    //@RequestMapping("/classify")
    //public String classify(Model model) {
    //    List<FactMenuVo> menuList = getMenuList("档案管理");
    //    model.addAttribute("menuList", menuList);
    //    return "fact/common";
    //}

    /**
     * 生成跳转地址
     *
     * @param path 路由地址或路由名称
     * @return 结果
     */
    private String generateTicketUrl(String path) {
        String userName = ShiroUtil.getShiroUser().loginName;
        Long userId = ShiroUtil.getShiroUser().id;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> param = new HashMap<>();
        param.put("loginName", userName);
        param.put("userId", userId);
        String ticket = SecureUtil.aes(secretKey.getBytes()).encryptHex(JSONUtil.toJsonStr(param));
        //StringBuilder sb = new StringBuilder(TRADE_LOGIN_URL+"/zg-sso");
        StringBuilder sb = new StringBuilder(TRADE_LOGIN_URL + "/zg-sso");
        try {
            sb.append("?path=").append(URLEncoder.encode(path, "UTF-8"));
            sb.append("&loginName=").append(userName);
            sb.append("&timestamp=").append(timestamp);
            sb.append("&ticket=").append(ticket);
        } catch (UnsupportedEncodingException e) {
            log.error("地址转换异常：{}", e.toString());
        }
        return sb.toString();
    }


}
