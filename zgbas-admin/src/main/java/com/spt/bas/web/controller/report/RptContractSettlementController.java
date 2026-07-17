package com.spt.bas.web.controller.report;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.report.client.entity.RptContractSettlementVo;
import com.spt.bas.report.client.remote.IRptContractSettlementClient;
import com.spt.bas.report.client.vo.RptContractSettlementSearchVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.EasyTreeUtil2;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import com.spt.tools.data.vo.PageDown;
import com.spt.tools.web.util.JsonEasyUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Controller
@RequestMapping(value = "/rpt/contractSettlement")
public class RptContractSettlementController {
    
    @Autowired
    private IRptContractSettlementClient contractSettlementClient;
    @Resource
    private WebParamUtils webParamUtils;

    @RequestMapping(value = "content")
    public String content(Model model, HttpServletRequest request) {
        model.addAttribute("companyStatus",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_COMPANY_STATUS)));
        model.addAttribute("creditRating",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CREDITRATING)));// 信用等级
        model.addAttribute("companyType",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_COMPANYTYPE)));// 客户分类
        model.addAttribute("companyGrade",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_COMPANYGRADE)));// 客户分类
        model.addAttribute("onLineFlg",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ONLiNEFLG)));// 线上化查询

        model.addAttribute("matchUserId", request.getParameter("matchUserId"));
        model.addAttribute("deptId", request.getParameter("deptId"));
        //获取业务员树
        List<SysDeptSdk> deptList = webParamUtils.getDeptAll();
        EasyTreeNode nodes = EasyTreeUtil2.getDeptTree(deptList, true,true);
        model.addAttribute("deptJson", JsonUtil.obj2Json(deptList));
        model.addAttribute("matchUserNameTree",JsonUtil.obj2Json(nodes.getChildren()));

        model.addAttribute("settlementStatus",
                JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_SETTLEMENT_STATUS)));// 结算状态
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        String budgetType = request.getParameter("budgetType");
        String receiveDateStart = request.getParameter("receiveDateStart");
        String receiveDateEnd = request.getParameter("receiveDateEnd");
        model.addAttribute("budgetType",budgetType);
        model.addAttribute("receiveDateStart",receiveDateStart);
        model.addAttribute("receiveDateEnd",receiveDateEnd);


        return "report/contractSettlement";
    }

    /**
     * 结算数据列表查询
     * @param searchVo
     * @param request
     * @param response
     */
    @RequestMapping(value = "findCreditAmountMonitorPage")
    public void findCreditAmountMonitorPage(RptContractSettlementSearchVo searchVo, HttpServletRequest request, HttpServletResponse response){
        handelSearchParams(searchVo);
        PageDown<RptContractSettlementVo> page = contractSettlementClient.findRptContractSettlementPage(searchVo);
        JsonEasyUI.renderJson(response, page,null,getFooter(searchVo));
    }
    /**
     * 合计
     *
     * @return 合计
     */
    private Map<String, Object> getFooter(RptContractSettlementSearchVo searchVo) {
        Map<String, Object> result = new HashMap<>();
        handelSearchParams(searchVo);
        RptContractSettlementVo sum = contractSettlementClient.findRptContractSettlementSum(searchVo);
        if (Objects.nonNull(sum)) {
            result.put("approveNo", "合计");
            result.put("dealNumber", sum.getDealNumber());
            result.put("buyPrice", sum.getBuyPrice());
            result.put("sellPrice", sum.getSellPrice());
            result.put("buyTotalAmount", sum.getBuyTotalAmount());
            result.put("sellTotalAmount", sum.getSellTotalAmount());
            result.put("financialServiceAmount", sum.getFinancialServiceAmount());
            result.put("vatAmount", sum.getVatAmount());
            result.put("surchargeAmount", sum.getSurchargeAmount());
            result.put("printAmount", sum.getPrintAmount());
            result.put("afterTaxSpreadAmount", sum.getAfterTaxSpreadAmount());
            result.put("warehouseAmount", sum.getWarehouseAmount());
            result.put("transportAmount", sum.getTransportAmount());
            result.put("deliveryFee", sum.getDeliveryFee());
            result.put("stevedorage", sum.getStevedorage());
            result.put("insuranceAmount", sum.getInsuranceAmount());
        }
        return result;
    }
    public void handelSearchParams(RptContractSettlementSearchVo searchVo){
        List<BsDictData> listByCategory = BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID, BasConstants.DICT_SETTLEMENT_NOT_DEPT);
        List<Long> notDeptIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listByCategory)) {
            for (BsDictData bsDictData : listByCategory) {
                notDeptIds.add(Long.valueOf(bsDictData.getDictCd()));
            }
        }
        searchVo.setNotDeptIds(notDeptIds);
    }


}
