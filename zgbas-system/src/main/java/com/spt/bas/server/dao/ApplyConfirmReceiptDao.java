package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyConfirmReceipt;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ApplyConfirmReceiptDao extends BaseDao<ApplyConfirmReceipt> {
    List<ApplyConfirmReceipt> findByContractNo(String contractNo);

    @Query("from ApplyConfirmReceipt a where a.contractId = ?1 and a.status != 'C' order by id desc")
    List<ApplyConfirmReceipt> findByContractId(Long contractId);

    ApplyConfirmReceipt findByApproveId(Long approveId);

    @Modifying
    @Query("update ApplyConfirmReceipt c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query("from ApplyConfirmReceipt a where a.contractId in ?1 and a.status = 'D' and a.fileId is not null")
    List<ApplyConfirmReceipt> findByContractIdList(List<Long> contractIdList);

    @Query("select c.contractId from ApplyConfirmReceipt c where c.status = 'D' and c.updatedDate >= ?1 and c.updatedDate < ?2")
    List<Long> findContractIdByDate(Date beginDate, Date endDate);

    @Query("select c.contractId from ApplyConfirmReceipt c where c.status = 'D' and c.updatedDate >= ?1")
    List<Long> findContractIdByBeginDate(Date beginDate);

    @Query("select c.contractId from ApplyConfirmReceipt c where c.status = 'D' and c.updatedDate < ?1")
    List<Long> findContractIdByEndDate(Date endDate);

    @Query(nativeQuery = true, value = "SELECT r.* FROM t_apply_confirm_receipt r LEFT JOIN t_ctr_logistics_file f ON r.logistics_file_id = f.id WHERE r.`status` = 'D' AND f.sign_flg = FALSE AND r.created_date >= '2024-04-28'")
    List<ApplyConfirmReceipt> findUnSignLogistics();

    @Query("select max(c.confirmReceiptDate) from ApplyConfirmReceipt c where c.contractId = ?1 and c.status = 'D'")
    Date findMaxConfirmDate(Long contractId);
}
