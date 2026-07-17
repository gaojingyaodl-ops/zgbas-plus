package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyInvoiceDelivery;
import com.spt.tools.jpa.service.IBaseService;

/**
 * @author shengong
 */
public interface IApplyInvoiceDeliveryService extends IBaseService<ApplyInvoiceDelivery> {

    /**
     * 发起发票寄送申请
     * @param applyInvoiceDelivery
     */
    void startInvoiceDelivery(ApplyInvoiceDelivery applyInvoiceDelivery);

    void updateFileId(Long id, String fileId);

}
