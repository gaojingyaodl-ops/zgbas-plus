package com.spt.bas.server.dao;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ApplyDcsxDao extends BaseDao<ApplyCtrDCSX> {

    @Query("from ApplyCtrDCSX a where a.approveId = ?1 and a.businessType ='ZY-BB'")
    ApplyCtrDCSX findByDCSXApproveId(Long approveId);

    @Query("from ApplyCtrDCSX a where a.approveId = ?1 and a.businessType =?2")
    ApplyCtrDCSX findByDCSXApproveIdAndBusinessType(Long approveId,String businessType);

    @Query("from ApplyCtrDCSX a where a.approveId = ?1")
    List<ApplyCtrDCSX> findByDCSXApproveIdAll(Long approveId);

    List<ApplyCtrDCSX> findByApproveId(Long approveId);

    ApplyCtrDCSX findByContractNo(String contractNo);

    @Modifying
    @Query("update ApplyCtrDCSX c set c.invoiceBillAmount=?2,c.contractStatus=?3,c.status=?4 where c.id=?1 ")
    void updateStatus(Long id, BigDecimal amount,String contractStatus,String status);

    @Modifying
    @Query("update ApplyCtrDCSX c set c.fileId =?2,c.totalAmount=?3,c.totalNumber=?4,c.dealPrice=?5,c.payFullTime=?6 where c.id=?1 ")
    void updateFileId(Long id, String fileId, BigDecimal newTotalAmount, BigDecimal newTotalNumber, BigDecimal newDealPrice, Date newPayFullTime);

    @Query(nativeQuery = true,value = "SELECT * FROM t_ctr_dcsx WHERE (dealed_amount + apply_pay_amount) < total_amount AND pay_full_time <= DATE_ADD(NOW(),INTERVAL 1 DAY) AND status != 'C'")
    List<ApplyCtrDCSX> findNeedPayList();

    @Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_dcsx c WHERE c.`status` != 'C' AND c.updated_date >= DATE_SUB(NOW(),INTERVAL 48 HOUR)")
    List<ApplyCtrDCSX> findChangeDCSXList();


    @Query(nativeQuery=true,value ="SELECT * FROM t_ctr_dcsx d   WHERE  ( d.dealed_amount + d.apply_pay_amount ) < d.total_amount AND DATE_FORMAT( d.created_date, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND DATE_FORMAT(d.pay_full_time, '%Y-%m-%d' ) <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND d.STATUS != 'C' ORDER BY d.pay_full_time DESC ")
    List<ApplyCtrDCSX> autoDcsxPayAmount();

    @Query(nativeQuery=true,value ="SELECT * FROM t_ctr_dcsx WHERE  ( dealed_amount + apply_pay_amount ) < bond_amount AND DATE_FORMAT( created_date, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_ADD( NOW(), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND DATE_FORMAT( pay_bond_time, '%Y-%m-%d' )  <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND STATUS != 'C' ORDER BY pay_bond_time DESC ")
    List<ApplyCtrDCSX> autoDcsxPayBondAmount();

    @Query(nativeQuery=true,value ="SELECT * FROM t_ctr_dcsx d LEFT JOIN t_ctr_contract_dcsx_apply a ON a.ctr_contract_id = d.id  WHERE  ( a.apply_receive_amount + d.receive_amount ) < d.total_amount AND DATE_FORMAT( d.created_date, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND d.STATUS != 'C' AND DATE_FORMAT(d.pay_full_time, '%Y-%m-%d' ) <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) ORDER BY d.created_date DESC")
    List<ApplyCtrDCSX> autoDcsxReceiveAmount();

    @Query(nativeQuery=true,value ="SELECT * FROM t_ctr_dcsx d LEFT JOIN t_ctr_contract_dcsx_apply a ON a.ctr_contract_id = d.id  WHERE  a.apply_receive_amount < d.bond_amount and ( a.apply_receive_amount + d.receive_amount ) < d.total_amount AND DATE_FORMAT( d.created_date, '%Y-%m-%d' ) >= DATE_FORMAT(DATE_ADD( NOW( ), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND d.STATUS != 'C' and DATE_FORMAT( d.pay_bond_time, '%Y-%m-%d' ) <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) ORDER BY d.created_date DESC")
    List<ApplyCtrDCSX> autoDcsxBondReceiveAmount();

    @Query(nativeQuery = true, value = "SELECT * from t_ctr_dcsx where status != 'C' and business_type ='ZY-BB' and seal_flg = true and dealed_amount > 0 and (interest_abort_flg is null or interest_abort_flg != TRUE)")
    List<ApplyCtrDCSX> findComputeList();

    @Query("from ApplyCtrDCSX where id in ?1")
    List<ApplyCtrDCSX> findByIds(List<Long> ids);

    @Modifying
    @Query("update ApplyCtrDCSX c set c.settlementStatus=?2 where c.id in ?1 ")
    void updateSettlementStatus(List<Long> ids, String status);


    @Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_dcsx c WHERE c.`status` != 'C' AND c.buy_pay_full_time < DATE_SUB(CURDATE(), INTERVAL 60 DAY) AND (company_name = ?1 OR our_company_name = ?1) AND IFNULL(dealed_amount, 0) < IFNULL(total_amount, 0)")
    List<ApplyCtrDCSX> findHb60DayNotApplyList(String companyName);
}
