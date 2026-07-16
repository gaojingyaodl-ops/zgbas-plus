package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.bas.client.entity.ApplyConfirmReceiptDcsx;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ApplyConfirmReceiptDcsxDao extends BaseDao<ApplyConfirmReceiptDcsx> {
    List<ApplyConfirmReceiptDcsx> findByContractNo(String contractNo);

    @Query("from ApplyConfirmReceiptDcsx a where a.contractId = ?1 and a.status != 'C' order by id desc")
    List<ApplyConfirmReceiptDcsx> findByContractId(Long contractId);

    ApplyConfirmReceiptDcsx findByApproveId(Long approveId);

    ApplyConfirmReceiptDcsx findByApplyNo(String applyNo);

    @Transactional
    @Modifying
    @Query("update ApplyConfirmReceiptDcsx c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query("from ApplyConfirmReceiptDcsx a where a.contractId in ?1 and a.status = 'D' and a.fileId is not null")
    List<ApplyConfirmReceiptDcsx> findByContractIdList(List<Long> contractIdList);
}
