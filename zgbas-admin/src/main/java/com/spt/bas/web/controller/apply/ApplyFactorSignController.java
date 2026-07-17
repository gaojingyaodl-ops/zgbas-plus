package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyFactorSign;
import com.spt.bas.client.remote.IApplyFactorSignClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 签署保理申请
 */
@Controller
@RequestMapping(value = "/apply/factor/sign")
public class ApplyFactorSignController extends PageController<ApplyFactorSign, BaseVo> {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IApplyFactorSignClient applyFactorSignClient;

    @Override
    public BaseClient<ApplyFactorSign> getService() {
        return applyFactorSignClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request){
        ApplyFactorSign entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("status",entity.getStatus());
        model.addAttribute("psv", permissionVo);
        model.addAttribute("entity", entity);
        return "apply/factorSign-content";
    }

    @ModelAttribute("preload")
    public ApplyFactorSign getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyFactorSign entity = new ApplyFactorSign();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        if (id != null && id != 0L) {
            entity = getService().getEntity(id);
        }
        return entity;
    }
}
