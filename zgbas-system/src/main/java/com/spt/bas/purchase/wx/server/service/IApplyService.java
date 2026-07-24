package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.client.vo.ApplyDeliveryOutVo;
import com.spt.bas.purchase.wx.server.payload.CfcaRequest;

public interface IApplyService {
    /**
     * 入金验证申请
     *
     * @param deposit
     */
    void applyDeposit(ApplyDeposit deposit);

    /**
     * 准入申请
     *
     * @param bsCompanyAllowed
     */
    void applyAdmittance(BsCompanyAllowed bsCompanyAllowed);

    /**
     * 企业资料审核
     *
     * @param applyCompanyInfo
     */
    void applyCompanyInfo(ApplyCompanyInfo applyCompanyInfo);

    /**
     * 委托授权审核
     * @param entrust
     */
    void applyEntrust(ApplyEntrust entrust);

    /**
     * 浙塑网站委托授权审核
     * @param entrust
     */
    void applyEntrustCms(ApplyEntrust entrust);

    /**
     * 发起意见反馈审批
     * @param feedback
     */
    void applyFeedback(Feedback feedback);

    /**
     * 发起申请成为合伙人审批
     * @param partner
     */
    void applyPartner(ApplyPartner partner);

    /**
     * 发起cfca审批
     * @param applyCfca
     */
    void applyCfca(ApplyCfca applyCfca);

    /**
     * 发起确认收货审批
     * @param applyConfirmReceiptVo
     */
    void applyConfirmReceipt(ApplyConfirmReceiptVo applyConfirmReceiptVo);

    void applyConfirmReceiptAndFinish(ApplyConfirmReceiptVo applyConfirmReceiptVo);

    /**
     * 发起出库审批
     * @param applyDeliveryOutVo
     */
    void applyDeliveryOut(ApplyDeliveryOutVo applyDeliveryOutVo);

    /**
     * 收货款申请
     * @param applyReceive
     */
    void confirmPay(ApplyReceive applyReceive);

    /**
     * 支付服务费申请
     * @param applyServiceReceive
     */
    void confirmServicePay(ApplyServiceReceive applyServiceReceive);

    /**
     * 申请开票
     * @param applyInvoice
     */
    void applyBill(ApplyInvoice applyInvoice);


    /**
     * cfca上传资料合并开户
     * @param
     */
     void ApplyWxCfca(ApplyWxCfca applyWxCfca);

}
