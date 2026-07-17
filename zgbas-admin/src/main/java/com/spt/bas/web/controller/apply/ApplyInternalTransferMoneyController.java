package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.ApplyInternalTransferMoney;
import com.spt.bas.client.remote.IApplyInternalTransferMoneyClient;
import com.spt.bas.web.util.ProcessControlUtil;
import com.spt.bas.web.util.WebParamUtils;
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
@RequestMapping(value = "/apply/internalTransferMoney")
public class ApplyInternalTransferMoneyController extends PageController<ApplyInternalTransferMoney, BaseVo> {
    @Autowired
    private IApplyInternalTransferMoneyClient applyInternalTransferMoneyClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {

        String processCode = request.getParameter("processCode");
        ApplyInternalTransferMoney internalBuy = getEntity(id, processCode);
        model.addAttribute("entity", internalBuy);
        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, internalBuy.getApproveId());
        model.addAttribute("borrower",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_OURCOMPANY)));
        model.addAttribute("lender",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_OURCOMPANY)));
        model.addAttribute("psv", permissionVo);
        if (id == 0) {
            return "apply/applyInternalTransferMoney";
        }
        return "apply/applyInternalTransferMoney-readonly";


    }

    @ModelAttribute("preload")
    public ApplyInternalTransferMoney getEntity(@RequestParam(value = "id", required = false) Long id,
                                                @RequestParam(value = "processCode", required = false) String processCode) {
        ApplyInternalTransferMoney enObject = null;
        if (id != null) {
            if (id > 0 && StringUtils.isNotBlank(processCode)) {
                enObject = (ApplyInternalTransferMoney) ProcessControlUtil.getEntity(id, processCode);
                enObject.setId(id);
                return enObject;
            } else {
                ApplyInternalTransferMoney entity = new ApplyInternalTransferMoney();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                return entity;
            }
        }
        return enObject;
    }

    @Override
    public BaseClient<ApplyInternalTransferMoney> getService() {
        return applyInternalTransferMoneyClient;
    }
}
