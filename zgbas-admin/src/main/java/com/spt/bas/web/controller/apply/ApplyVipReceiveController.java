package com.spt.bas.web.controller.apply;

import com.spt.bas.client.entity.ApplyVipReceive;
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
import java.math.BigDecimal;

/**
 * vip提额收款申请
 */
@Controller
@RequestMapping(value = "/apply/applyVipReceive")
public class ApplyVipReceiveController {
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model,
                          HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyVipReceive entity = getEntity(id, processCode);
        BigDecimal bigDecimalStr = new BigDecimal("0.00");
        entity.setUnpayedAmount(bigDecimalStr);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        String canEditFile = request.getParameter("hasEditFile");
        boolean canEdit = BooleanUtils.toBoolean(canEditFile);
        model.addAttribute("hasEditFile", canEdit);
        return "apply/vip-receive-content";
    }

    public static final Logger logger = LogManager.getLogger();


    @ModelAttribute("preload")
    public ApplyVipReceive getEntity(@RequestParam(value = "id", required = false) Long id, @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyVipReceive enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyVipReceive) ProcessControlUtil.getEntity(id, processCode);
                if (enObject != null) {
                    enObject.setId(id);
                }
                return enObject;
            } else {
                ApplyVipReceive applyVipReceive = new ApplyVipReceive();
                applyVipReceive.setId(0L);
                return applyVipReceive;
            }
        }
        return enObject;
    }

}
