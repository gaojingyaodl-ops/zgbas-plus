package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.vo.ApplyConfirmReceiptVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;
import java.util.List;

public interface IApplyConfrimReceiptService extends IBaseService<ApplyConfirmReceipt> {

    /**
     * 新建确认收货申请
     */
    void applyConfirmReceipt(ApplyConfirmReceiptVo confirmReceiptVo)throws ApplicationException;

    List<ApplyConfirmReceipt> findByContractId(Long contractId);

    void updateFileId(Long id, String fileId);

    List<Long> findContractIdByDate(Date beginDate, Date endDate);

    void doSignLogistics();

    Date findMaxConfirmDate(Long contractId);
}
