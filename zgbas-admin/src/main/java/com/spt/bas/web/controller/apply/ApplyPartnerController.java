package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyPartner;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *      意见反馈审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 13:42
 */
@Controller
@RequestMapping(value = "/apply/partner")
public class ApplyPartnerController {
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyPartner entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/applyPartner";
    }

    @ModelAttribute("preload")
    public ApplyPartner getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyPartner enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyPartner) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyPartner entity = new ApplyPartner();
                entity.setId(0L);
                return entity;
            }
        }
        return enObject;
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
}
