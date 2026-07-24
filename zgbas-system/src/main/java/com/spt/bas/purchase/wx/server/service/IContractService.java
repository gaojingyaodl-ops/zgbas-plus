package com.spt.bas.purchase.wx.server.service;

import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.bas.report.client.entity.RptApplyBillVo;
import com.spt.bas.report.client.entity.RptApplyDeliveryOutPayload;
import com.spt.bas.report.client.entity.RptConfirmPayVo;
import com.spt.bas.report.client.entity.RptConfirmReceiptVo;

/**
 * 采购管家合同服务接口
 * @author shengong
 */
public interface IContractService {
    /**
     * 确认收货
     * @param confirmReceiptVo
     */
    ApplyConfirmReceiptVo confirmReceipt(RptConfirmReceiptVo confirmReceiptVo);

    ApplyConfirmReceiptVo confirmReceiptV2(RptConfirmReceiptVo confirmReceiptVo);


    /**
     * 申请发货
     * @param applyDeliveryOutPayload
     */
    void applyDeliveryOut(RptApplyDeliveryOutPayload applyDeliveryOutPayload);

    /**
     * 支付货款
     * @param confirmPayVo
     */
    void confirmPay(RptConfirmPayVo confirmPayVo);

    /**
     * 支付服务费
     * @param confirmPayVo
     */
    void confirmServicePay(RptConfirmPayVo confirmPayVo);

    /**
     * 申请开票
     * @param applyBillVo
     */
    void applyBill(RptApplyBillVo applyBillVo);

}
