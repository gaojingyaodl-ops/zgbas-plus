package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.auth.sdk.open.IAuthOpenFacade;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.entity.BsCompanyQuota;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsCompanyQuotaClient;
import com.spt.bas.client.remote.IPmApproveClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.date.DateOperator;
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
import java.util.Date;

/**
 * 额度浮动审批
 */
@Controller
@RequestMapping(value = "/apply/bsCompanyQuota")
public class ApplyBsCompanyQuotaController extends PageController<BsCompanyQuota, BaseVo> {
    @Autowired
    private IBsCompanyQuotaClient bsCompanyQuotaClient;
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Autowired
    private IAuthOpenFacade authOpenFacade;
    @Resource
    private WebParamUtils webParamUtils;

    @Override
    public BaseClient<BsCompanyQuota> getService() {
        return bsCompanyQuotaClient;
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String companyIdStr = request.getParameter("companyId");
        if (companyIdStr != null && !companyIdStr.trim().equals("") && !companyIdStr.trim().equals("null")){
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
            if(null != company.getMatchUserId()){
                SysUserSdk SysUserSdk = authOpenFacade.findUserById(company.getMatchUserId());
                model.addAttribute("matchUser",SysUserSdk.getNickName());
            }
        }

        String processCode = request.getParameter("processCode");
        BsCompanyQuota entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        Long approveId=entity.getApproveId();
        if (approveId != null && approveId != 0L) {
            PmApprove approve = pmApproveClient.getEntity(approveId);
            Date lastApproveDate = approve.getLastApproveDate();
            Date createdDate = approve.getCreatedDate();
            if(lastApproveDate!= null){
                Date effectiveDate = DateOperator.addDays(lastApproveDate, +3);
                model.addAttribute("effectiveDate",effectiveDate);
            } else {
                Date effectiveDate = DateOperator.addDays(createdDate, +3);
                model.addAttribute("effectiveDate",effectiveDate);
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
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "bs/companyQuotaOperateV2";
    }

    @ModelAttribute("preload")
    public BsCompanyQuota getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        BsCompanyQuota enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (BsCompanyQuota) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                BsCompanyQuota bsCompanyQuota = new BsCompanyQuota();
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
