package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContractSettlement;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


public interface CtrContractSettlementDao extends BaseDao<CtrContractSettlement> {
	@Query("from CtrContractSettlement where id in ?1")
	List<CtrContractSettlement> findBySettlementIds(List<Long> ids);

	CtrContractSettlement findBySellContractId(Long contractId);

	List<CtrContractSettlement> findBySellContractNo(String contractNo);

	@Query(value = "from CtrContractSettlement where settleStatus = '0' OR settleStatus is null")
	List<CtrContractSettlement> findCalculateCommissionList();

	@Transactional
	@Modifying
	@Query("update CtrContractSettlement set receiveFlg =?2 where sellContractId =?1")
	void updateSettlementReceiveFlg(Long contractId, Boolean receiveFlg);

	@Transactional
	@Modifying
	@Query("update CtrContractSettlement set confirmFlg =?2 where sellContractId =?1")
	void updateSettlementConfirmFlg(Long contractId, Boolean confirmFlg);

	@Transactional
	@Modifying
	@Query("update CtrContractSettlement set status = ?1, settlementDate = ?2 where id in ?3")
	void markSettlement(String status, Date settlementDate, List<Long> settlementIds);


	@Transactional
	@Modifying
	@Query("update CtrContractSettlement set settleStatus = ?1, settlementDate = ?2 where id in ?3")
	void updateSettleStatus(String settleStatus, Date settlementDate,  List<Long> settlementIds);

	@Transactional
	@Modifying
	@Query("update CtrContractSettlement set settleTotalFlg =?2  where id in ?1")
	void updateSettleTotalFlg(List<Long> settlementId, Boolean settleTotalFlg);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE extract(year_month from summary_date) = ?1 AND settle_status in ('2','3') AND enable_flg = true AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getSummaryList(String summaryDate);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND enable_flg = true AND mark_supplier_flag = false AND match_credit_flg = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getfinalAccountList(Date summaryDate);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND sell_match_user_id =?2 AND enable_flg = true AND mark_supplier_flag = false AND match_credit_flg = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getfinalAccountList(Date summaryDate, Long sellMatchUserId);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND enable_flg = true AND mark_supplier_flag = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getMarkFinalAccountList(Date summaryDate);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND sell_match_user_id =?2 AND enable_flg = true AND mark_supplier_flag = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getMarkFinalAccountList(Date summaryDate, Long sellMatchUserId);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND enable_flg = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE AND dept_id = 67957")
	List<CtrContractSettlement> getKhFinalAccountList(Date summaryDate);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND sell_match_user_id =?2 AND enable_flg = true AND virtual_flg = false AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE AND dept_id = 67957")
	List<CtrContractSettlement> getKhFinalAccountList(Date summaryDate, Long sellMatchUserId);

	@Query("select count(*) from CtrContractSettlement")
	Integer selectAllCount();

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND settle_status = '3' AND enable_flg = TRUE AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE")
	List<CtrContractSettlement> getCommissionList(Date commissionDate);

	@Query(nativeQuery = true, value = "SELECT * FROM t_ctr_contract_settlement WHERE DATE_FORMAT(summary_date, '%Y-%m') = DATE_FORMAT(?1, '%Y-%m') AND settle_status = '3' AND enable_flg = TRUE AND receive_flg = TRUE AND confirm_flg = TRUE AND bill_flg = TRUE AND (sell_match_user_id =?2 or buy_match_user_id =?2)")
	List<CtrContractSettlement> getCommissionList(Date commissionDate, Long sellMatchUserId);
}

