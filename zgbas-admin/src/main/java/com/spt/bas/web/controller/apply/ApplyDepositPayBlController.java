package com.spt.bas.web.controller.apply;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsCompanyOurUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.BsDictConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.IApplyPayClient;
import com.spt.bas.client.remote.ICtrContractClient;
import com.spt.bas.client.remote.IFileProcessRelClient;
import com.spt.bas.client.vo.CtrContractChooseVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.vo.PmPermissionVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.JsonEasyUI;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 保理预算--保证金付款
 */
@Controller
@RequestMapping(value = "/apply/depositPayBl")
public class ApplyDepositPayBlController extends PageController<ApplyPay, BaseVo> {

    @Autowired
    private IApplyPayClient applyPayClient;
    @Resource
    private WebParamUtils webParamUtils;
    @Autowired
    private IApplyPayClient payClient;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IFileProcessRelClient fileProcessRelClient;

    @Override
    public BaseClient<ApplyPay> getService() {
        return applyPayClient;
    }


    /** 审批模板内容 */
    @RequestMapping(value = "content/{id}", method = RequestMethod.GET)
    public String content(@PathVariable("id") Long id, PmPermissionVo permissionVo, Model model, HttpServletRequest request) {
        ApplyPay entity = getEntity(id);
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));
        List<BsDictData> lstPayType = BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(),BsDictConstants.DICT_BL_TYPE_PAYTYPE);
        String applyPayTypeJson;
        if (lstPayType.isEmpty()) {
            applyPayTypeJson = JsonUtil.obj2Json(DictUtil.getListByCategory(BsDictConstants.DICT_BL_TYPE_PAYTYPE));
        }else {
            applyPayTypeJson = JsonUtil.obj2Json(lstPayType);
        }
//        保证金计算方式：
//        1. 销售合同30万以下，合同金额 * 85%后，得到保理金额，金额保留万位（向下取整），然后乘以20%
//                2. 销售合同30万及以上，合同金额 * 90%后，得到保理金额，金额保留万位，然后乘以20%
//                计算公式：保证金额 = (合同金额 X 0.85) 向下取整到万位 X 0.2

        //    计算方式调整2022-09-28 ：
        //    计算公式：保证金额 = (合同金额 X 0.9) 向下取整到万位 X 0.1
        String contractId = request.getParameter("contractId");
        if (!"null".equals(contractId)) {
            CtrContractChooseVo contract = ctrContractClient.findByContractId(Long.valueOf(contractId));
            BigDecimal totalAmount = contract.getTotalAmount();
            BigDecimal bigDecimal = new BigDecimal(0.9);
            BigDecimal realFactorAmount = BigDecimal.ZERO;
            BigDecimal factorAmount = totalAmount.multiply(bigDecimal);
            BigDecimal divide = factorAmount.divide(new BigDecimal(10000));
            BigDecimal decimalAmount = divide.setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(10000)).multiply(new BigDecimal(0.1));
            realFactorAmount = decimalAmount.setScale(0, BigDecimal.ROUND_HALF_DOWN);
            entity.setFactorAmount(realFactorAmount);
            contract.setFactoringAmount(realFactorAmount);
            ctrContractClient.save(contract);
        }
        model.addAttribute("entity", entity);
        model.addAttribute("applyPayTypeJson", applyPayTypeJson);//付款类型
        model.addAttribute("payModeJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));//付款方式
        //我方抬头
        model.addAttribute("ourCompanyJson", JsonUtil.obj2Json(
                BsCompanyOurUtil.getCompanyOurToBsDictDataList()));
        //银行
        model.addAttribute("bankInfoJson", JsonUtil.obj2Json(
                BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BsDictConstants.DICT_TYPE_BANKINFO)));

        String processCode = request.getParameter("processCode");
        // 附件类型
        List<FileProcessRel> fileTypeList = fileProcessRelClient.findList(processCode);
        model.addAttribute("fileTypeJson", JsonUtil.obj2Json(fileTypeList));

        //处理审批中部分控件可编辑
        permissionVo = webParamUtils.verifyPermission(permissionVo, entity.getApproveId());
        model.addAttribute("psv", permissionVo);
        // 业务类型
        model.addAttribute("business", "保证金付款");
        return "apply/depositPayment_dcsxBl_content";
    }
    @RequestMapping(value = "updateFileId", method = RequestMethod.POST)
    public void updateFileId(FileIdUpdateVo vo,
                             HttpServletResponse response) {
        try {
            payClient.updateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }


    /**
     * 使用@ModelAttribute, 实现Struts2
     * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
     */
    @ModelAttribute("preload")
    public ApplyPay getEntity(@RequestParam(value = "id", required = false) Long id) {
        if (id != null) {
            if (id > 0) {
                return getService().getEntity(id);
            } else {
                ApplyPay entity = new ApplyPay();
                entity.setId(0L);
                entity.setStatus(BasConstants.APPROVE_STATUS_N);
                entity.setPayDate(new Date());
                entity.setCompanyName("余姚市应收账款债权管理有限公司");
                entity.setBankName("宁波余姚农村商业银行营业部");
                entity.setBankAccount("201000189769286");
                return entity;
            }
        }
        return null;
    }

    @RequestMapping(value = "listVo")
    public void listVo(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        Page<ApplyPay> page = findPage(searchVo, request, response);
        Map<String, Object> searchParams = searchVo.getSearchParams();
        searchParams.put("EQS_status", BasConstants.APPROVE_STATUS_D);
        searchVo.setSearchParams(searchParams);
        ApplyPay sum = payClient.findPageSum(searchVo);
        Map<String, Object> footer = new HashMap<>();
        footer.put("payDate", "合计");
        footer.put("payAmount", sum.getPayAmount());
        JsonEasyUI.renderJson(response, page,footer);
    }

    @RequestMapping(value = "queryCancelList", method = RequestMethod.POST)
    public void queryCancelList(PageSearchVo searchVo, HttpServletRequest request, HttpServletResponse response) {
        initSearch(searchVo, request);
        Map<String, Object> map = searchVo.getSearchParams();
        map.put("NEQS_status", BasConstants.APPROVE_STATUS_C);
        map.put("EQL_enterpriseId", ShiroUtil.getEnterpriseId());
        Page<ApplyCancelDetail> page = payClient.findPageDetail(searchVo);

        JsonEasyUI.renderJson(response, page);
    }

    private String getBusiness(ApplyPay pay) {
        String business = "";
        if (pay != null && pay.getContractId() != null && pay.getContractId() != 0L) {

            CtrContract ctrContract = ctrContractClient.getEntity(pay.getContractId());
            String businessType = ctrContract.getBusinessType();
            if (businessType != null) {
                if (StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB, businessType)) {
                    if (ctrContract.getMatchCreditFlg()) {
                        business = "白条";
                    }else {
                        business = "代采";
                    }
                }else {
                    business = DictUtil.getValue(BasConstants.DICT_TYPE_BUSINESS, BasConstants.DICT_TYPE_BUSINESS_ZY);
                }
            }


        }
        return business;
    }

}
