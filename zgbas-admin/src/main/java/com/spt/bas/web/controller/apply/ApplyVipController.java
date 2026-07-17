package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.ApplyVip;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 审批vip
 */
@Controller
@RequestMapping(value = "/apply/vip")
public class ApplyVipController {
    @Resource
    private WebParamUtils webParamUtils;


    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyVip entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        String canEditFile = request.getParameter("hasEditFile");
        boolean canEdit = BooleanUtils.toBoolean(canEditFile);
        model.addAttribute("hasEditFile", canEdit);

        return "apply/apply-vip";
    }

    public static final Logger logger = LogManager.getLogger();


    @ModelAttribute("preload")
    public ApplyVip getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyVip enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyVip) ProcessControlUtil.getEntity(id, processCode);
                if (enObject != null) {
                    enObject.setId(id);
                }
                return enObject;
            } else {
                ApplyVip applyVip = new ApplyVip();
                applyVip.setId(0L);
                return applyVip;
            }
        }
        return enObject;
    }
}
