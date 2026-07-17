package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierFuture;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.*;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/apply/supplierFuture")
public class ApplySupplierFutureController extends PageController<ApplySupplierFuture, BaseVo> {
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;

    @Override
    public BaseClient<ApplySupplierFuture> getService() {
        return null;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplySupplierFuture applySupplierFuture = getEntity(id, processCode);
        model.addAttribute("entity", applySupplierFuture);
        logger.info("content - applySupplierFuture : " + JsonUtil.obj2Json(applySupplierFuture));

        Long companyId = applySupplierFuture.getCompanyId();
        if (companyId != null){
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
        }
        return "apply/applySupplierFuture";
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
    public ApplySupplierFuture getEntity(@RequestParam(value = "id", required = false) Long id,
                                           @RequestParam(value = "processCode", required = false) String processCode) {
        ApplySupplierFuture enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplySupplierFuture) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplySupplierFuture applySupplierFuture = new ApplySupplierFuture();
                applySupplierFuture.setId(0L);
                return applySupplierFuture;
            }
        }
        return enObject;
    }
}
