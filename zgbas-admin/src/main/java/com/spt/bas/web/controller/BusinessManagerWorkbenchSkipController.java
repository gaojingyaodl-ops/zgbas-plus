package com.spt.bas.web.controller;

import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.remote.IPmProcessClient;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 业务经理工作台，跳转打开界面专用
 */
@Controller
@RequestMapping(value = "/business/manager/workbenchSkip")
public class BusinessManagerWorkbenchSkipController {
    @Autowired
    private IPmProcessClient processClient;

    @RequestMapping(value = "orderExecute")
    public String orderExecute(@RequestParam("labelCode") String labelCode, HttpServletResponse response, Model model) {
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        model.addAttribute("labelCode", labelCode);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        return "index/businessManagerSkip/orderExecute";
    }

    @RequestMapping(value = "orderExecuteNSP")
    public String orderExecuteNSP(@RequestParam("labelCode") String labelCode, HttpServletResponse response, Model model) {
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        model.addAttribute("labelCode", labelCode);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        return "index/businessManagerSkip/orderExecuteNSP";
    }

    @RequestMapping(value = "orderReceivable")
    public String orderReceivable(@RequestParam("labelCode") String labelCode, HttpServletResponse response, Model model) {
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        model.addAttribute("labelCode", labelCode);
        PmProcessSearchVo searchVo = new PmProcessSearchVo();
        searchVo.setEnterpriseId(ShiroUtil.getEnterpriseId());
        List<PmProcess> processList = processClient.findByEnterpriseId(searchVo);
        model.addAttribute("processListJson", JsonUtil.obj2Json(processList));
        return "index/businessManagerSkip/orderReceivable";
    }

    @RequestMapping(value = "orderApprove")
    public String orderApprove(HttpServletResponse response, Model model) {
        return "index/businessManagerSkip/orderApprove";
    }

    @RequestMapping(value = "inventory")
    public String inventory(HttpServletResponse response, Model model) {
        return "index/businessManagerSkip/inventory";
    }

    /**
     * companyType I 工业客户，T 供应商
     *
     * @param companyType
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "companyXZ")
    public String companyXZ(@RequestParam("companyType") String companyType, HttpServletResponse response, Model model) {
        model.addAttribute("companyType", companyType);
        model.addAttribute("companySourceJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANY_SOURCE)));
        // 行业分类
        model.addAttribute("companyIndustryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANY_INDUSTRY)));
        // 企业性质
        model.addAttribute("companyCategoryJson",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYCATEGORY)));
        return "index/businessManagerSkip/companyXZ";
    }

    @RequestMapping(value = "companyBX")
    public String companyBX(HttpServletResponse response, Model model) {
        model.addAttribute("companySourceJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANY_SOURCE)));
        return "index/businessManagerSkip/companyBX";
    }

    /**
     * companyType I 工业客户，T 供应商
     *
     * @param companyType
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "companyHY")
    public String companyHY(@RequestParam("companyType") String companyType, HttpServletResponse response, Model model) {
        model.addAttribute("companyType", companyType);
        model.addAttribute("companySourceJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANY_SOURCE)));
        return "index/businessManagerSkip/companyHY";
    }
}
