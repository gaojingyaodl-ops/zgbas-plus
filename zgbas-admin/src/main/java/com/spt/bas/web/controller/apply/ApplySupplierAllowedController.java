package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierAllowed;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.vo.SupplierAllowed;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/apply/supplierAllowed")
public class ApplySupplierAllowedController extends PageController<ApplySupplierAllowed, BaseVo> {
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private WebParamUtils webParamUtils;

    @Override
    public BaseClient<ApplySupplierAllowed> getService() {
        return null;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {

        model.addAttribute("supplierRatingJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));
        String processCode = request.getParameter("processCode");
        SupplierAllowed applySupplierAllowed = getEntity2(id, processCode);
        SupplierAllowed showSupplierAllowed = new SupplierAllowed();
        BeanUtils.copyProperties(applySupplierAllowed, showSupplierAllowed);
        showSupplierAllowed.setExistJudicialFreeze(showSupplierAllowed.getExistJudicialFreeze().equals("0") ? "否" : "是");
        showSupplierAllowed.setExistOverDueTax(showSupplierAllowed.getExistOverDueTax().equals("0") ? "否" : "是");
        model.addAttribute("entity", showSupplierAllowed);
        model.addAttribute("supplierLevelJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SUPPLIERLEVEL)));

        Long companyId = applySupplierAllowed.getCompanyId();
        if (companyId != null){
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
            if(null != company.getMatchUserId()){
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
                model.addAttribute("matchUser",SysUserSdk.getNickName());
            }
        }
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, applySupplierAllowed.getApproveId());
        // 公司类型
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));
        // 客户分类
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_SUPPLIERCATEGORY)));
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/applySupplierAllowed";
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
    public ApplySupplierAllowed getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        ApplySupplierAllowed enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplySupplierAllowed) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplySupplierAllowed applySupplierAllowed = new ApplySupplierAllowed();
                applySupplierAllowed.setId(0L);
                return applySupplierAllowed;
            }
        }
        return enObject;
    }

    @ModelAttribute("preload2")
    public SupplierAllowed getEntity2(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        SupplierAllowed enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (SupplierAllowed) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                SupplierAllowed applySupplierQuota = new SupplierAllowed();
                applySupplierQuota.setId(0L);
                return applySupplierQuota;
            }
        }
        return enObject;
    }
}