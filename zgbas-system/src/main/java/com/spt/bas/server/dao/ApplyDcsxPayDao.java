package com.spt.bas.server.dao;


import com.spt.bas.client.entity.ApplyPay;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface ApplyDcsxPayDao extends BaseDao<ApplyPay> {

    @Modifying
    @Query("update ApplyPay c set c.fileId =?2 where c.id=?1 ")
    void updateFileId(Long id, String fileId);

    @Query("from ApplyPay c where c.contractId=?1 and c.status !='C'")
    List<ApplyPay> findByContractId(Long contractId);

    @Modifying
    @Query("update ApplyPay c set c.status ='C' where c.contractId=?1 ")
    void updateApplyStatus(Long contractId);

    List<ApplyPay> findByContractNo(String contractNo);


    @Query("from ApplyPay c where c.contractNo=?1  and c.status !='D'")
    List<ApplyPay> findApplyPay(String contractNo);


}

