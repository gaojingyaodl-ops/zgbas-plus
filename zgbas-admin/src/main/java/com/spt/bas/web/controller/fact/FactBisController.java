package com.spt.bas.web.controller.fact;

import com.spt.auth.sdk.entity.SysMenuSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.MenuTableSearchVo;
import com.spt.bas.client.vo.FactMenuVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.tools.http.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 杨英承
 * 考核明细
 * @version 1.0.0
 * @date 2022/5/19 16:14
 */
@Controller
@RequestMapping(value = "/fact")
@Slf4j
public class FactBisController {

    @Value("${fact.bis.secret.secretKey}")
    private String secretKey;
    @Value("${fact.bis.url}")
    private String factLoginUrl;
    @Autowired
    IAuthOpenFacade authOpenFacade;


    /**
     * 管理者考核
     */
    @RequestMapping("/eva")
    public String eva(Model model) {
        List<FactMenuVo> menuList = getMenuList("中后台考核");
        model.addAttribute("menuList", menuList);
        return "fact/common";
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


    /**
     * 行政预算
     */
    @RequestMapping("/productCategory")
    public String productCategory(Model model) {
        List<FactMenuVo> menuList = getMenuList("行政预算");
        model.addAttribute("menuList", menuList);
        return "fact/common";
    }

    /**
     * 档案管理
     */
    @RequestMapping("/classify")
    public String classify(Model model) {
        List<FactMenuVo> menuList = getMenuList("档案管理");
        model.addAttribute("menuList", menuList);
        return "fact/common";
    }

    /**
     * 生成跳转地址
     *
     * @param path 路由地址或路由名称
     * @return 结果
     */
    private String generateTicketUrl(String path) {
        String userName = ShiroUtil.getShiroUser().loginName;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> param = new HashMap<>();
        param.put("loginName", userName);
        String accessToken = TokenUtil.createToken(param, secretKey);
        StringBuilder sb = new StringBuilder(factLoginUrl);
        try {
            sb.append("?path=").append(URLEncoder.encode(path, "UTF-8"));
            sb.append("&loginName=").append(userName);
            sb.append("&timestamp=").append(timestamp);
            sb.append("&accessToken=").append(accessToken);
        } catch (UnsupportedEncodingException e) {
            log.error("地址转换异常：{}", e.toString());
        }
        return sb.toString();
    }


    /**
     * 月度报表
     */
    @RequestMapping("/business/report/monthly")
    public String monthlyBusinessReport(Model model) {
        List<FactMenuVo> menuList = getMenuList("月度报表");
        model.addAttribute("menuList", menuList);
        return "fact/common";
    }

    /**
     * 季度报表
     */
    @RequestMapping("/business/report/quarterly")
    public String quarterlyBusinessReport(Model model) {
        List<FactMenuVo> menuList = getMenuList("季度报表");
        model.addAttribute("menuList", menuList);
        return "fact/common";
    }


}
