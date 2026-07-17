package com.spt.bas.web.controller.apply;


import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyWxCfca;
import com.spt.bas.client.remote.IApplyWxCfcaClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/apply/ApplyWxCfcaController")
public class ApplyWxCfcaController extends PageController<ApplyWxCfca, BaseVo> {
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IApplyWxCfcaClient applyWxCfcaClient;
    @Override
    public BaseClient<ApplyWxCfca> getService() {
        return applyWxCfcaClient;
    }
    @Value("${file.show.url}")
    private String fileShowUrl;


    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyWxCfca entity = getEntity(id, processCode);
        entity.setElectronicSignFileId(fileShowUrl + "/view/show/" + entity.getElectronicSignFileId());
        entity.setBusinessLicenseWithSealUrl(fileShowUrl + "/view/show/" + entity.getBusinessLicenseWithSealUrl());
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));

        return "apply/applyWxCfca";
    }


    private String getStatus(Long id) {
        if (id != null && id > 0L) {
            PmApproveContents entity = pmApproveContentsClient.getEntity(id);
            if (entity != null) {
                return entity.getStatus();
            }
        }
        return BasConstants.APPROVE_STATUS_N;
    }


    @ModelAttribute("preload")
    public ApplyWxCfca getEntity(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyWxCfca enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyWxCfca) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyWxCfca entity = new ApplyWxCfca();
                entity.setId(0L);
                return entity;
            }
        }
        return enObject;
    }
}
