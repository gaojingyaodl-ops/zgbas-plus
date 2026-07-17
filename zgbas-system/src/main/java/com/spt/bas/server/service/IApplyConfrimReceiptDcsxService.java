package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyConfirmReceiptDcsx;
import com.spt.bas.client.entity.ApplyDeliveryOut;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ApplyConfirmReceiptDcsxVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyConfrimReceiptDcsxService extends IBaseService<ApplyConfirmReceiptDcsx> {

    /**
     * 新建确认收货申请
     */
    void applyConfirmReceiptDcsx(ApplyConfirmReceiptDcsxVo confirmReceiptDcsxVo) throws ApplicationException;

    List<ApplyConfirmReceiptDcsx> findByContractId(Long contractId);

    /**
     * 中游确认收货生成电子签
     *
     * @param entity
     * @param lstDetail
     * @return
     */
    ApplyConfirmReceiptDcsx generateSignature(ApplyConfirmReceiptDcsx entity, List<ApplyProductDetail> lstDetail);

    /**
     * 中游确认收货回调更新
     *
     * @param confirmReceiptDcsx
     */
    void signatureComplete(ApplyConfirmReceiptDcsx confirmReceiptDcsx);

    void updateFileId(Long id, String fileId);

    void autoApplyConfirmReceiptDcsx(CtrContract contract, ApplyDeliveryOut entity, List<ApplyProductDetail> lstDetail);

    void initHistoryConfirmReceiptDcsx();
}
