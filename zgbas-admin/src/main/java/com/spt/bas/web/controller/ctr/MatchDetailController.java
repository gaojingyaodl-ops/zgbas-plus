package com.spt.bas.web.controller.ctr;

import com.spt.auth.sdk.cache.DictUtil;
import com.spt.bas.client.cache.BsDictUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.constant.PermissionEnum;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyDcsxChooseVo;
import com.spt.bas.client.vo.BudgetSettlementVo;
import com.spt.bas.client.vo.FileIdUpdateVo;
import com.spt.bas.web.shiro.ShiroUtil;
import com.spt.bas.web.util.WebParamUtils;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.json.JsonUtil;
import com.spt.tools.data.service.BaseClient;
import com.spt.tools.data.vo.BaseVo;
import com.spt.tools.web.controller.PageController;
import com.spt.tools.web.util.RenderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping(value = "/bs/matchDetail")
public class MatchDetailController extends PageController<CtrContract, BaseVo> {
    @Autowired
    private IApplyCtrDcsxClinent applyCtrDcsxClinent;
    @Autowired
    private ICtrContractClient ctrContractClient;
    @Autowired
    private IApplyMatchDetailClient applyMatchDetailClient;
    @Autowired
    private IApplyMatchClient applyMatchClient;
    @Autowired
    private IBudgetSettlementClient budgetSettlementClient;
    @Autowired
    private ICtrContractChainClient ctrContractChainClient;
    @Autowired
    private IPmApproveClient pmApproveClient;
    @Value("${file.show.url}")
    private String fileShowUrl;

    /***
     * 撮合申请单(代采)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content/{id}")
    public String content(Model model, @PathVariable("id") Long id) {
        String dcsxFlg="false";
        queryMatchDetailsInfo(model, id,dcsxFlg);
        return "apply/match-contract-detail-ctr";
    }

    /***
     * 撮合申请单(白条)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content2/{id}")
    public String content2(Model model, @PathVariable("id") Long id) {
        String dcsxFlg="false";
        queryMatchDetailsInfo(model, id,dcsxFlg);
        return "apply/match-contract-detail-ctr";

    }

    /***
     * 撮合申请单(托盘)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "content3/{id}")
    public String content3(Model model, @PathVariable("id") Long id) {
        String dcsxFlg="false";
        queryMatchDetailsInfo(model, id,dcsxFlg);
        return "apply/match-contract-detail-ctr";
    }

    /***
     * 撮合申请单(托盘)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "contentTP/{id}")
    public String contentTP(Model model, @PathVariable("id") Long id, HttpServletRequest request) {
        String dctpMiddleFlg = request.getParameter("dctpMiddleFlg");
        String dcsxFlg="false";
        queryMatchDetailsInfo(model, id,dctpMiddleFlg);
        return "apply/match-contract-detail-ctr-tp";
    }

    /***
     * 撮合申请单(代采赊销)
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "contentdcsx/{id}")
    public String contentdcsx(Model model, @PathVariable("id") Long id) {
        String dcsxFlg="true";
        queryMatchDetailsInfo(model, id,dcsxFlg);
        return "apply/match-contract-detail-ctr";
    }

    @RequestMapping(value = "contentdcsx2/{id}")
    public String contentdcsx2(Model model, @PathVariable("id") Long id) {
        String dcsxFlg="true";
        ApplyDcsxChooseVo byId = applyCtrDcsxClinent.findById(id);
        queryMatchDetailsInfo(model, byId.getApproveId(),dcsxFlg);
        return "apply/match-contract-detail-ctr";
    }



    /***
     * 代采赊销 盖章
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "dcsx/{id}")
    public String dcsx(Model model, @PathVariable("id") Long id) {
        return "apply/contract-seal-usage";
    }

    @Override
    public BaseClient<CtrContract> getService() {
        return ctrContractClient;
    }
    private void queryMatchDetailsInfo(Model model, Long id,String flg) {
        model.addAttribute("fundSpecialViewFlag", false);
        model.addAttribute("specialChainContractId", 0);
        boolean fundViewFlag = ShiroUtil.isPermitted(PermissionEnum.ZGBAS_NEW_USER_FUNDER.getPermissionCode());
        ApplyCtrDCSX byDCSXApproveId = applyCtrDcsxClinent.findByDCSXApproveId(id);
        List<CtrContract> ctrContractsList =  ctrContractClient.findByApproveId(id);
        ApplyMatch applyMatch = applyMatchClient.findByApproveId(id);
        if (applyMatch == null) {
            logger.error("找不到撮合申请单:{}", id);
            return;
        }
        if (ShiroUtil.isPermitted(PermissionEnum.PERM_ADMIN_NEW.getPermissionCode())) {
            model.addAttribute("isAdmin",true);
        }
        //预算概况
        PmApprove pmApprove = pmApproveClient.findApproveNoByApproveId(id);
        model.addAttribute("approve", pmApprove);
        String businessKind = "";
        boolean buyDealedAmountFlg = false;
        boolean sellDealedAmountFlg = false;
        for (CtrContract ctrContract : ctrContractsList) {
            businessKind = ctrContract.getBusinessKind();
            CtrContract specialChainContract = ctrContractClient.findSpecialChainContract(ctrContract.getApproveId());
            ApplyMatchDetail applyMatchDetail  = applyMatchDetailClient.findByContractNo(ctrContract.getContractNo());
            if(ctrContract.getContractType().equals(BasConstants.CONTRACTTYPE_SELL)){
                applyMatchDetail.setSettlementType(ctrContract.getSettlementType());
                model.addAttribute("discountChargeAmount", ctrContract.getDiscountChargeAmount());
                model.addAttribute("discountReceiveAmount", ctrContract.getDiscountReceiveAmount());
                model.addAttribute("discountChargeTarget", BsDictUtil.getValue(ctrContract.getEnterpriseId(),
                        BasConstants.DICT_DISCOUNT_TARGET, ctrContract.getDiscountChargeTarget()));
            }
            model.addAttribute("contractStatus",ctrContract.getContractStatus());
            if(ctrContract.getContractType().equals(BasConstants.CONTRACTTYPE_BUY)){
                if (Boolean.TRUE.equals(fundViewFlag) && (Boolean.TRUE.equals(WebParamUtils.verifySpecialChain(applyMatch)))){
                    model.addAttribute("fundSpecialViewFlag", true);
                    ctrContract = Objects.nonNull(specialChainContract) ? specialChainContract : ctrContract;
                    applyMatchDetail.setContractId(specialChainContract.getId());
                }
                if (Boolean.FALSE.equals(fundViewFlag) && Objects.nonNull(specialChainContract)){
                    model.addAttribute("specialChainContractId", specialChainContract.getId());
                }
                applyMatchDetail.setApproveTransportAmount(ctrContract.getApproveTransportAmount());
                applyMatchDetail.setApproveWarehouseAmount(ctrContract.getApproveWarehouseAmount());
                //装卸费
                applyMatchDetail.setStevedorage(ctrContract.getStevedorage());
                //我方
                // applyMatch.setOurCompanyName(ctrContract.getOurCompanyName());
                //供方
                applyMatchDetail.setCompanyName(ctrContract.getCompanyName());
                //合同编号
                applyMatchDetail.setContractNo(ctrContract.getContractNo());
                //业务员
                applyMatchDetail.setMatchUserName(ctrContract.getMatchUserName());
                //结算方式
                applyMatchDetail.setDeliveryMode(ctrContract.getDeliveryMode());
                //定金比例
                applyMatchDetail.setPayRate(ctrContract.getBondRate());
                //定金
                applyMatchDetail.setPayRateAmount(ctrContract.getBondAmount());
                //付全款日期
                applyMatchDetail.setPayFullTime(ctrContract.getPayFullTime());
                //付定金日期
                applyMatchDetail.setPayBondTime(ctrContract.getPayBondTime());
                //交货方式
                applyMatchDetail.setDeliveryType(ctrContract.getDeliveryType());
                //交货日期
                applyMatchDetail.setDeliveryDate(ctrContract.getDeliveryDateTo());
                //交货地点
                applyMatchDetail.setDeliveryAddr(ctrContract.getDeliveryAddr());
                //详细地址
                applyMatchDetail.setContactAddr(ctrContract.getContactAddr());
                //支付方式
                applyMatchDetail.setPayType(ctrContract.getPayType());
                //仓储费
                applyMatchDetail.setWarehouseCost(ctrContract.getWarehouseAmount());
                //运输费
                applyMatchDetail.setTransportCost(ctrContract.getTransportAmount());
                //含税单价
                applyMatchDetail.setDealPrice(ctrContract.getDealPrice());
                //不含税单价
                applyMatchDetail.setDealAmountNotax(ctrContract.getDealAmountNoTax());
                //总价
                applyMatchDetail.setTotalAmount(ctrContract.getTotalAmount());
                //补充条款
                applyMatchDetail.setExtraTerm(ctrContract.getExtraTerm());
                //备注
                applyMatchDetail.setPayRemark(ctrContract.getRemark());

                applyMatchDetail.setConfirmDate(ctrContract.getConfirmDate());

                // 修改数量
                applyMatch.setDealNumber(ctrContract.getTotalNumber());

                //统一返回
                matchDetailByContract(model, applyMatch, ctrContract, applyMatchDetail);
                model.addAttribute("buyProductDetailList", applyMatchDetail);
                // 采购来源
                model.addAttribute("buySourceJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_SOURCE)));
                // 销售来源
                model.addAttribute("sellSourceJson", JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_SOURCE)));
                // 采购结算方式
                model.addAttribute("buyDeliveryModeJson",
                        JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUY_DELIVERYMODE)));
                // 提货方式
                model.addAttribute("deliveryTypeJson",
                        JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_BUYDELIVERY)));
                String buyContractFileId = StringUtils.isEmpty(ctrContract.getBuyContentFileId()) ? "" : fileShowUrl + "/view/show/"+ctrContract.getBuyContentFileId().split(",")[0];
                model.addAttribute("buyContractFileId", buyContractFileId);
                model.addAttribute("virtualContractId", ctrContract.getVirtualContractId());
                BigDecimal dealedAmount = ctrContract.getDealedAmount();
                BigDecimal totalAmount = ctrContract.getTotalAmount();
                if (dealedAmount.compareTo(totalAmount) >= 0) {
                    buyDealedAmountFlg = true;
                }

            }else{
                //合同编号
                List<ApplyMatchDetail> sellList = applyMatchDetailClient.findByApproveId(id);
                for (ApplyMatchDetail matchDetail : sellList) {
                    if(matchDetail.getContractType().equals(BasConstants.CONTRACTTYPE_SELL)){
                        if(matchDetail.getContractId()!=null){
                            BudgetSettlementVo settlement = budgetSettlementClient.findBySellContractId(matchDetail.getContractId());
                            model.addAttribute("settlement", settlement);
                        }
                        CtrContract sellContract=new CtrContract();
                        if(StringUtils.equals(BasConstants.BUSINESS_TYPE_ZY_BB_C,matchDetail.getBusinessType())){
                            CtrContractChain byContractNo = ctrContractChainClient.findByContractNo(matchDetail.getContractNo());
                            BeanUtils.copyProperties(byContractNo, sellContract);
                        }else{
                            sellContract = ctrContractClient.findByContractNoV2(matchDetail.getContractNo());
                        }
                        String sellContractFileId = StringUtils.isEmpty(sellContract.getSellContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getSellContentFileId().split(",")[0];
                        String serviceContractFileId = StringUtils.isEmpty(sellContract.getServiceContentFileId()) ? "" : fileShowUrl + "/view/show/"+sellContract.getServiceContentFileId().split(",")[0];
                        model.addAttribute("sellContractFileId", sellContractFileId);
                        model.addAttribute("serviceContractFileId", serviceContractFileId);
                        if(StringUtils.equals(BasConstants.BUSINESS_TYPE_HDFK,ctrContract.getContractModel())){
                            model.addAttribute("contractBondRateSellJson",
                                    JsonUtil.obj2Json(BsDictUtil.getListByCategory(BasConstants.ZG_ENTERPRISE_ID,BasConstants.DEPOSIT_PROPORTION)));
                        }
                    }
                }
                applyMatchDetail.setApproveTransportAmount(ctrContract.getApproveTransportAmount());
                applyMatchDetail.setApproveWarehouseAmount(ctrContract.getApproveWarehouseAmount());
                applyMatchDetail.setStevedorage(ctrContract.getStevedorage());
                applyMatchDetail.setGrossProfit(ctrContract.getGrossProfit());
                //需方
                // applyMatchDetail.setCompanyName(ctrContract.getCompanyName());
                //合同编号
                applyMatchDetail.setContractNo(ctrContract.getContractNo());
                //业务员
                applyMatchDetail.setMatchUserName(ctrContract.getMatchUserName());
                //结算方式
                applyMatchDetail.setDeliveryMode(ctrContract.getDeliveryMode());
                //支付方式
                applyMatchDetail.setReceiveType(ctrContract.getPayType());
                //定金比例
                applyMatchDetail.setReceiveRate(ctrContract.getBondRate());
                //定金
                applyMatchDetail.setReceiveBondAmount(ctrContract.getBondAmount());
                //付全款日期
                applyMatchDetail.setReceiveFullTime(ctrContract.getPayFullTime());
                //付定金日期
                applyMatchDetail.setPayBondTime(ctrContract.getPayBondTime());
                //交货方式
                applyMatchDetail.setDeliveryType(ctrContract.getDeliveryType());
                //交货日期
                applyMatchDetail.setDeliveryDate(ctrContract.getDeliveryDateTo());
                //交货地点
                applyMatchDetail.setDeliveryAddr(ctrContract.getDeliveryAddr());
                //详细地址
                applyMatchDetail.setContactAddr(ctrContract.getContactAddr());
                //仓储费
                applyMatchDetail.setWarehouseCost(ctrContract.getWarehouseAmount());
                //运输费
                applyMatchDetail.setTransportCost(ctrContract.getTransportAmount());
                //加价
                applyMatchDetail.setPremium(ctrContract.getPremium());
                //销售单价
                applyMatchDetail.setDealPrice(ctrContract.getDealPrice());
                //销售总价
                applyMatchDetail.setTotalAmount(ctrContract.getTotalAmount());
                //补充条款
                applyMatchDetail.setExtraTerm(ctrContract.getExtraTerm());
                // 修改数量
                applyMatch.setDealNumber(ctrContract.getTotalNumber());

                applyMatchDetail.setBreachDays(ctrContract.getBreachDays());
                applyMatchDetail.setBreachAmount(ctrContract.getBreachAmount());
                applyMatchDetail.setReceiveBreachAmount(ctrContract.getReceiveBreachAmount());

                applyMatchDetail.setConfirmDate(ctrContract.getConfirmDate());
                
                //统一返回
                matchDetailByContract(model, applyMatch, ctrContract, applyMatchDetail);
                model.addAttribute("sellProductDetailList", applyMatchDetail);
                model.addAttribute("debtCertificateFileId", ctrContract.getDebtCertificateFileId());
                model.addAttribute("sealFlg", ctrContract.getSealFlg());
                if(byDCSXApproveId!=null){
                    model.addAttribute("id", byDCSXApproveId.getId());

                }
                model.addAttribute("dcsxFlg", flg);

                // 销售结算方式
                model.addAttribute("sellDeliveryModeJson",
                        JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_SELL_DELIVERYMODE)));
                // 销售方式
                model.addAttribute("deliveryModeJson", JsonUtil.obj2Json(
                        BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_TYPE_DELIVERYMODE)));
                model.addAttribute("appointPayFullTime", ctrContract.getAppointPayFullTime());
                model.addAttribute("realPayFullTime", ctrContract.getRealPayFullTime());
                BigDecimal dealedAmount = ctrContract.getDealedAmount();
                BigDecimal totalAmount = ctrContract.getTotalAmount();
                if (dealedAmount.compareTo(totalAmount) >= 0) {
                    sellDealedAmountFlg = true;
                }
            }
        }
        model.addAttribute("businessKind", businessKind);
        model.addAttribute("businessKindJson", JsonUtil.obj2Json(BsDictUtil.getListByCategory(ShiroUtil.getEnterpriseId(), BasConstants.DICT_BUSINESS_KIND)));
        model.addAttribute("companyNameSDNH", BasConstants.COMPANY_NAME_SDNH);
        model.addAttribute("companyNameSUGX", BasConstants.COMPANY_NAME_SUGX);
        model.addAttribute("approveStatusJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_APPROVESTATUS)));

        // 合同预览权限
        model.addAttribute("permCtrPreview", ShiroUtil.isPermitted(PermissionEnum.PERM_CTR_PREVIEW.getPermissionCode()));
        model.addAttribute("buyDealedAmountFlg", buyDealedAmountFlg);
        model.addAttribute("sellDealedAmountFlg", sellDealedAmountFlg);
    }
    /**
     * 统一返回相同的参数
     * @param model
     * @param applyMatch
     * @param ctrContract
     * @param matchDetail
     */
    private void matchDetailByContract(Model model, ApplyMatch applyMatch, CtrContract ctrContract, ApplyMatchDetail matchDetail) {
        // 支付方式
        model.addAttribute("payTypeJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_APPLY_PAYMODE)));
        // 定金比例
        model.addAttribute("contractBondRateJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_CONTRACT_BOND_RATE)));
        // 包装规格
        model.addAttribute("packingSpecificaJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_PACKINGSPECIFICA)));
        // 质量标准
        model.addAttribute("qualityStandardJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_QUALITYSTANDDARD)));
        // 交货时间的补充字段
        model.addAttribute("arrivalTimeExtJson",
                JsonUtil.obj2Json(DictUtil.getListByCategory(BasConstants.DICT_TYPE_ATTACHDELIVERYTIME)));
        model.addAttribute("match", applyMatch);
    }

    @PostMapping(value = "content4/{contractNo}")
    public void content4(@PathVariable("contractNo") String contractNo, HttpServletResponse response){

        CtrContract contract = ctrContractClient.findByContractNoV2(contractNo);
        RenderUtil.renderJson(contract,response);
    }

    @RequestMapping(value = "content5/{contractId}")
    public void content5(@PathVariable("contractId") Long contractId, HttpServletResponse response){
        ApplyDcsxChooseVo contract = applyCtrDcsxClinent.findById(contractId);
        RenderUtil.renderJson(contract,response);
    }

    @RequestMapping(value = "updateDebtCertificateFileId", method = RequestMethod.POST)
    public void updateDebtCertificateFileId(FileIdUpdateVo vo, HttpServletResponse response) {
        try {
            ctrContractClient.updateDebtCertificateFileId(vo);
            RenderUtil.renderSuccess("success", response);
        } catch (Exception e) {
            logger.error("errorId:", e);
            RenderUtil.renderFailure("errorId:" + e.getMessage(), response);
        }
    }
}
