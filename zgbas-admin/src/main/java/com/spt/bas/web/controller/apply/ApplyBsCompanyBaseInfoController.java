package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyCompanyInfo;
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

/**
 * <p>
 *  企业信息审批
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-10 13:48
 */
@Controller
@RequestMapping(value = "/apply/applyCompanyInfo")
public class ApplyBsCompanyBaseInfoController extends PageController<ApplyCompanyInfo, BaseVo> {

    @Override
    public BaseClient<ApplyCompanyInfo> getService() {
        return null;
    }
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IBsCompanyClient companyClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String companyIdStr = request.getParameter("companyId");
        if (companyIdStr != null && !companyIdStr.trim().equals("") && !companyIdStr.trim().equals("null")){
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
        }
        // 信用等级
        model.addAttribute("creditRatingJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));
        // 是否准入
        model.addAttribute("allowedJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_ALLOWED)));

        String processCode = request.getParameter("processCode");
        ApplyCompanyInfo entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());

        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/applyCompanyInfo";
    }

    @ModelAttribute("preload")
    public ApplyCompanyInfo getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyCompanyInfo enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyCompanyInfo) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyCompanyInfo bsCompanyAllowed = new ApplyCompanyInfo();
                bsCompanyAllowed.setId(0L);
                return bsCompanyAllowed;
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
