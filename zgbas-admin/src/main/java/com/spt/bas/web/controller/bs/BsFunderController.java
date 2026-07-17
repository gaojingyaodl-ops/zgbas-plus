package com.spt.bas.web.controller.bs;

import com.google.common.collect.Maps;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.entity.BsCompanyDcsx;
import com.spt.bas.client.entity.BsFunder;
import com.spt.bas.client.remote.IBsCompanyDcsxClient;
import com.spt.bas.client.remote.IBsFunderClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.LogUtil;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资金方配置
 *
 */
@Controller
@RequestMapping("/bs/funder")
public class BsFunderController extends SingleCrudControll<BsFunder, BaseVo> {

    @Autowired
    private IBsFunderClient bsFunderClient;

    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Autowired
    private IBsCompanyDcsxClient bsCompanyDcsxClient;

    @Override
    public BaseClient<BsFunder> getService() {
        return bsFunderClient;
    }


    /**
     * 查询所有信息
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findFunder")
    public void findAllBsFunder(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        PageDown<BsFunder> page = bsFunderClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }


    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "")
    public String findBsFunder(Model model) {
        //代采方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("companyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("userNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        return "bs/funder";
    }

    /**
     * 修改方法
     * @param request
     * @param response
     */
    @RequestMapping("updateFunder")
    public void updateFunder(HttpServletRequest request, HttpServletResponse response){
        BsFunder bsFunder = new BsFunder();
        bsFunder.setId(Long.valueOf(request.getParameter("id")));
        bsFunder.setUserId(Long.valueOf(request.getParameter("userId")));
        bsFunder.setUserName(request.getParameter("userName"));
        String[] companyNames = request.getParameterValues("companyNames");
        bsFunder.setCompanyNames(Arrays.stream(companyNames).map(c ->c.toString()).collect(Collectors.joining(",")));
        bsFunder.setRemark(request.getParameter("remark"));//备注
        bsFunder.setEnterpriseId(ShiroUtil.getEnterpriseId());
        try{
            BsFunder old = bsFunderClient.getEntity(bsFunder.getId());
            if(!old.getUserId().equals(bsFunder.getUserId())) {
                List<BsFunder> bsFunderList = bsFunderClient.findAllByUserId(bsFunder.getUserId());
                if(!CollectionUtils.isEmpty(bsFunderList)){
                    RenderUtil.renderFailure("【"+bsFunder.getUserName()+"】用户配置已存在", response);
                    return;
                }
            }
            bsFunderClient.save(bsFunder);
            LogUtil.saveOrUpdate(request, old, bsFunder, bsFunder.getId());
            RenderUtil.renderSuccess("修改成功", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderFailure("修改失败", response);
        }
    }

    /**
     * 跳转到详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        //代采方
        List<BsCompanyDcsx> dcsxCompanyList = bsCompanyDcsxClient.findDcsxCompanyList();
        model.addAttribute("companyNameJson", JsonUtil.obj2Json(dcsxCompanyList));
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("userNameTree", JsonUtil.obj2Json(nodes.getChildren()));

        BsFunder entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new BsFunder();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "bs/funder-detail";

    }

    @ModelAttribute("preload")
    public BsFunder getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                BsFunder entity = new BsFunder();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }
    @Override
    public Map<String, Object> getDefaultFilter() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        return map;
    }

    /**
     * 删除方法
     * @param id
     * @param request
     * @param response
     * @return
     */
	@Override
	@RequestMapping(value = "delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            LogUtil.del(request, bsFunderClient.getEntity(id));
            bsFunderClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }

    /**
     * 添加方法
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void saveBsFunder(HttpServletRequest request, HttpServletResponse response) {
        BsFunder bsFunder = new BsFunder();
        bsFunder.setUserId(Long.valueOf(request.getParameter("userId")));
        bsFunder.setUserName(request.getParameter("userName"));
        String[] companyNames = request.getParameterValues("companyNames");
        bsFunder.setCompanyNames(Arrays.stream(companyNames).map(c ->c.toString()).collect(Collectors.joining(",")));
        bsFunder.setRemark(request.getParameter("remark"));//备注
        bsFunder.setEnterpriseId(ShiroUtil.getEnterpriseId());
        try{
            List<BsFunder> bsFunderList = bsFunderClient.findAllByUserId(bsFunder.getUserId());
            if(!CollectionUtils.isEmpty(bsFunderList)){
                RenderUtil.renderFailure("【"+bsFunder.getUserName()+"】用户配置已存在", response);
                return;
            }
            bsFunderClient.save(bsFunder);
            LogUtil.saveOrUpdate(request,new BsFunder() , bsFunder, bsFunder.getId());
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
    }
}
