package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDictDataSdk;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmApplySet;
import com.spt.pm.entity.PmApprove;
import com.spt.pm.entity.PmApproveStep;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 申请-损耗申请
 */
@Controller
@RequestMapping(value = "/apply/logistics/adjust")
public class ApplyLogisticsAdjustController extends PageController<ApplyLogisticsAdjust, BaseVo> {
    
    @Autowired
    private IApplyLogisticsAdjustClient applyLogisticsAdjustClient;
    @Autowired
    private IPmApproveStepClient pmApproveStepClient;
    @Autowired
    private IPmApplySetClient pmApplySetClient;
    @Autowired
    private IPmApproveClient pmApproceClient;
    @Autowired
    private IBsCompanyAccountClient bsCompanyAccountClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IBsProductTypeClient productTypeClient;
    @Autowired
    private IBsFactoryClient factoryClient;
    
    @Override
    public BaseClient<ApplyLogisticsAdjust> getService() {
        return applyLogisticsAdjustClient;
    }
    
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyLogisticsAdjust entity = getEntity(id);
        if (entity.getId() == null) {
            entity.setId(0L);
        }
        model.addAttribute("entity", entity);
        //产品类型
        List<SysDictDataSdk> list = DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYPRODUCT);
        model.addAttribute("productType", JsonUtil.obj2Json(list));
        model.addAttribute("productJson",
                JsonUtil.obj2Json(productTypeClient.findAll()));
        List<BsFactory> lstFactory = factoryClient.findByEnterpriseId(ShiroUtil.getEnterpriseId());
        model.addAttribute("factoryJson", JsonUtil.obj2Json(lstFactory));

        //业务类型
        return "apply/applyLogisticsAdjust";
    }

    
    @ModelAttribute("preload")
    public ApplyLogisticsAdjust getEntity(@RequestParam(value = "id", required = false) Long id) {
        ApplyLogisticsAdjust entity = new ApplyLogisticsAdjust();
        entity.setStatus(BasConstants.APPROVE_STATUS_N);
        if (id != null && id != 0L) {
            entity = getService().getEntity(id);
        }
        return entity;
    }
}
