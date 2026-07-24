package com.spt.bas.purchase.wx.server.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.*;
import com.spt.bas.client.entity.CtrContractRela;
import com.spt.bas.client.remote.*;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.purchase.wx.client.constant.PurchaseWxConstant;
import com.spt.bas.purchase.wx.server.common.BaseException;
import com.spt.bas.purchase.wx.server.common.Status;
import com.spt.bas.purchase.wx.server.service.IApplyService;
import com.spt.bas.purchase.wx.server.service.IContractService;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.bas.report.client.entity.*;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 采购管家合同服务接口
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-18 10:48
 */
@Component
@Slf4j
public class ContractServiceImpl implements IContractService {

    Cache<String, Long> applyConfirmUserIdCache = CacheUtil.newLRUCache(100);


    @Autowired
    private IApplyService applyService;

    @Autowired
    private ICtrContractClient contractClient;

    @Autowired
    private ICtrServiceContractClient ctrServiceContractClient;

    @Autowired
    private IApplyDeliveryOutClient applyDeliveryOutClient;

    @Autowired
    private ICtrProductClient ctrProductClient;

    @Autowired
    private ICtrContractRelaClient ctrContractRelaClient;

    @Autowired
    private IApplyConfirmReceiptClient applyConfirmReceiptClient;

    @Autowired
    private IPmApproveClient pmApproveClient;

    /**
     * 确认收货
     *
     * @param confirmReceiptVo
     */
    @Override
    public ApplyConfirmReceiptVo confirmReceipt(RptConfirmReceiptVo confirmReceiptVo) {

        if (confirmReceiptVo.getConfirmReceiptList().isEmpty()) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }
        CtrContract ctr = contractClient.findByContractNoV2(confirmReceiptVo.getContractNo());

        if (ctr == null) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }
        ApplyConfirmReceiptVo vo = new ApplyConfirmReceiptVo();

        for (RptConfirmReceiptDetail detail : confirmReceiptVo.getConfirmReceiptList()) {
            if (checkDeliveryExistConfirmReceipt(detail)) {
                String msg = "当前选择的出库申请已有对应的确认收货申请，一个出库无法对应多个收货确认申请";
                log.error(msg + "，RptConfirmReceiptDetail：{}", JSONUtil.toJsonStr(detail));
                throw new BaseException(Status.APPLY_CHECK_FAIL, msg);
            }
            vo.setCompanyId(ctr.getCompanyId());
            vo.setCompanyName(ctr.getCompanyName());
            vo.setFileId(detail.getFileId());
            vo.setContractId(ctr.getId());
            vo.setContractNo(ctr.getContractNo());
            vo.setId(0L);

            ApplyProductDetail applyProductDetail = applyDeliveryOutClient.findByApplyDeliveryOutApplyNo(detail.getDeliveryId());
            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutClient.findByApplyNo(detail.getDeliveryId());
            List<ApplyDeliveryOut> applyDeliveryOutArrayList = new ArrayList<>(1);
            applyDeliveryOutArrayList.add(applyDeliveryOut);
            applyProductDetail.setId(null);
            applyProductDetail.setApplyDeliveryOutId(applyDeliveryOutClient.findByApplyNo(detail.getDeliveryId()).getId());
            applyProductDetail.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            List<ApplyProductDetail> applyProductDetails = new ArrayList<>(1);
            applyProductDetails.add(applyProductDetail);
            vo.setLstInsert(applyProductDetails);
            vo.setLstUpdate(new ArrayList<>());
            vo.setApplyDeliveryOuts(applyDeliveryOutArrayList);
            vo.setLstDelete(new ArrayList<>());
            vo.setConfirmReceiptDate(confirmReceiptVo.getConfirmReceiptDate());
           // applyService.applyConfirmReceipt(vo);
        }
        applyConfirmUserIdCache.put(confirmReceiptVo.getDeliveryId(), UserHelper.getCurUserId());

        return vo;


    }

    @Override
    public ApplyConfirmReceiptVo confirmReceiptV2(RptConfirmReceiptVo confirmReceiptVo) {
        if (confirmReceiptVo.getConfirmReceiptList().isEmpty()) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }
        CtrContract ctr = contractClient.findByContractNoV2(confirmReceiptVo.getContractNo());
        if (ctr == null) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }
        ApplyConfirmReceiptVo vo = new ApplyConfirmReceiptVo();
        for (RptConfirmReceiptDetail detail : confirmReceiptVo.getConfirmReceiptList()) {
            // 检查当前是出库申请是否已有确认收货申请
            if (checkDeliveryExistConfirmReceipt(detail)) {
                // 作废之前的确认收货申请 以当前申请为主
                ApplyDeliveryOut entity = applyDeliveryOutClient.findByApplyNo(detail.getDeliveryId());
                entity.setConfirmFlg(BasConstants.CONFIRM_FLG_NOT);
                applyDeliveryOutClient.save(entity);
                if (Objects.nonNull(entity.getConfirmReceiptApplyId())) {
                    // 1.作废确认收货
                    ApplyConfirmReceipt confirmReceipt = applyConfirmReceiptClient.getEntity(entity.getConfirmReceiptApplyId());
                    if (Objects.nonNull(confirmReceipt)){
                        confirmReceipt.setStatus(BasConstants.APPROVE_STATUS_C);
                        applyConfirmReceiptClient.save(confirmReceipt);
                        // 2.作废审批单
                        PmApprove approve = pmApproveClient.getEntity(confirmReceipt.getApproveId());
                        approve.setStatus(BasConstants.APPROVE_STATUS_C);
                        pmApproveClient.save(approve);
                    }
                }
                log.info("作废之前的确认收货申请 applyNo:{},contractNo:{}", entity.getApplyNo(), entity.getContractNo());
            }
            vo.setCompanyId(ctr.getCompanyId());
            vo.setCompanyName(ctr.getCompanyName());
            vo.setFileId(detail.getFileId());
            vo.setContractId(ctr.getId());
            vo.setContractNo(ctr.getContractNo());
            vo.setId(0L);

            ApplyDeliveryOut applyDeliveryOut = applyDeliveryOutClient.findByApplyNo(detail.getDeliveryId());

            ApplyProductDetail applyProductDetail = applyDeliveryOutClient.findByApplyDeliveryOutApplyNo(detail.getDeliveryId());
            applyProductDetail.setId(null);
            applyProductDetail.setEnterpriseId(BasConstants.ZG_ENTERPRISE_ID);
            List<ApplyProductDetail> applyProductDetails = new ArrayList<>(1);
            applyProductDetails.add(applyProductDetail);
            vo.setLstInsert(applyProductDetails);
            vo.setLstUpdate(new ArrayList<>());
            vo.setLstDelete(new ArrayList<>());
            vo.setWxUserId(applyConfirmUserIdCache.get(confirmReceiptVo.getDeliveryId()));
            vo.setConfirmReceiptDate(ctr.getPreselectionConfirmDate());
            vo.setContractMatchUserId(ctr.getMatchUserId());
            log.info("confirmReceiptV2 wxUserId:{}", JsonUtil.obj2Json(vo.getWxUserId()));
            log.info("confirmReceiptV2 vo:{}", JsonUtil.obj2Json(vo));
            applyService.applyConfirmReceiptAndFinish(vo);
        }
        return vo;
    }

    /**
     * 申请发货
     *
     * @param applyDeliveryOutPayload
     */
    @Override
    public void applyDeliveryOut(RptApplyDeliveryOutPayload applyDeliveryOutPayload) {
        if (applyDeliveryOutPayload.getDeliveryOutList().isEmpty()) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }
        CtrContract ctr = contractClient.findByContractNoV2(applyDeliveryOutPayload.getContractNo());
        if (ctr == null) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }

        // 拼装申请参数
        ApplyDeliveryOutVo vo = assembleApplyParam(applyDeliveryOutPayload, ctr);

        // 补充参数
        for (RptApplyDeliveryOutDetail delivery : applyDeliveryOutPayload.getDeliveryOutList()) {
            CtrProduct entity = ctrProductClient.getEntity(delivery.getCtrProductId());
            ApplyProductDetail detail = new ApplyProductDetail();
            BeanUtils.copyProperties(entity, detail);
            detail.setId(null);
            detail.setCtrProductId(entity.getId());
            detail.setCurNumber(delivery.getDeliveryOutNumber());
            List<ApplyProductDetail> list = new ArrayList<>();
            list.add(detail);
            vo.setLstInsert(list);
            vo.setFileId(delivery.getFileId());
            vo.setLstUpdate(new ArrayList<>());
            vo.setLstDelete(new ArrayList<>());
            applyService.applyDeliveryOut(vo);
        }
    }

    /**
     * 支付货款
     *
     * @param confirmPayVo
     */
    @Override
    public void confirmPay(RptConfirmPayVo confirmPayVo) {
        CtrContract ctr = contractClient.findByContractNoV2(confirmPayVo.getContractNo());
        if (ctr == null) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }

        ApplyReceive applyReceive = new ApplyReceive();
        applyReceive.setId(0L);
        applyReceive.setContractId(ctr.getId());
        applyReceive.setContractNo(ctr.getContractNo());
        applyReceive.setBusinessNo(ctr.getContractNo());
        applyReceive.setCompanyId(ctr.getCompanyId());
        applyReceive.setCompanyName(ctr.getCompanyName());
        applyReceive.setOurCompanyName(ctr.getOurCompanyName());
        applyReceive.setTotalAmount(ctr.getTotalAmount());
        applyReceive.setPayedAmount(ctr.getDealedAmount());
        // 未付
        applyReceive.setUnpayedAmount(NumberUtil.sub(applyReceive.getTotalAmount(), applyReceive.getPayedAmount()));
        applyReceive.setReceiveDate(new Date());

        // 当前付款等于合同金额 =》 全款A ；否则 部分P
        if (NumberUtil.equals(applyReceive.getTotalAmount(), confirmPayVo.getDealedAmount())) {
            applyReceive.setReceiveType("A");
        } else {
            applyReceive.setReceiveType("P");
        }

        // 默认 电汇
        applyReceive.setReceiveMode("T");
        applyReceive.setReceiveAmount(confirmPayVo.getDealedAmount());
        applyReceive.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));

        applyService.confirmPay(applyReceive);

    }

    /**
     * 支付服务费
     *
     * @param confirmPayVo
     */
    @Override
    public void confirmServicePay(RptConfirmPayVo confirmPayVo) {
        CtrContract ctrContract = contractClient.findByContractNoV2(confirmPayVo.getContractNo());
        if (ctrContract != null) {
            CtrServiceContract serviceContract = ctrServiceContractClient.findByCtrContract(ctrContract.getId());
            if (serviceContract != null) {
                ApplyServiceReceive applyServiceReceive = new ApplyServiceReceive();
                applyServiceReceive.setId(0L);
                applyServiceReceive.setContractId(ctrContract.getId());
                applyServiceReceive.setServiceContractId(serviceContract.getId());
                applyServiceReceive.setServiceContractNo(serviceContract.getServiceContractNo());
                applyServiceReceive.setCompanyId(ctrContract.getCompanyId());
                applyServiceReceive.setCompanyName(ctrContract.getCompanyName());
                applyServiceReceive.setOurCompanyName(ctrContract.getOurCompanyName());
                applyServiceReceive.setReceiveAmount(confirmPayVo.getDealedAmount());
                applyServiceReceive.setReceiveDate(new Date());
                applyServiceReceive.setTotalAmount(serviceContract.getTotalAmount());
                // 已收服务费金额
                BigDecimal receiveServiceAmount = ctrContract.getReceiveServiceAmount();
                // 未收服务费
                applyServiceReceive.setUnReceivedAmount(NumberUtil.sub(applyServiceReceive.getTotalAmount(), receiveServiceAmount));
                // 默认 电汇
                applyServiceReceive.setReceiveMode("T");
                applyService.confirmServicePay(applyServiceReceive);
            }
        }

    }

    /**
     * 申请开票
     *
     * @param applyBillVo
     */
    @Override
    public void applyBill(RptApplyBillVo applyBillVo) {
        CtrContract ctr = contractClient.findByContractNoV2(applyBillVo.getContractNo());
        if (ctr == null) {
            throw new BaseException(Status.APPLY_CHECK_FAIL);
        }

        ApplyInvoice applyInvoice = assembleApplyInvoice(applyBillVo, ctr);
        applyService.applyBill(applyInvoice);
    }

    /**
     * 校验选择的出库是否已有对应的确认收货申请
     *
     * @param detail
     * @return
     */
    private Boolean checkDeliveryExistConfirmReceipt(RptConfirmReceiptDetail detail) {
        ApplyDeliveryOut entity = applyDeliveryOutClient.findByApplyNo(detail.getDeliveryId());
        if (entity == null
                || !BasConstants.CONFIRM_FLG_NOT.equals(entity.getConfirmFlg())
                || !BasConstants.APPROVE_STATUS_D.equals(entity.getStatus())) {
            return true;
        }
        return false;
    }


    /**
     * 拼装申请参数
     *
     * @param applyDeliveryOutPayload
     * @param ctr
     * @return
     */
    private ApplyDeliveryOutVo assembleApplyParam(RptApplyDeliveryOutPayload applyDeliveryOutPayload, CtrContract ctr) {
        // 拼装申请参数
        ApplyDeliveryOutVo vo = new ApplyDeliveryOutVo();
        vo.setContractId(ctr.getId());
        vo.setContractNo(ctr.getContractNo());
        vo.setBusinessNo(ctr.getContractNo());
        vo.setDeliveryMode(BasConstants.DELIVERY_MODE_XKHH);
        // 详细地址
        vo.setContactAddr(applyDeliveryOutPayload.getAreaName());
        // 所在地
        vo.setDeliveryAddr(applyDeliveryOutPayload.getCurAreaName());
        // 已付金额
        vo.setPayAmount(ctr.getDealedAmount());
        vo.setContactName(applyDeliveryOutPayload.getContactPerson());
        vo.setContactPhone(applyDeliveryOutPayload.getContactPhone());
        vo.setWareCompanyName(applyDeliveryOutPayload.getWareCompanyName());

        vo.setCompanyId(ctr.getCompanyId());
        vo.setCompanyName(ctr.getCompanyName());
        vo.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));

        // 自提
        if (BasConstants.DICT_TYPE_BUYDELIVERY_Z.equals(ctr.getDeliveryType())) {
            vo.setWarehouseOutType("AZT");
        }else {
            // 配送 我司配送
            vo.setWarehouseOutType("APS");
        }
        // 出库时间
        vo.setWarehouseOutDate(new Date());
        vo.setId(0L);

        return vo;
    }

    /**
     * 根据销售合同id获取上游公司id
     *
     * @param contractId
     * @return
     */
    private Long getBuyCompanyIdBySellContractId(Long contractId) {
        Long buyCompanyId = null;
        CtrContractRela rela = ctrContractRelaClient.getRelaBySellContractId(contractId);
        if (rela != null) {
            buyCompanyId = rela.getBuyCompanyId();
        }
        return buyCompanyId;
    }

    /**
     * 拼装applyInvoice审批内容信息
     *
     * @param applyBillVo
     * @param ctr 销售合同
     * @return
     */
    private ApplyInvoice assembleApplyInvoice(RptApplyBillVo applyBillVo, CtrContract ctr) {
        ApplyInvoice applyInvoice = new ApplyInvoice();
        // 开货款发票
        if (PurchaseWxConstant.APPLY_INVOICE_TYPE_AMOUNT.equals(applyBillVo.getType())) {
            applyInvoice.setContractId(ctr.getId());
            applyInvoice.setContractNo(ctr.getContractNo());
            applyInvoice.setTotalAmount(ctr.getTotalAmount());
            applyInvoice.setBilledAmount(ctr.getBilledAmount());
            applyInvoice.setReceiveAmount(ctr.getDealedAmount());
        } else if (PurchaseWxConstant.APPLY_INVOICE_TYPE_SERVICE_AMOUNT.equals(applyBillVo.getType())) {
            // 开服务费发票
            CtrServiceContract serviceContract = ctrServiceContractClient.findByCtrContract(ctr.getId());
            applyInvoice.setContractId(serviceContract.getId());
            applyInvoice.setContractNo(serviceContract.getServiceContractNo());
            applyInvoice.setTotalAmount(ctr.getServiceAmount());
            applyInvoice.setBilledAmount(ctr.getServiceBilledAmount());
            applyInvoice.setReceiveAmount(ctr.getReceiveServiceAmount());
        }
        applyInvoice.setInvoiceType(applyBillVo.getType());
        applyInvoice.setInvoiceDate(new Date());
        applyInvoice.setCompanyId(ctr.getCompanyId());
        applyInvoice.setCompanyName(ctr.getCompanyName());
        applyInvoice.setOurCompanyName(ctr.getOurCompanyName());
        applyInvoice.setDealAmount(applyBillVo.getBilledAmount());
        applyInvoice.setBankName(applyBillVo.getBankName());
        applyInvoice.setBankAccount(applyBillVo.getBankAccount());
        applyInvoice.setTaxNo(applyBillVo.getTaxNo());
        applyInvoice.setAddress(applyBillVo.getContactAddress());
        applyInvoice.setCompanyPhone(applyBillVo.getContactPhone());
        applyInvoice.setContactPerson(applyBillVo.getContactPerson());
        applyInvoice.setBuyCompanyId(getBuyCompanyIdBySellContractId(ctr.getId()));
        applyInvoice.setId(0L);
        return applyInvoice;
    }

}
