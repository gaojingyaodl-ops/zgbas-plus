package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyCredit;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.client.entity.BsCompanyQuotaV1;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyCreditClient;
import com.spt.bas.client.remote.IBsCompanyQuotaClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.client.vo.ApplyQuotaVo;
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

/**
 * 额度审批
 */
@Controller
@RequestMapping(value = "/apply/bsCompanyQuotaV1")
public class ApplyBsCompanyQuotaV1Controller extends PageController<BsCompanyQuota, BaseVo> {
    @Autowired
    private IBsCompanyQuotaClient bsCompanyQuotaClient;
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsCompanyCreditClient bsCompanyCreditClient;

    @Override
    public BaseClient<BsCompanyQuota> getService() {
        return bsCompanyQuotaClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyQuotaVo entity = getEntity(id, processCode);
        Long companyId = entity.getCompanyId();
        BsCompany company = companyClient.getEntity(companyId);
        model.addAttribute("company", company);
        if(null != company.getMatchUserId()){
            SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
            model.addAttribute("matchUser",SysUserSdk.getNickName());
        }
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        // 公司类型
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));
        // 客户分类
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));
        // 企业性质
        model.addAttribute("credictTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "bs/companyQuotaOperationV1";
    }
    @RequestMapping(value = "content1/{id}", method = RequestMethod.GET)
    public String content1(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String processCode = request.getParameter("processCode");
        ApplyQuotaVo entity = getEntity(id, processCode);
        BsCompanyCredit byCompanyIdAndType = bsCompanyCreditClient.findByCompanyIdAndType(entity.getCompanyId(), entity.getCreditType());
        // 用于前端展示授信额度
        entity.setCreditAmount(byCompanyIdAndType.getCreditAmount());
        entity.setRiskAmount(byCompanyIdAndType.getRiskAmount());
        Long companyId = entity.getCompanyId();
        BsCompany company = companyClient.getEntity(companyId);
        model.addAttribute("company", company);
        if(null != company.getMatchUserId()){
            SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
            model.addAttribute("matchUser",SysUserSdk.getNickName());
        }
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        // 公司类型
        model.addAttribute("companyGradeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));
        // 客户分类
        model.addAttribute("companyTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_CPYTYPE)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));
        // 企业性质
        model.addAttribute("credictTypeJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_CREDIT_TYPE)));
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "bs/companyQuotaTemporary";
    }
    @ModelAttribute("preload")
    public ApplyQuotaVo getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyQuotaVo enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyQuotaVo) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyQuotaVo bsCompanyQuota = new ApplyQuotaVo();
                bsCompanyQuota.setId(0L);
                return bsCompanyQuota;
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
