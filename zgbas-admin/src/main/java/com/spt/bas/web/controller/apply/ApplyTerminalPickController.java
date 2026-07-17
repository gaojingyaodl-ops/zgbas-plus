package com.spt.bas.web.controller.apply;


import com.spt.bas.client.entity.ApplyTerminalPick;
import com.spt.bas.client.remote.IApplyTerminalPickClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 终端工厂自提审批
 */
@Controller
@RequestMapping(value = "/apply/applyTerminalPick")
public class ApplyTerminalPickController extends PageController<ApplyTerminalPick, BaseVo>{
    @Autowired
    private IApplyTerminalPickClient applyTerminalPickClient;


    @Override
    public BaseClient<ApplyTerminalPick> getService() {
        return applyTerminalPickClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyTerminalPick entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        return "apply/applyTerminalPick";
    }
    @ModelAttribute("preload")
    public ApplyTerminalPick getEntity(@RequestParam(value = "id", required = false) Long id,@RequestParam(value = "processCode", required = false) String processCode) {
        ApplyTerminalPick enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyTerminalPick) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyTerminalPick entity = new ApplyTerminalPick();
                entity.setId(0L);
                return entity;
            }
        }
        return enObject;
    }

}
