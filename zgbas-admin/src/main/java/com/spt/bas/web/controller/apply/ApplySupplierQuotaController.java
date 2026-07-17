package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplySupplierQuota;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/apply/supplierQuota")
public class ApplySupplierQuotaController extends PageController<ApplySupplierQuota, BaseVo> {
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private WebParamUtils webParamUtils;

    @Override
    public BaseClient<ApplySupplierQuota> getService() {
        return null;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        model.addAttribute("shareholderJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SHAREHOLDER)));
        model.addAttribute("lastYearTaxableSalesJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_LASTYEARTAXABLESALE)));
        model.addAttribute("unallocatedStockJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_UNALLOCATEDSTOCK)));
        model.addAttribute("supplierLevelJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SUPPLIERLEVEL)));
        // 公司类型
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));
        // 客户分类
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));
        String processCode = request.getParameter("processCode");
        ApplySupplierQuota applySupplierQuota = getEntity(id, processCode);
        model.addAttribute("entity", applySupplierQuota);

        Long companyId = applySupplierQuota.getCompanyId();
        if (companyId != null){
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
            if(null != company.getMatchUserId()){
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
                model.addAttribute("matchUser",SysUserSdk.getNickName());
            }
        }
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, applySupplierQuota.getApproveId());
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/applySupplierQuota";
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
    public ApplySupplierQuota getEntity(@RequestParam(value = "id", required = false) Long id,
                                          @RequestParam(value = "processCode", required = false) String processCode) {
        ApplySupplierQuota enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplySupplierQuota) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplySupplierQuota applySupplierQuota = new ApplySupplierQuota();
                applySupplierQuota.setId(0L);
                return applySupplierQuota;
            }
        }
        return enObject;
    }
}
