package com.spt.bas.web.controller.evaluate;


import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.auth.sdk.vo.DeptSearchVo;
import com.spt.bas.client.entity.EvaluateUserManage;
import com.spt.bas.client.remote.IEvaluateUserManageClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/evaluate/user/manage")
public class EvaluateUserManageController extends PageController<EvaluateUserManage, BaseVo> {
    @Autowired
    private IEvaluateUserManageClient evaluateUserManageClient;
    
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    
    
    @Override
    public BaseClient<EvaluateUserManage> getService() {
        return evaluateUserManageClient;
    }
    @RequestMapping(value = "content")
    public String content(Model model) {
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("userNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        return "evaluate/evaluate-user-manage";
    }

    /**
     * 查询所有信息
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findAllEvaluateUserManageLoading")
    public void findAllEvaluateUserManageLoading(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
//        searchVo.setSort("dispOrderNo");
//        searchVo.setOrder("ASC");
        PageDown<EvaluateUserManage> page = evaluateUserManageClient.findPage(searchVo);
        JsonEasyUI.renderJson(response, page);
    }
    /**
     * 跳转到详情页
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "detail/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model model) {
        Long shiroid = ShiroUtil.getEnterpriseId();
        //获取业务员树
        DeptSearchVo deptSearchVo = new DeptSearchVo( ShiroUtil.getEnterpriseId());
        List<SysDeptSdk> deptList = authOpenFacade.findDeptAll(deptSearchVo);
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("userNameTree", JsonUtil.obj2Json(nodes.getChildren()));
        
        EvaluateUserManage entity;
        model.addAttribute("id", id);
        if (id != null && id != 0) {
            entity = getEntity(id);
            model.addAttribute("entity", entity);
        } else {
            entity = new EvaluateUserManage();
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        return "evaluate/evaluate-user-manage-detail";

    }
    @ModelAttribute("preload")
    public EvaluateUserManage getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0)
                return getService().getEntity(id);
            else {
                EvaluateUserManage entity = new EvaluateUserManage();
                entity.setId(0l);
                return entity;
            }
        }
        return null;
    }


    /**
     * 添加方法
     * @param request
     * @param response
     */
    @RequestMapping("/save")
    public void save(EvaluateUserManage entity,HttpServletRequest request, HttpServletResponse response) {
        try{
            evaluateUserManageClient.save(entity);
            RenderUtil.renderSuccess("success", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderFailure("fail", response);
        }
    }
    /**
     * 修改方法
     * @param request
     * @param response
     */
    @RequestMapping("update")
    public void update(EvaluateUserManage entity,HttpServletRequest request, HttpServletResponse response){
        try{
            evaluateUserManageClient.save(entity);
            RenderUtil.renderSuccess("修改成功", response);
        }catch (Exception e){
            e.printStackTrace();
            RenderUtil.renderSuccess("修改失败", response);
        }
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
            evaluateUserManageClient.delete(id);
            RenderUtil.renderSuccess("删除成功", response);
        } catch (Exception e) {
            RenderUtil.renderFailure("操作错误，请联系管理员", response);
        }
        return null;
    }


}
