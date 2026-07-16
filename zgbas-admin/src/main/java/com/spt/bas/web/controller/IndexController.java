/**
 *
 */
package com.spt.bas.web.controller;

import cn.hutool.core.convert.Convert;
import com.ruoyi.common.constant.ShiroConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.spt.auth.sdk.cache.ConfigUtil;
import com.spt.auth.sdk.entity.SysMenuSdk;
import com.spt.auth.sdk.entity.SysRoleSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.MenuSearchVo;
import com.spt.auth.sdk.vo.UserChangeVo;
import com.spt.auth.sdk.vo.UserLoginVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IApproveWaitDealClient;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.client.vo.ApproveWaitSearchVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.*;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.core.number.NumberUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangjian
 *
 */
@Controller
@RequestMapping(value = "")
public class IndexController {
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    // STUB (D-P3-10): Phase-4 FeignClient contract not yet migrated. required=false so
    // startup succeeds without a bean; null-guards degrade business-data calls.
    @Autowired(required = false)
    private IPmProcessClient processClient;
    @Autowired(required = false)
    private IApproveWaitDealClient waitDealClient;
    @Autowired(required = false)
    private WebParamUtils webParamUtils;

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static final String BUSINESS_PROCESS = "业务流程";
    private static final String ADMIN_PROCESS = "企管流程";

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap) {
        mmap.put("version", "");
        return "main";
    }

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap) {
        queryMenu(mmap);
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        mmap.put("sideTheme", ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.sideTheme"));
        mmap.put("skinName", ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.skinName"));
        Boolean footer = Convert.toBool(ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.footer"), true);
        Boolean tagsView = Convert.toBool(ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.tagsView"), true);
        mmap.put("footer", footer);
        mmap.put("tagsView", tagsView);
        mmap.put("mainClass", contentMainClass(footer, tagsView));
        mmap.put("copyrightYear", "");
        mmap.put("demoEnabled", Convert.toBool(ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.demoEnabled"), false));
        mmap.put("isDefaultModifyPwd", false);
        mmap.put("isPasswordExpired", false);
        mmap.put("isMobile", ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")));
        // 工作台访问权限
        mmap.put("workBenchViewFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_WORK_BENCH_VIEW.getPermissionCode()));
        // 业务经理工作台权限
        mmap.put("businessManagerWorkBenchViewFlg", ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_MANAGER_WORK_BENCH_VIEW.getPermissionCode()));
        // 业务总览访问权限
        mmap.put("businessOverviewPerm", ShiroUtil.isPermitted(PermissionEnum.PERM_BUSINESS_OVERVIEW_VIEW.getPermissionCode()));
        // 审批流程菜单访问权限
        mmap.put("approveProcessMenuViewFlag", getApproveProcessMenuViewFlag());
        mmap.put("configViewPerm", ShiroUtil.isPermitted(PermissionEnum.PERM_CONFIG_VIEW.getPermissionCode()));
        mmap.put("stockInquiryPerm", ShiroUtil.isPermitted(PermissionEnum.PERM_STOCK_INQUIRY.getPermissionCode()));
        // 菜单导航显示风格
        String menuStyle = ConfigUtil.getValue(BasConstants.APP_CODE, "sys.index.menuStyle");
        // 移动端，默认使左侧导航菜单，否则取默认配置
        String indexStyle = ServletUtils.checkAgentIsMobile(ServletUtils.getRequest().getHeader("User-Agent")) ? "index" : menuStyle;

        // 优先Cookie配置导航菜单
        Cookie[] cookies = ServletUtils.getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (StringUtils.isNotEmpty(cookie.getName()) && "nav-style".equalsIgnoreCase(cookie.getName())) {
                indexStyle = cookie.getValue();
                break;
            }
        }
        // STUB guard (D-P3-10): waitDealClient/webParamUtils absent until Phase 4
        if (waitDealClient != null) {
            Long userWaitDealNum = waitDealClient.getUserWaitDealNum(new ApproveWaitSearchVo(ShiroUtil.getCurrentUserId().toString()));
            mmap.put("waitDealNum", webParamUtils != null ? webParamUtils.formatterWaitDealNum(userWaitDealNum) : "0");
        } else {
            mmap.put("waitDealNum", "0");
        }
        boolean fundCompanyFlg = ShiroUtil.isPermitted(PermissionEnum.APPROVE_FUND_RECHARGE.getPermissionCode());
        mmap.put("fundCompanyFlg", fundCompanyFlg);
        if (Boolean.TRUE.equals(fundCompanyFlg)) {
            if (webParamUtils != null) {
                BsCompanyDcsx fundCompany = webParamUtils.queryFundCompany();
                if (Objects.isNull(fundCompany)) {
                    mmap.put("fundCompanyFlg", false);
                } else {
                    BigDecimal fundAmountQg = fundCompany.getFundAmountQg();
                    if (fundAmountQg == null) {
                        fundAmountQg = BigDecimal.ZERO;
                    }
                    BigDecimal fundAmountWs = fundCompany.getFundAmountWs();
                    if (fundAmountWs == null) {
                        fundAmountWs = BigDecimal.ZERO;
                    }
                    BigDecimal fundAmount = fundAmountQg.add(fundAmountWs);
                    String fundAmountQgStr = (Objects.isNull(fundAmountQg) || fundAmountQg.compareTo(BigDecimal.ZERO) == 0)
                            ? "0.00"
                            : NumberUtil.formatNumber(fundAmountQg, "#,###.00");
                    String fundAmountWsStr = (Objects.isNull(fundAmountWs) || fundAmountWs.compareTo(BigDecimal.ZERO) == 0)
                            ? "0.00"
                            : NumberUtil.formatNumber(fundAmountWs, "#,###.00");
                    String fundAmountStr = (Objects.isNull(fundAmount) || fundAmount.compareTo(BigDecimal.ZERO) == 0)
                            ? "0.00"
                            : NumberUtil.formatNumber(fundAmount, "#,###.00");
                    mmap.put("fundAmount", fundAmountStr);
                    mmap.put("fundAmountQg", fundAmountQgStr);
                    mmap.put("fundAmountWs", fundAmountWsStr);
                    mmap.put("fundCompanyId", fundCompany.getId());
                    mmap.put("fundCompanyName", fundCompany.getCompanyAbbr());
                }
            } else {
                // webParamUtils absent — disable fund view gracefully
                mmap.put("fundCompanyFlg", false);
            }
        }
        return "topnav".equalsIgnoreCase(indexStyle) ? "index-topnav" : "index";
    }

    // 提取字符串中的数字部分
    public static int extractNumber(String key) {
        // 使用正则表达式提取最后的数字串
        String numStr = key.replaceAll("\\D", "");
        if(StringUtils.isEmpty(numStr)){
            return 0;
        } else {
            return Integer.parseInt(numStr);
        }
    }

    /**
     * 判断是否有审批流程tab菜单的访问权限
     *
     * @return false-没有，true-有
     */
    private Boolean getApproveProcessMenuViewFlag() {
        Long currentUserId = ShiroUtil.getCurrentUserId();
        if(Objects.isNull(currentUserId)){
            return false;
        }
        // 找出当前用户的所有角色
        List<SysRoleSdk> roleList = authOpenFacade.findRoleByUserId(currentUserId);
        List<BsDictData>  approveProcessList = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.APPROVEPERMISSION);
        Set<String> approveProcessRoleSet = approveProcessList.stream().filter(BsDictData::getEnableFlg).map(BsDictData::getDictCd).collect(Collectors.toSet());
        // 角色列表不为空，并且在可以访问审批流程tab菜单的数据字典里面
        return CollectionUtils.isNotEmpty(roleList) && roleList.stream().anyMatch(e -> approveProcessRoleSet.contains(e.getRoleKey()));
    }

    // 锁定屏幕
    @GetMapping("/lockscreen")
    public String lockscreen(ModelMap mmap) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        mmap.put("user", user);
        com.ruoyi.common.utils.ServletUtils.getSession().setAttribute(ShiroConstants.LOCK_SCREEN, true);
        return "lock";
    }

    // 解锁屏幕
    @PostMapping("/unlockscreen")
    @ResponseBody
    public AjaxResult unlockscreen(String password) {
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        if (com.ruoyi.common.utils.StringUtils.isNull(user)) {
            return AjaxResult.error("服务器超时，请重新登录");
        }
        UserLoginVo vo = new UserLoginVo();
        vo.setPassword(password);
        vo.setUserId(ShiroUtil.getCurrentUserId());
        if (authOpenFacade.isPwdEqual(vo)) {
            com.ruoyi.common.utils.ServletUtils.getSession().removeAttribute(ShiroConstants.LOCK_SCREEN);
            return AjaxResult.success();
        }
        return AjaxResult.error("密码不正确，请重新输入。");
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin() {
        return "skin";
    }

    // 切换菜单
    @GetMapping("/system/menuStyle/{style}")
    public void menuStyle(@PathVariable String style, HttpServletResponse response) {
        CookieUtils.setCookie(response, "nav-style", style);
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {
        Integer passwordValidateDays = Convert.toInt(ConfigUtil.getValue(BasConstants.APP_CODE, "sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {
            if (StringUtils.isNull(pwdUpdateDate)) {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {
        Integer initPasswordModify = Convert.toInt(ConfigUtil.getValue(BasConstants.APP_CODE, "sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // content-main class
    public String contentMainClass(Boolean footer, Boolean tagsView) {
        if (!footer && !tagsView) {
            return "tagsview-footer-hide";
        } else if (!footer) {
            return "footer-hide";
        } else if (!tagsView) {
            return "tagsview-hide";
        }
        return StringUtils.EMPTY;
    }

    private List<SysMenuSdk> findAllMenu() {
        MenuSearchVo searchVo = new MenuSearchVo();
        searchVo.setAppCode(ShiroUtil.appCd);
        searchVo.setAppId(ShiroUtil.getCurrAppId());
        searchVo.setEnableFlg(true);
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        return authOpenFacade.findAllMenu(searchVo);
    }

    @RequestMapping(value = "/index2")
    public String index(Model model) {
        EasyTreeNode treeNode = EasyTreeUtil2.getMenuTree(findAllMenu());
        if (treeNode.getChildren().size() > 0) {
            EasyTreeNode appNode = treeNode.getChildren().get(0);
            for (EasyTreeNode tmp : treeNode.getChildren()) {
                if (tmp.getId().substring(3).equals(ShiroUtil.getCurrAppId() + "")) {
                    appNode = tmp;
                    break;
                }
            }
            if (appNode != null) {
                model.addAttribute("menus", appNode.getChildren());
            }
        }
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setUserId(ShiroUtil.getCurrentUserId());
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        // STUB guard (D-P3-10): processClient absent until Phase 4
        List<PmProcess> processList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();

        // 判断是否是观察员
        model.addAttribute("isGcy", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_GCY.getPermissionCode()));

        searchVo.setProcessGroup(BasConstants.PROCESS_GROUP_BIZ);
        List<PmProcess> approveList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();
        searchVo.setProcessGroup(BasConstants.PROCESS_GROUP_MNG);
        List<PmProcess> sealList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();
        model.addAttribute("approveList", approveList);
        model.addAttribute("sealList", sealList);
        if (ShiroUtil.isPermitted(PermissionEnum.APPROVE_START.getPermissionCode())) {
            model.addAttribute("hasApproveStart", true);
        } else {
            model.addAttribute("hasApproveStart", false);
        }
        List<EasyTreeNode> easyTree = EasyTreeUtil2.getProcessTree(processList);
        model.addAttribute("processTree", JsonUtil.obj2Json(easyTree));
        model.addAttribute("configViewPerm", ShiroUtil.isPermitted(PermissionEnum.PERM_CONFIG_VIEW.getPermissionCode()));
        //查询当前用户角色
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        SysUserSdk user = authOpenFacade.findUserById(ShiroUtil.getCurrentUserId());
        model.addAttribute("user", user);
        return "admin/index";
    }

    /**
     * 修改密码
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param response
     */
    @RequestMapping(value = "changePwd", method = RequestMethod.POST)
    public void changePwd(@Valid @RequestParam("id") Long userId, @RequestParam("oldPwd") String oldPwd,
                          @RequestParam("newPwd") String newPwd, HttpServletResponse response) {
        try {
            UserLoginVo vo = new UserLoginVo();
            vo.setPassword(oldPwd);
            vo.setUserId(userId);
            if (!authOpenFacade.isPwdEqual(vo)) {
                RenderUtil.renderText("error", response);
                return;
            }
            String result = "success";
            UserChangeVo changeVo = new UserChangeVo();
            changeVo.setUserId(userId);
            changeVo.setPassword(newPwd);
            logger.info("修改密码：{},{},{}", userId, oldPwd, newPwd);
            authOpenFacade.updateUser(changeVo);
            RenderUtil.renderText(result, response);
        } catch (Exception e) {
            RenderUtil.renderFailure("保存失败", response);
        }
    }

    /**
     * 更改头像
     *
     * @param vo
     * @param response
     */
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            SysUserSdk user = authOpenFacade.findUserById(vo.getId());
            user.setAvatar(vo.getFileId());
            authOpenFacade.saveUser(user);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }

    private void queryMenu(ModelMap mmap){
        // 根据用户id取出菜单
        EasyTreeNode menus = EasyTreeUtil2.getMenuTree(findAllMenu());
        if (CollectionUtils.isNotEmpty(menus.getChildren())) {
            EasyTreeNode appNode = menus.getChildren().get(0);
            for (EasyTreeNode tmp : menus.getChildren()) {
                if (tmp.getId().substring(3).equals(ShiroUtil.getCurrAppId() + "")) {
                    appNode = tmp;
                    break;
                }
            }
            if (appNode != null) {
                List<EasyTreeNode> lstMenu = menus.getChildren().get(0).getChildren();
                PmProcessSearchVo searchVo = new PmProcessSearchVo();
                searchVo.setUserId(ShiroUtil.getCurrentUserId());
                searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
                searchVo.setProcessGroup(BasConstants.PROCESS_GROUP_BIZ);
                // STUB guard (D-P3-10): processClient absent until Phase 4; menu tree
                // still builds from authOpenFacade.findAllMenu, only biz/mng process
                // sub-groupings are skipped when the client is absent.
                List<PmProcess> approveList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();
                Map<String, List<PmProcess>> bizProcessMap = approveList.stream().filter(p-> StringUtils.isNotBlank(p.getRemark())).collect(Collectors.groupingBy(PmProcess::getRemark));
                // hashMap 无法保证顺序
                LinkedHashMap<String, List<PmProcess>> linkMap = new LinkedHashMap<>();
                if(StringUtils.isNotEmpty(bizProcessMap)){
                    List<Map.Entry<String, List<PmProcess>>> sortedEntries = new ArrayList<>(bizProcessMap.entrySet());
                    sortedEntries.sort(Comparator.comparing(entry -> extractNumber(entry.getKey())));
                    // 构建排序后的LinkedHashMap
                    bizProcessMap.clear();
                    sortedEntries.forEach(entry -> linkMap.put(entry.getKey(), entry.getValue()));
                }
                searchVo.setProcessGroup(BasConstants.PROCESS_GROUP_MNG);
                List<PmProcess> sealList = processClient != null ? processClient.findAccess(searchVo) : Collections.emptyList();

                lstMenu.forEach(m -> {
                    if (StringUtils.equals(BUSINESS_PROCESS, m.getText())) {
                        List<EasyTreeNode> lstChildren = new ArrayList<>();
                        linkMap.forEach((k, v) -> {
                            EasyTreeNode node = new EasyTreeNode();
                            // 剔除排序数字标示
                            node.setText(k.replaceAll("\\d", ""));
                            node.setState(EasyTreeNode.STATE_OPEN);
                            node.addAttr("parentId", m.getId());
                            node.addAttr("isRefresh", "0");
                            node.setChildren(EasyTreeUtil2.createMeneNode(v, ""));
                            lstChildren.add(node);
                        });
                        m.setChildren(lstChildren);
                    } else if (StringUtils.equals(ADMIN_PROCESS, m.getText())) {
                        List<EasyTreeNode> lstChildren = EasyTreeUtil2.createMeneNode(sealList, m.getId());
                        m.setChildren(lstChildren);
                    }
                });
                lstMenu.removeIf(e -> CollectionUtils.isEmpty(e.getChildren()));
                mmap.put("menus", lstMenu);
            }
        }
    }
}
