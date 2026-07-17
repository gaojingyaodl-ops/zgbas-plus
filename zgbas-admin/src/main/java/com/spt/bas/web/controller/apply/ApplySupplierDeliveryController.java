package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierDelivery;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.*;
import com.spt.bas.web.shiro.ShiroUtil;
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
@RequestMapping(value = "/apply/supplierDelivery")
public class ApplySupplierDeliveryController extends PageController<ApplySupplierDelivery, BaseVo> {
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;

    @Override
    public BaseClient<ApplySupplierDelivery> getService() {
        return null;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplySupplierDelivery applySupplierDelivery = getEntity(id, processCode);
        model.addAttribute("entity", applySupplierDelivery);
        logger.info("content - applySupplierDelivery : " + JsonUtil.obj2Json(applySupplierDelivery));

        Long companyId = applySupplierDelivery.getCompanyId();
        if (companyId != null){
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
            if(null != company.getMatchUserId()){
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
                model.addAttribute("matchUser",SysUserSdk.getNickName());
            }
        }
        // 公司类型
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));
        // 客户分类
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_SUPPLIERCATEGORY)));
        return "apply/applySupplierDelivery";
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
    public ApplySupplierDelivery getEntity(@RequestParam(value = "id", required = false) Long id,
                                        @RequestParam(value = "processCode", required = false) String processCode) {
        ApplySupplierDelivery enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplySupplierDelivery) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplySupplierDelivery applySupplierDelivery = new ApplySupplierDelivery();
                applySupplierDelivery.setId(0L);
                return applySupplierDelivery;
            }
        }
        return enObject;
    }
}
