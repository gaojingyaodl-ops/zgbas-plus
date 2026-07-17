package com.spt.bas.web.controller.ass;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.AssComplaints;
import com.spt.bas.client.remote.IAssComplaintsClient;
import com.spt.bas.client.vo.AssComplaintsSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 投诉记录
 * @Author: gaojy
 * @create 2022/5/23 16:21
 * @version: 1.0
 * @description:
 */
@Controller
@RequestMapping("/ass/complaints")
public class AssComplainsController extends SingleCrudControll<AssComplaints, BaseVo> {
    @Autowired
    private IAssComplaintsClient assComplaintsClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<AssComplaints> getService() {
        return assComplaintsClient;
    }

    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    @RequestMapping(value = "")
    public String index(Model model) {
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode userNodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        EasyTreeNode deptNodes = EasyTreeUtil2.getDeptTree(deptList, false,false);
        model.addAttribute("userTree", JsonUtil.obj2Json(userNodes.getChildren()));
        model.addAttribute("deptTree", JsonUtil.obj2Json(deptNodes.getChildren()));
        model.addAttribute("complainsFlg", ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIRE_ALL_COMPLAINTS.getPermissionCode()));
        model.addAttribute("complaintsTypeJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.COMPLAINTS_TYPE)));
        return "ass/complaintsList";
    }

    @RequestMapping(value = "findComplaintsPage")
    public void findUnDeliveryPage(AssComplaintsSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode()) || ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIRE_ALL_COMPLAINTS.getPermissionCode())){
            // 管理员或拥有查询所有投诉权限
            searchVo.setSearchType("A");
        }
        if (ShiroUtil.isPermitted(PermissionEnum.ZGBAS_VIEW_CENTER_COMPLAINTS.getPermissionCode())){
            // 中心负责人可查询本中心所有投诉
            searchVo.setSearchType("C");
        }
        searchVo.setSearchUserId(ShiroUtil.getCurrentUserId());
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        PageDown<AssComplaints> page = assComplaintsClient.findComplaintsPage(searchVo);
        JsonEasyUI.renderJson(response, page, null, null);
    }

    @RequestMapping(value = "saveComplaints", method = RequestMethod.POST)
    public void saveComplaints(AssComplaints assComplaints, HttpServletResponse response) {
        try {
            assComplaints.setFromUserId(ShiroUtil.getCurrentUserId());
            assComplaints.setFromUserName(ShiroUtil.getCurrentUserName());
            assComplaints.setEnterpriseId(ShiroUtil.getEnterpriseId());
            assComplaints.setStatus("0");
            assComplaintsClient.saveComplaints(assComplaints);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveComplaints:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "dealWithComplaints/{id}", method = RequestMethod.POST)
    public void dealWithComplaints(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            AssComplaints entity = assComplaintsClient.getEntity(id);
            if (Objects.nonNull(entity)){
                entity.setStatus("1");
                assComplaintsClient.save(entity);
            }
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveComplaints:", e);
            RenderUtil.renderFailure("error:" + e.getMessage(), response);
        }
    }

    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        AssComplaints entity = getEntity(id);
        model.addAttribute("entity", entity);
        return "ass/complaintsDetail";

    }

    @ModelAttribute("preload")
    public AssComplaints getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                AssComplaints entity = new AssComplaints();
                entity.setId(0L);
                entity.setComplaintsType("0");
                entity.setCreatedDate(new Date());
                entity.setFromUserId(ShiroUtil.getCurrentUserId());
                entity.setFromUserName(ShiroUtil.getCurrentUserName());
                return entity;
            }
        }
        return null;
    }
}
