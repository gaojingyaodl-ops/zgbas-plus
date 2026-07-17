package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.ApplyPromoteVip;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * vip提额
 */
@Controller
@RequestMapping(value = "/apply/applyPromoteVIP")
public class ApplyPromoteVIPController  {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsCompanyClient bsCompanyClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyPromoteVip entity = getEntity(id, processCode);
        BsCompany company = bsCompanyClient.findCompany(entity.getCompanyId());
        model.addAttribute("daysRemaining", company.getDaysRemaining());
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        String canEditFile = request.getParameter("hasEditFile");
        boolean canEdit = BooleanUtils.toBoolean(canEditFile);
        model.addAttribute("hasEditFile", canEdit);

        return "apply/apply-vip-promote";
    }

    public static final Logger logger = LogManager.getLogger();


    @ModelAttribute("preload")
    public ApplyPromoteVip getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyPromoteVip enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyPromoteVip) ProcessControlUtil.getEntity(id, processCode);
                if (enObject != null) {
                    enObject.setId(id);
                }
                return enObject;
            } else {
                ApplyPromoteVip applyPromoteVip = new ApplyPromoteVip();
                applyPromoteVip.setId(0L);
                return applyPromoteVip;
            }
        }
        return enObject;
    }



}
