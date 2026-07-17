package com.spt.bas.web.controller.apply;


import com.spt.bas.client.entity.ApplyCompanyOnline;
import com.spt.bas.client.entity.BsCompanyIndustry;
import com.spt.bas.client.remote.IApplyCompanyOnlineClient;
import com.spt.bas.client.remote.IBsCompanyIndustryClient;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 企业线上化
 */
@Controller
@RequestMapping(value = "/apply/Companyonline")
public class ApplyCompanyOnlineController {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsCompanyIndustryClient bsCompanyIndustryClient;
    @Autowired
    private IApplyCompanyOnlineClient applyCompanyOnlineClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyCompanyOnline entity = getEntity(id, processCode);
         String s = entity.getIndustry();
        String industryName="";
        String[] array = s.split(",");
        List<String> list = new ArrayList<>();
        for(String temp:array){
            list.add(temp);
        }
        for (String n : list) {
            BsCompanyIndustry bsCompanyIndustry = bsCompanyIndustryClient.getEntity(Long.parseLong(n));
            industryName=industryName+"/"+bsCompanyIndustry.getIndustryName();
        }
        entity.setIndustryName(industryName);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        String canEditFile = request.getParameter("hasEditFile");
        boolean canEdit = BooleanUtils.toBoolean(canEditFile);
        model.addAttribute("hasEditFile", canEdit);

        return "apply/applyCompanyOnline";
    }

    public static final Logger logger = LogManager.getLogger();


    @ModelAttribute("preload")
    public ApplyCompanyOnline getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyCompanyOnline enObject = null;
        if (id != null) {
            if (id > 0) {
                enObject = applyCompanyOnlineClient.getEntity(id);
                if (enObject != null) {
                    enObject.setId(id);
                }
                return enObject;
            } else {
                ApplyCompanyOnline applyCompanyOnline = new ApplyCompanyOnline();
                applyCompanyOnline.setId(0L);
                return applyCompanyOnline;
            }
        }
        return enObject;
    }
}
