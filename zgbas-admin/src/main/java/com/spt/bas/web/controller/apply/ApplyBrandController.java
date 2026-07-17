package com.spt.bas.web.controller.apply;

import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasBrand;
import com.spt.bas.client.entity.BsCompany;
import com.spt.bas.client.remote.IApplyBrandClient;
import com.spt.bas.client.remote.IBsCompanyClient;
import com.spt.bas.client.remote.IBsProductTypeClient;
import com.spt.bas.client.remote.IPmApproveContentsClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.pm.entity.PmApproveContents;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/apply/brand")
public class ApplyBrandController extends PageController<BasBrand, BaseVo> {

    @Autowired
    private IApplyBrandClient applyBrandClient;
    @Autowired
    private IPmApproveContentsClient pmApproveContentsClient;
    @Autowired
    private IBsProductTypeClient bsProductTypeClient;
    @Autowired
    private IBsCompanyClient companyClient;

    @Override
    public BaseClient<BasBrand> getService() {
        return applyBrandClient;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request) {
        //货品树
        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productJson",
                JsonUtil.obj2Json(bsProductTypeClient.findAll()));
        return "apply/applyBrand";
    }

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        String companyIdStr = request.getParameter("companyId");
        if (companyIdStr != null && !companyIdStr.trim().equals("") && !companyIdStr.trim().equals("null")){
            Long companyId = Long.parseLong(request.getParameter("companyId"));
            BsCompany company = companyClient.getEntity(companyId);
            model.addAttribute("company", company);
        }
        String processCode = request.getParameter("processCode");
        BasBrand entity = getEntity(id, processCode);
        model.addAttribute("entity", entity);
        // 是否可在审批中编辑
        if(Boolean.TRUE.equals(permissionVo.getHasApprove()) && !permissionVo.getMapEdit().isEmpty()){
            permissionVo.setCanApproveEdit(true);
        }else{
            permissionVo.setCanApproveEdit(false);
        }
        //货品树
        List<EasyTreeNode> productTree = bsProductTypeClient.findAllProductTree(ShiroUtil.getEnterpriseId());
        model.addAttribute("productTypeJson", JsonUtil.obj2Json(productTree));
        model.addAttribute("productJson",
                JsonUtil.obj2Json(bsProductTypeClient.findAll()));
        model.addAttribute("psv", permissionVo);
        model.addAttribute("status",getStatus(id));
        return "apply/applyBrand";
    }



    @ModelAttribute("preload")
    public BasBrand getEntity(@RequestParam(value = "id", required = false) Long id,
                                      @RequestParam(value = "processCode", required = false) String processCode) {
        BasBrand enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (BasBrand) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                BasBrand basBrand = new BasBrand();
                basBrand.setId(0L);
                return basBrand;
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
