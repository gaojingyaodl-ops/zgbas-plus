package com.spt.bas.web.controller.approve;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.auth.sdk.vo.UserSearchVo;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApproveWaitDeal;
import com.spt.bas.client.remote.IApproveWaitDealClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.vo.ApproveWaitSearchVo;
import com.spt.bas.client.vo.WsMessage;
import com.spt.bas.report.client.remote.IRptCtrContractReportClient;
import com.spt.bas.report.client.vo.RptCtrProfitSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.CopyUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.LogUtil;
import com.spt.bas.web.ws.IndexWebSocketServer;
import com.spt.bas.web.ws.po.Message;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/approve/waitDeal")
public class ApproveWaitDealController extends PageController<ApproveWaitDeal, BaseVo>{

    @Autowired
    private IApproveWaitDealClient iApproveWaitDealClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IRptCtrContractReportClient ctrContractReportClient;
    @Autowired
    private IndexWebSocketServer indexWebSocketServer;
    @Override
    public BaseClient<ApproveWaitDeal> getService() {
        return iApproveWaitDealClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("readFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.READ_FLG)));
        model.addAttribute("dealType",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BasConstants.WAIT_DEAL_TYPE)));

        UserSearchVo userSearchVo = new UserSearchVo(ShiroUtil.getEnterpriseId(), true);
        List<SysUserSdk> userAll = authOpenFacade.findUserAll(userSearchVo);
        model.addAttribute("userJson", JsonUtil.obj2Json(userAll));
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true);
        // 申请人
        model.addAttribute("matchUserNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        //货品树
        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productJson",
                JsonUtil.obj2Json(bsProductTypeClient.findAll()));
        return "approve/approveWaitDeal";
    }

    /**
     * 分页查询
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "findPageWaitDeal")
    public void findPageWaitDeal(ApproveWaitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Page<ApproveWaitDeal> pageWaitDeal = iApproveWaitDealClient.findPageWaitDeal(searchVo);
        JsonEasyUI.renderJson(response, pageWaitDeal);
    }
    /**
     *
     *
     *
     */
    @RequestMapping(value = "findSubject")
    public void findSubject(ApproveWaitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        String subject = iApproveWaitDealClient.findSubject(searchVo);
        List<String> list =new ArrayList<>();
        list.add(subject);
        JsonEasyUI.renderListJson(response,list);
    }
    /**
     * 根据Id查询所属的待办事项
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "findPageWaitDealById")
    public void findPageWaitDealById(ApproveWaitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        if (StringUtils.isNotBlank(String.valueOf(searchVo.getRelaUserId()))){
            searchVo.setRelaUserId(ShiroUtil.getCurrentUserId().toString());
            searchVo.setRelaDeptId(ShiroUtil.getDeptId());
        }
        Page<ApproveWaitDeal> page = iApproveWaitDealClient.findPageWaitDealById(searchVo);
        JsonEasyUI.renderJson(response, page);

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
        Long shiroId = ShiroUtil.getEnterpriseId();
        model.addAttribute("readFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.READ_FLG)));
        model.addAttribute("dealType",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.WAIT_DEAL_TYPE)));
        model.addAttribute("completeFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.COMPLETE_FLG)));
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode userNodes = EasyTreeUtil2.getDeptTree(deptList, true);
        EasyTreeNode deptNodes = EasyTreeUtil2.getDeptTree(deptList,false);
        model.addAttribute("matchDeptNameTree",JsonUtil.obj2Json(deptNodes.getChildren()));
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(userNodes.getChildren()));
        ApproveWaitDeal entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new ApproveWaitDeal();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "approve/approveWaitDeal-detail";
    }

    @ModelAttribute("preload")
    public ApproveWaitDeal getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApproveWaitDeal entity = null;
        if (id != null) {
            if (id > 0L)
                entity = getService().getEntity(id);
            else {
                entity = new ApproveWaitDeal();
                entity.setId(0L);
            }
        }
        return entity;
    }

    @RequestMapping(value = "findProfitByDeptId")
    public void findProfitByDeptId(RptCtrProfitSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        searchVo.setSearchType(null);
        List<Long> profitByDeptId = ctrContractReportClient.findProfitByDeptId(searchVo);
        List<SysUserSdk> deptRows = authOpenFacade.findByDeptIds(profitByDeptId);
        JsonEasyUI.renderListJson(response, deptRows,null,null);
    }
    // 保存
    @RequestMapping(value = "saveWaitDeal")
    public void saveWaitDeal(@Valid @ModelAttribute("preload") ApproveWaitDeal entity, HttpServletRequest request, HttpServletResponse response) {
        try {
            String[] relaUserIds = request.getParameterValues("relaUserId");
            String[] relaDeptIds = request.getParameterValues("relaDeptId");
            List<ApproveWaitDeal> dealList = new ArrayList<>();
            List<String> userIdList =new ArrayList<>();
            List<Long> deptIdList =new ArrayList<>();
            entity.setEnterpriseId(ShiroUtil.getEnterpriseId());
            entity.setCreatedDate(new Date());
            entity.setCreatedUserId(ShiroUtil.getCurrAppId());
            //默认未读
            entity.setReadFlg(BasConstants.READ_FLG_NOT);
            if (!ObjectUtils.isEmpty(relaUserIds) && relaUserIds.length!=0){
                dealList = Arrays.stream(relaUserIds).filter(a -> a.contains("user"))
                        .map(a -> a.replace("user", ""))
                        .map(a -> {
                            ApproveWaitDeal approveWaitDeal = CopyUtil.copy(entity, ApproveWaitDeal.class);
                            approveWaitDeal.setRelaUserId(a);
                            return approveWaitDeal;
                        }).collect(Collectors.toList());
                userIdList=dealList.stream().map(ApproveWaitDeal::getRelaUserId).collect(Collectors.toList());
            }else {
                dealList = Arrays.stream(relaDeptIds).map(a -> {
                            ApproveWaitDeal approveWaitDeal = CopyUtil.copy(entity, ApproveWaitDeal.class);
                            approveWaitDeal.setRelaDeptId(Long.valueOf(a));
                            return approveWaitDeal;
                        }).collect(Collectors.toList());
                deptIdList = dealList.stream().map(ApproveWaitDeal::getRelaDeptId).collect(Collectors.toList());
            }
            dealList.forEach(a -> iApproveWaitDealClient.save(a));

            long countRedFlg=0;
            long countCompleteFlg=0;
            if (!ObjectUtils.isEmpty(userIdList)){
                for (String s : userIdList) {
                    //查询未读个数集合
                    ApproveWaitSearchVo searchVo =new ApproveWaitSearchVo();
                    searchVo.setRelaUserId(s);
                    List<ApproveWaitDeal> list = iApproveWaitDealClient.findPageWaitDealCount(searchVo);
                    countRedFlg = list.stream().filter(string -> "0".equals(string.getReadFlg()) && Long.valueOf(s).equals(Long.valueOf(string.getRelaUserId()))).count();
                    countCompleteFlg =list.stream().filter(string->"0".equals(string.getCompleteFlg()) && s.equals(string.getRelaUserId())).count();
                    Message msg = new Message();
                    msg.setDate(new Date());
                    msg.setTo(s);
                    msg.setText("1");
                    msg.setCountReadFlg(countRedFlg);
                    msg.setCountCompleteFlg(countCompleteFlg);
                    indexWebSocketServer.sendInfo(s, JsonUtil.obj2Json(msg));
                }

            }else {
                for (Long aLong : deptIdList) {
                    //查询未读个数集合
                    ApproveWaitSearchVo searchVo =new ApproveWaitSearchVo();
                    searchVo.setRelaDeptId(aLong);
                    //当前用户ID
                    List<ApproveWaitDeal> list = iApproveWaitDealClient.findPageWaitDealCount(searchVo);

                countRedFlg = list.stream().filter(string -> "0".equals(string.getReadFlg()) && aLong.equals(string.getRelaDeptId()) ).count();
                countCompleteFlg =list.stream().filter(string->"0".equals(string.getCompleteFlg()) && aLong.equals(string.getRelaDeptId())).count();
                Message msg = new Message();
                msg.setDate(new Date());
                msg.setTo(String.valueOf(aLong));
                msg.setText("1");
                msg.setCountReadFlg(countRedFlg);
                msg.setCountCompleteFlg(countCompleteFlg);
                SysDeptSdk sydDept = authOpenFacade.findDeptById(aLong);
                sydDept.getUsers().forEach(users -> indexWebSocketServer.sendInfo(users.getUserId().toString(), JsonUtil.obj2Json(msg)));
                }
            }
            LogUtil.saveOrUpdate(request, entity, entity, entity.getId());// 记录日志

            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("saveWaitDeal:", e);
            RenderUtil.renderFailure("saveWaitDeal:" + e.getMessage(), response);
        }
    }
    //改变读取状态
    @RequestMapping(value = "updateStatus")
    public void updateStatus(ApproveWaitSearchVo searchVo,HttpServletRequest request, HttpServletResponse response){
        try {
            iApproveWaitDealClient.updateStatus(searchVo);
            notifyWaitDealNum();
            LogUtil.saveOrUpdate(request,searchVo,searchVo,searchVo.getId());// 记录日志
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("searchVo:", e);
            RenderUtil.renderFailure("searchVo:" + e.getMessage(), response);
        }

    }
    //改变完成状态
    @RequestMapping(value = "updateFlg")
    public void updateFlg(ApproveWaitSearchVo searchVo,HttpServletRequest request, HttpServletResponse response){
        try {
            iApproveWaitDealClient.updateFlg(searchVo);
            LogUtil.saveOrUpdate(request,searchVo,searchVo,searchVo.getId());// 记录日志
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("searchVo:", e);
            RenderUtil.renderFailure("searchVo:" + e.getMessage(), response);
        }

    }

    /**
     * 跳转到主页面详情页
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "index/{id}", method = RequestMethod.GET)
    public String index(@PathVariable("id") Long id, Model model) {
        Long shiroId = ShiroUtil.getEnterpriseId();
        Long deptId = ShiroUtil.getDeptId();
        model.addAttribute("readFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.READ_FLG)));
        model.addAttribute("dealType",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.WAIT_DEAL_TYPE)));
        model.addAttribute("completeFlg",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(shiroId,BasConstants.COMPLETE_FLG)));
        DeptSearchVo deptSearchVo = new DeptSearchVo(ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));
        model.addAttribute("shiroId",shiroId);
        model.addAttribute("deptId",deptId);
        ApproveWaitDeal entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new ApproveWaitDeal();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "admin/index_detail";

    }

    private void notifyWaitDealNum(){
        String currUserId = ShiroUtil.getCurrentUserId().toString();
        Long userWaitDealNum = iApproveWaitDealClient.getUserWaitDealNum(new ApproveWaitSearchVo(currUserId));
        WsMessage wsMessage = new WsMessage(ShiroUtil.getCurrentUserId(), WsMessage.MESSAGE_TYPE_W, userWaitDealNum);
        indexWebSocketServer.sendInfo(currUserId, JsonUtil.obj2Json(wsMessage));
    }
}
