package com.spt.bas.web.controller.bs;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.entity.BsMatchProfitsConfig;
import com.spt.bas.client.remote.IBsMatchProfitsConfigClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.SingleCrudControll;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 业务员利润计算配置表
 */
@Controller
@RequestMapping("/bs/match")
public class BsMatchProfitsConfigController extends SingleCrudControll<BsMatchProfitsConfig, BaseVo> {

    @Autowired
    private IBsMatchProfitsConfigClient matchProfitsConfigClient;

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<BsMatchProfitsConfig> getService() {
        return matchProfitsConfigClient;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        return "bs/matchProfitsConfig";
    }

    /**
     * 分页查询
     *
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findMatchProfitsConfigPage")
    public void findAllConfig(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<BsMatchProfitsConfig> page = matchProfitsConfigClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }


    /**
     * 修改方法
     *
     * @param request
     * @param response
     */
    @RequestMapping("updateConfig")
    public void updateConfig(HttpServletRequest request, HttpServletResponse response) {
        Long matchUserId = Long.valueOf(request.getParameter("matchUserId"));//业务员Id
        Long id = Long.valueOf(request.getParameter("id"));//Id
        SysUserSdk SysUserSdk = authOpenFacade.findUserById(matchUserId);
        List<BsMatchProfitsConfig> byMathUserId = matchProfitsConfigClient.findByMathUserId(matchUserId);
        String matchUserName = SysUserSdk.getNickName();//业务员姓名
        BsMatchProfitsConfig bsMatchProfitsConfig = new BsMatchProfitsConfig();
        bsMatchProfitsConfig.setId(id);
        bsMatchProfitsConfig.setMatchUserId(matchUserId);
        bsMatchProfitsConfig.setMatchUserName(matchUserName);
        bsMatchProfitsConfig.setBuyCommissionRate(new BigDecimal(request.getParameter("buyCommissionRate")));//采购提成比率
        bsMatchProfitsConfig.setSellCommissionRate(new BigDecimal(request.getParameter("sellCommissionRate")));//销售提成比率
        bsMatchProfitsConfig.setMarketingRate(new BigDecimal(request.getParameter("marketingRate")));//营销留存比率
        bsMatchProfitsConfig.setCompanyRate(new BigDecimal(request.getParameter("companyRate")));//公司净利比率
        bsMatchProfitsConfig.setBuyHeadCommissionRate(new BigDecimal(request.getParameter("buyHeadCommissionRate")));//采购团队负责人提成比率
        bsMatchProfitsConfig.setSellHeadCommissionRate(new BigDecimal(request.getParameter("sellHeadCommissionRate")));//销售团队负责人提成比率
        if (byMathUserId.size() > 0){
            if (byMathUserId.get(0).getId() == id){
                try {
                    matchProfitsConfigClient.save(bsMatchProfitsConfig);
                    RenderUtil.renderSuccess("修改成功", response);
                } catch (Exception e) {
                    e.printStackTrace();
                    RenderUtil.renderSuccess("修改失败", response);
                }
            }else{
                RenderUtil.renderFailure("业务员重复，不可修改", response);
            }
        }else {
            try {
                matchProfitsConfigClient.save(bsMatchProfitsConfig);
                RenderUtil.renderSuccess("修改成功", response);
            } catch (Exception e) {
                e.printStackTrace();
                RenderUtil.renderSuccess("修改失败", response);
            }
        }
    }

    /**
     * 跳转到详情页
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        BsMatchProfitsConfig entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsMatchProfitsConfig();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "bs/matchProfitsConfig-detail";

    }

    @ModelAttribute("preload")
    public BsMatchProfitsConfig getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                BsMatchProfitsConfig entity = new BsMatchProfitsConfig();
                entity.setId(0L);
                return entity;
            }
        }
        return null;
    }

    /**
     * 删除方法
     *
     * @param id
     * @param request
     * @param response
     * @return
     */
    @Override
    @RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            matchProfitsConfigClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 添加方法
     *
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void saveBsConfig(HttpServletRequest request, HttpServletResponse response) {
        Long matchUserId = Long.valueOf(request.getParameter("matchUserId"));//业务员Id
        List<BsMatchProfitsConfig> byMathUserId = matchProfitsConfigClient.findByMathUserId(matchUserId);
        if (byMathUserId.size()>0){
            RenderUtil.renderFailure("业务员重复，不可添加", response);
        }else {
        SysUserSdk SysUserSdk = authOpenFacade.findUserById(matchUserId);
        String matchUserName = SysUserSdk.getNickName();//业务员姓名
        BsMatchProfitsConfig bsMatchProfitsConfig = new BsMatchProfitsConfig();
        bsMatchProfitsConfig.setMatchUserId(matchUserId);
        bsMatchProfitsConfig.setMatchUserName(matchUserName);
        bsMatchProfitsConfig.setBuyCommissionRate(new BigDecimal(request.getParameter("buyCommissionRate")));//采购提成比率
        bsMatchProfitsConfig.setSellCommissionRate(new BigDecimal(request.getParameter("sellCommissionRate")));//销售提成比率
        bsMatchProfitsConfig.setMarketingRate(new BigDecimal(request.getParameter("marketingRate")));//营销留存比率
        bsMatchProfitsConfig.setCompanyRate(new BigDecimal(request.getParameter("companyRate")));//公司净利比率
        bsMatchProfitsConfig.setBuyHeadCommissionRate(new BigDecimal(request.getParameter("buyHeadCommissionRate")));//采购团队负责人提成比率
        bsMatchProfitsConfig.setSellHeadCommissionRate(new BigDecimal(request.getParameter("sellHeadCommissionRate")));//销售团队负责人提成比率
        try {
            matchProfitsConfigClient.save(bsMatchProfitsConfig);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
        }
    }
}
