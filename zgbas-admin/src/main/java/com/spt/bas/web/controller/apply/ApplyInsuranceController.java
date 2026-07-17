package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInsurance;
import com.spt.bas.client.remote.IApplyInsuranceClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
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

@Controller
@RequestMapping(value = "/apply/insurance")
public class ApplyInsuranceController extends PageController<ApplyInsurance, BaseVo> {

    @Autowired
    IApplyInsuranceClient iApplyInsuranceClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyInsurance entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status", getStatus(id));
        return "apply/applyInsurance";
    }

    //通过Id和流程code获取实体属性
    @ModelAttribute("preload")
    public ApplyInsurance getEntity(@RequestParam(value = "id", required = false) Long id,
                                    @RequestParam(value = "processCode", required = false) String processCode) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyInsurance entity = new ApplyInsurance();
                entity.setId(0L);
                entity.setApproveId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return null;
    }


    //获取状态
    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }

    @Override
    public BaseClient<ApplyInsurance> getService() {
        return iApplyInsuranceClient;
    }
}
