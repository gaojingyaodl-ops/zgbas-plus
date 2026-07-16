package com.spt.bas.server.dao;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.vo.ContractPiccInsuranceVo;
import com.spt.tools.jpa.dao.BaseDao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CtrContractDao extends BaseDao<CtrContract> {
	@Transactional
	@Modifying
	@Query("update CtrContract c set c.fileId =?2 where c.id=?1 ")
	public void updateFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.agreementDealPrice =?2 where c.id=?1 ")
	public void updateAgreementDealPrice(Long id, BigDecimal agreementDealPrice);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.finalTotalAmount =?2 where c.id=?1 ")
	public void updatefinalTotalAmount(Long id, BigDecimal finalTotalAmount);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.debtCertificateFileId =?2 where c.id=?1 ")
	public void updateDebtCertificateFileId(Long id, String debtCertificateFileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.buyContentFileId =?2 where c.id=?1 ")
	public void updateBuyContentFileId(Long id, String fileId);


	@Transactional
	@Modifying
	@Query("update CtrContract c set c.sellContentFileId =?2 where c.id=?1 ")
	public void updateSellFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.buyContentFileId =?2 where c.id=?1 ")
	public void updateBuyFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.invoiceFileId =?2 where c.id=?1 ")
	public void updateInvoiceFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.warehouseFileId =?2 where c.id=?1 ")
	public void updateWarehouseFileId(Long id, String fileId);

	List<CtrContract> findByApproveId(Long approveId);
	@Query(" from CtrContract c where c.approveId=?1 and c.status = 'D' and c.specialChainFlag = false order by c.contractType desc ")
	public List<CtrContract> findApproveByOrder(Long approveId);

	@Modifying
	@Query("update CtrContract c set c.contractStatus='C',c.status='C' where c.id =?1 ")
	void updateContractStatusIsC(Long contractId);

	@Modifying
	@Query("update CtrContract c set c.contractStatus =?1 where c.id in ?2")
	void updateContractStatusByIds(String contractStatus, List<Long> contractIdList);

	@Modifying
	@Query("update CtrContract c set c.contractStatus =?1, c.contractStatusWx =?2 where c.id in ?3")
	void updateContractStatusAndWxStatusByIds(String contractStatus, String contractStatusWx, List<Long> contractIdList);

	@Query("select c.contractNo from CtrContract c where c.id=?1 ")
	public String findContractNoById(Long contractId);

	@Query("from CtrContract c,StockDetail d where c.id = d.buyContractId and d.id = ?1")
	public CtrContract findContractByStockDetailId(Long stockDetailId);

	@Query("from CtrContract c where c.id= ?1")
	public CtrContract findContractByLinkContractId(Long linkContractId);

	@Query("select id from CtrContract c where c.linkContractId<>NUll AND c.contractStatus= ?1 AND c.source= ?2")
	public List<Long> findStatementByContractAndSource(String contractStatus, String source);

	@Query("select id from CtrContract c where c.source= ?1 and c.payFullTime<>NULL and c.linkContractId<>NUll")
	public List<Long> findStatementBySource(String source);

	@Query("from CtrContract c where  c.payFullTime >= ?1 and c.payFullTime < ?2")
	List<CtrContract> findContractByPayFullTime(Date startTime, Date endTime);

	@Query("from CtrContract c where  c.linkContractId like %?1%")
	public List<CtrContract> findByLinkContractIdLink(String linkContractId);

	@Query("from CtrContract c where  c.id in ?1")
	public List<CtrContract> findByIdIn(Long[] arr);

	@Query("from CtrContract c where  c.id in ?1")
	public List<CtrContract> findByIds(List<Long> ids);

	@Query("select id from CtrContract c where c.linkContractId like %?1%")
	public List<Long> findIdByLinkContractId(String id);

	public CtrContract findByContractNo(String contractNo);

	List<CtrContract> findByCompanyId(Long companyId);

	@Query("select new com.spt.bas.client.vo.ContractPiccInsuranceVo(id,contractNo,insuranceRate,insuranceAmount,insuranceFlag,ourCompanyName,totalAmount,approveId) from CtrContract")
	List<ContractPiccInsuranceVo> findContractInsurance();

	@Transactional
	@Modifying
	@Query("update CtrContract set insuranceRate=?2, insuranceAmount=?3, insuranceFlag=?4 where id=?1")
	public void updateInsuranceInfo(Long id, BigDecimal insuranceRate, BigDecimal insuranceAmount, Boolean insuranceFlag);

	/*@Query("select DISTINCT c.companyId from CtrContract c where DateDiff(NOW(),c.contractTime)> ?1")
	public List<Long> findCompanyIntervalTime(Long num);*/

	@Transactional
	@Modifying
	@Query("update CtrContract set updatedDate=?1, transportAmount=?2, warehouseAmount=?3 where id=?4")
	public void saveCtrContract(Date updateDate, BigDecimal transportAmount, BigDecimal warehouseAmount, Long id);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.doubleCheckFileId =?2 where c.id=?1 ")
	public void updateDoubleCheckFileId(Long id, String fileId);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.contractStatus ='S' where c.id=?1 ")
	public void updateContractStatusSign(Long id);

	@Query("from CtrContract c where c.contractType = ?1 and c.contractStatus != 'C'")
	public List<CtrContract> findByContractType(String contractType);

	@Query("from CtrContract c where c.source = ?1 and c.enterpriseId = ?2")
	public List<CtrContract> findBySourceAndEnterprise(String applyTypeMs, Long enterpriseId);

	@Query("select sum(c.totalAmount-c.dealedAmount) from CtrContract c where c.companyId = ?1 and c.enterpriseId = ?2 and c.contractType = ?3 and c.dealedAmount < c.totalAmount and c.contractStatus != 'C'")
	public BigDecimal findDealedAmount(Long companyId, Long enterpriseId, String contractType);

	@Query("select sum(c.totalAmount-c.billedAmount) from CtrContract c where c.companyId = ?1 and c.enterpriseId = ?2 and c.contractType = ?3 and c.billedAmount < c.totalAmount and c.contractStatus != 'C'")
	public BigDecimal findBilledAmount(Long companyId, Long enterpriseId, String contractType);

	@Query("select count(c.id) from CtrContract c where c.companyId = ?1 and c.enterpriseId = ?2 and c.contractType = ?3 and c.dealedAmount < c.totalAmount and c.contractStatus != 'C'")
	public Integer existDealedOrder(Long companyId, Long enterpriseId, String contractType);

	@Query("select count(c.id) from CtrContract c where c.companyId = ?1 and c.enterpriseId = ?2 and c.contractType = ?3 and c.billedAmount < c.totalAmount and c.contractStatus != 'C'")
	public Integer existBilledOrder(Long companyId, Long enterpriseId, String contractType);

	@Query("from CtrContract where billedAmount < totalAmount and contractType = 'B' and contractStatus != 'C'")
	public List<CtrContract> findNoBilledContract();

	@Query("from CtrContract where approveId =?1 and contractType =?2 and specialChainFlag = false")
	CtrContract findByApproveIdAndContractType(Long approveId, String contractType);

	@Transactional
	@Modifying
	@Query("update CtrContract a set a.status=?2,a.contractStatus=?2 where a.approveId=?1 ")
	void updateStatus(Long approveId, String status);

	@Transactional
	@Modifying
	@Query("update ApplyCtrDCSX a set a.status=?2,a.contractStatus=?2 where a.approveId=?1 ")
	void updateDCSXStatus(Long approveId, String status);;

	/**
	 * 通过approveId查询合同id
	 */
	@Query("from CtrContract a where a.approveId = ?1 and a.specialChainFlag = false")
	List<CtrContract> findContractIdByApproveId(Long approveId);

	@Query("from CtrContract a where a.approveId = ?1 and a.specialChainFlag = true")
	CtrContract findSpecialChainByApproveId(Long approveId);

	@Query("from CtrContract a where a.approveId = ?1")
	List<CtrContract> findAllContractByApproveId(Long approveId);
	
	@Query("from CtrContract a where a.approveId in ?1 and a.specialChainFlag = false")
	List<CtrContract> findContractIdByApproveIds(List<Long> approveIds);

	@Query("from CtrContract c where c.contractType = ?1 and c.contractStatus != 'C' and c.businessTypeDcsx = 'DCSX'")
	public List<CtrContract> findByContractTypeDCSX(String contractType);


	@Query("from CtrContract c where c.contractType = ?1 and c.contractStatus != 'C' and c.businessTypeDcsx = 'DCSXBL' or c.businessTypeDcsx = 'BL'")
	public List<CtrContract> findByContractTypeDCSXBl(String contractType);


    /**
     * 违约合同
     */
    @Query("from CtrContract where breachDays>15 and contractType='S' and contractStatus !='D' and contractStatus !='C'")
    List<CtrContract> findCtrContractBreach();

	/**
	 * 更新合同表中的原始金额 抵扣金额 合同实际金额
	 * @param contractNo
	 * @param actualContractAmount
	 * @param deductibleAmount
	 * @param originalContractAmount
	 */
	@Transactional
	@Modifying
	@Query("update CtrContract a set a.actualContractAmount = ?2,a.deductibleAmount = ?3,a.originalContractAmount = ?4 where a.contractNo = ?1")
	void updateContractData(String contractNo,  BigDecimal actualContractAmount, BigDecimal  deductibleAmount,  BigDecimal originalContractAmount);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.goodsFileId =?2 where c.id=?1 ")
	void updateGoodsFileId(Long id, String fileId);


	@Transactional
	@Modifying
	@Query("update CtrContract a set a.factorStatus=?1 where a.contractNo=?2 ")
	void updateStatusByContractNo(String factorStatus, String contractNo);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.violateTreatyFlg=1 where c.id =?1 ")
	void violateFlgUpdate(Long id);

	@Query("from CtrContract a where a.customerOrderCode = ?1 and a.contractType='S'")
	CtrContract findByCustomerOrderCode(String customerOrderCode);


	@Transactional
	@Modifying
	@Query("update CtrContract c set c.deliveryStaus =?2 where c.id=?1 ")
	void updateDeliveryStaus(Long id, String staus);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.performanceStatus =?2 where c.id=?1 ")
	void updatePerformanceStaus(Long id, String status);

	@Transactional
	@Modifying
	@Query("update CtrContract c set c.performanceStatus =?1 where c.id in ?2 ")
	void updatePerformanceStatusByIds(String status, List<Long> contractIds);

	@Query("from CtrContract c where c.status <> 'C' and c.companyId =?1 and c.contractType = 'B' and c.warehouseNumber = 0 and c.matchCreditFlg = true and c.dealedAmount >= c.totalAmount order by c.id desc")
	List<CtrContract> findUnDelivery(Long companyId);

	@Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_contract c WHERE c.enterprise_id =?1 AND c.`status` <> 'C' AND c.contract_type = 'B' AND c.warehouse_number < c.total_number AND c.delivery_date_to < CURDATE() AND c.delivery_date_to >= DATE_SUB(CURDATE(),INTERVAL 1 DAY)")
	List<CtrContract> findUnDelieryNotify(Long enterpriseId);

	// 查询某一个公司的罚息合同
	@Query("from CtrContract c where c.companyId = ?1 and (c.breachAmount - c.receiveBreachAmount)>0 and c.contractType = 'S' and c.contractNo not in (?2) and c.matchUserId = ?3 order by c.contractTime desc")
	List<CtrContract> findByCompanyInterest(Long companyId,List<String> contractNoList,Long userId);

	// 修改合同违约标识和已收逾期罚息
	// orverdurFlg = false
	// contractStatusWx = O  O=已完成
	@Transactional
	@Modifying
	@Query("update CtrContract c set c.violateTreatyFlg = false,c.orverdurFlg = false ,c.contractStatusWx = 'O',c.receiveBreachAmount =?2 where c.contractNo=?1 ")
	void updateReceiveBreachAmount(String contractNo, BigDecimal receiveBreachAmount);

	// 收款审批作废 修改合同违约标识 已收逾期罚息 已收罚息金额  L= 逾期
	@Transactional
	@Modifying
	@Query("update CtrContract c set c.violateTreatyFlg = true ,c.orverdurFlg = true ,c.contractStatusWx = 'L',c.receiveBreachAmount = 0  where c.contractNo in ?1 ")
	void rollbackReceiveInterestAmount(List<String> contractNoList);

	// 修改合同历史罚息总额
	@Transactional
	@Modifying
	@Query("update CtrContract c set c.sellInterestAmount =?2  where c.id =?1 ")
	void updateSellInterestAmount(Long contractId,BigDecimal sellInterestAmount);

	@Transactional
	@Modifying
	@Query("update CtrContract a set a.status=?2,a.contractStatus=?2 where a.id=?1 ")
	void updateByContractIdStatus(Long id, String status);

	@Query("from CtrContract c where c.approveId =?1 and c.id <>?2 and c.specialChainFlag = false")
	CtrContract findOtherContract(Long approveId, Long contractId);

	@Query("select count(*) from CtrContract")
	Integer selectAllCount();


	@Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_contract c WHERE c.`status` != 'C' AND c.updated_date >= DATE_SUB(NOW(),INTERVAL 48 HOUR)")
	List<CtrContract> findChangeContractList();

	@Query("select count(c.id) from CtrContract  c where c.companyId = ?1 and c.status != 'C' and c.contractStatus != 'C'")
    Long countByCompanyId(Long companyId);


	@Query(nativeQuery=true,value = "SELECT *   FROM t_ctr_contract c LEFT JOIN t_ctr_contract_apply a ON c.id = a.ctr_contract_id WHERE c.contract_type = 'B' AND a.apply_pay_amount < c.total_amount AND DATE_FORMAT( c.created_date, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND DATE_FORMAT( c.appoint_pay_full_time, '%Y-%m-%d' ) <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND c.STATUS != 'C' AND c.business_type = 'ZY-BB' ORDER BY c.appoint_pay_full_time DESC")
	List<CtrContract> autoPayAmount();

	@Query(nativeQuery=true,value = "SELECT * FROM t_ctr_contract c LEFT JOIN t_ctr_contract_apply a ON c.id = a.ctr_contract_id WHERE c.contract_type = 'B' AND a.apply_pay_amount < c.total_amount AND DATE_FORMAT( c.created_date, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL - 2 MONTH ), '%Y-%m-%d' ) AND DATE_FORMAT( c.pay_bond_time, '%Y-%m-%d' ) <= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) AND c.STATUS != 'C' AND c.business_type = 'ZY-BB' ORDER BY c.pay_bond_time DESC ")
	List<CtrContract> autoPayBondAmount();

	@Query(nativeQuery=true,value = "SELECT * FROM  t_ctr_contract c WHERE  DATE_FORMAT( c.appoint_pay_full_time,'%Y-%m-%d') = DATE_FORMAT(NOW( ),'%Y-%m-%d') AND c.settlement_type IS NOT NULL AND c.STATUS!='C'")
	List<CtrContract> findReceiveToday();


	@Query("select c from CtrContract c where c.createdDate >= ?1 and c.createdDate <= ?2 and c.status != 'C' and c.contractStatus != 'C' and c.contractType = 'S' and c.sealFlg = true and c.businessType = 'ZY-BB'")
	List<CtrContract> findCompanyOrder(Date startTime, Date endTime);

	@Query("from CtrContract where contractNo like %?1%")
	List<CtrContract> findByContractNoLikes(String contractNo);

	@Query("from CtrContract where approveId in ?1 and contractType = ?2 and specialChainFlag = false")
	List<CtrContract> findByApproveIdInAndContractType(List<Long> approveId, String contractType);

	/**
	 * N-进行中
	 * 履约状态-进行中
	 * 未到付全款日期
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE t_ctr_contract c SET c.performance_status = 'N' WHERE c.match_credit_flg = TRUE AND c.contract_status != 'C' AND (c.performance_status != 'P' OR c.performance_status IS NULL) AND c.contract_type = 'S' AND c.seal_flg = TRUE AND c.confirm_receive_number <= 0 AND c.dealed_amount < c.total_amount")
	void updatePerformanceStatusN();

	/**
	 * B-宽限期
	 * 履约状态-宽限期
	 * 过付全款日期10天以内，[0,10]
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE t_ctr_contract c SET c.performance_status = 'B' WHERE c.match_credit_flg = TRUE AND c.contract_status != 'C' AND (c.performance_status != 'P' OR c.performance_status IS NULL) AND c.contract_type = 'S' AND c.confirm_receive_number > 0 AND c.breach_days >= 0 AND c.breach_days < 10 AND c.dealed_amount < c.total_amount AND c.appoint_pay_full_time <= now()")
	void updatePerformanceStatusB();

	/**
	 * D-催告期
	 * 履约状态-催告期
	 * 过付全款日期10-15天，(10, 15]
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE t_ctr_contract c SET c.performance_status = 'D' WHERE c.match_credit_flg = TRUE AND c.contract_status != 'C' AND (c.performance_status != 'P' OR c.performance_status IS NULL) AND c.contract_type = 'S' AND c.confirm_receive_number > 0 AND c.breach_days >= 10 AND c.breach_days < 15 AND c.dealed_amount < c.total_amount")
	void updatePerformanceStatusD();

	/**
	 * S-逾期
	 * 履约状态-逾期
	 * 过付全款日期15天以上，(15, -]
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE t_ctr_contract c SET c.performance_status = 'S' WHERE c.match_credit_flg = TRUE AND c.contract_status != 'C' AND (c.performance_status != 'P' OR c.performance_status IS NULL) AND c.contract_type = 'S' AND c.confirm_receive_number > 0 AND c.breach_days >= 15 AND c.dealed_amount < c.total_amount")
	void updatePerformanceStatusS();

	/**
	 * A-已完成
	 * 履约状态-完成
	 * 已全部回款
	 */
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE t_ctr_contract c SET c.performance_status = 'A' WHERE c.match_credit_flg = TRUE AND c.contract_status != 'C' AND (c.performance_status != 'P' OR c.performance_status IS NULL) AND c.contract_type = 'S' AND c.dealed_amount >= c.total_amount")
	void updatePerformanceStatusA();


	@Transactional
	@Modifying
	@Query("update CtrContract c set c.deliveryType =?2  where c.id =?1 ")
	void updateDeliveryType(Long contractId,String deliveryType);

	@Query(nativeQuery = true, value = "select * from t_ctr_contract c where c.business_type = 'ZY-BB' and c.contract_type = 'S' and c.status != 'C' and c.contract_status != 'C' and c.seal_flg = true and ((c.our_company_name =?1 and c.company_name =?2) or c.id = ?3) and (c.total_amount + c.breach_amount) > (c.dealed_amount + c.receive_breach_amount) order by case when id =?3 then 0 else 1 end, c.id desc")
	List<CtrContract> findDraftContractList(String ourCompanyName, String companyName, Long contractId);

	@Query(nativeQuery = true, value = "select * from t_ctr_contract c where c.business_type = 'ZY-BB' and c.contract_type = 'S' and c.status != 'C' and c.contract_status != 'C' and c.seal_flg = true and ((c.our_company_name =?1 and c.company_name =?2) or c.id = ?3) and c.discount_charge_amount > c.discount_receive_amount and c.discount_charge_target = 'K' order by case when id =?3 then 0 else 1 end, c.id desc")
	List<CtrContract> findDiscountContractList(String ourCompanyName, String companyName, Long contractId);

	@Query(nativeQuery = true, value = "select * from t_ctr_contract c where c.business_type = 'ZY-TP' and c.contract_type = 'S' and c.status != 'C' and c.contract_status != 'C' and c.seal_flg = true and ((c.our_company_name =?1 and c.company_name =?2) or c.id = ?3) and (c.total_number) > (c.warehouse_number) order by case when id =?3 then 0 else 1 end, c.id desc")
	List<CtrContract> findTpDraftContractList(String ourCompanyName, String companyName, Long contractId);

	@Query(nativeQuery = true, value = "select * from t_ctr_contract c where c.business_type = 'ZY-TP' and c.contract_type = 'S' and c.status != 'C' and c.contract_status != 'C' and c.seal_flg = true and ((c.our_company_name =?1 and c.company_name =?2) or c.id = ?3) and c.discount_charge_amount > c.discount_receive_amount and c.discount_charge_target = 'K' order by case when id =?3 then 0 else 1 end, c.id desc")
	List<CtrContract> findTpDiscountContractList(String ourCompanyName, String companyName, Long contractId);

	@Query("from CtrContract c where c.companyId = ?1 and c.status != 'C' and c.contractStatus != 'C' and c.contractType = 'S' and c.businessType = 'ZY-BB' and	c.matchCreditFlg = true and c.sealFlg = true and c.dealedAmount < c.totalAmount and c.appointPayFullTime <= NOW() order by c.contractTime desc")
	List<CtrContract> findOverdueContractListByCompanyId(Long companyId);

	List<CtrContract> findCtrContractByVirtualId(Long virtualId);

	@Query("from CtrContract c where c.virtualContractId = ?1 and c.status != 'C' and c.contractType = 'B' order by c.contractTime asc")
	List<CtrContract> findCtrContractByVirtualContractId(Long virtualContractId);

	@Query("select c.approveId from CtrContract c where c.approveId in ?1 and c.contractType = 'B' and c.dealedAmount < c.totalAmount and c.specialChainFlag = false and c.ourCompanyName != '范伦克供应链管理（上海）有限公司'")
	List<Long> findPaidApproveId(List<Long> approveIds);

	@Transactional
	@Modifying
	@Query("update CtrContract a set a.piccPushFlg=?2 where a.id=?1 ")
	void updatePiccPushFlg(Long contractId, Boolean piccPushFlg);

	@Transactional
	@Modifying
	@Query("update CtrContract a set a.piccDeclareStatus=?2 where a.id=?1 ")
	void updatePiccDeclareStatus(Long contractId, String piccDeclareStatus);

	@Transactional
	@Modifying
	@Query("update CtrContract a set a.piccReceiveFlg=?2 where a.id=?1 ")
	void updatePiccReceiveFlg(Long contractId, Boolean piccReceiveFlg);

	@Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_contract c WHERE c.match_credit_flg = TRUE AND c.`status` != 'C' AND c.contract_type = 'S' AND c.total_amount > c.dealed_amount AND c.company_credit_id IS NULL AND c.contract_model NOT LIKE '%HDFK%'")
	List<CtrContract> findHisCreditContract();

	@Query(nativeQuery = true, value = "SELECT c.* FROM t_ctr_contract c WHERE c.match_credit_flg = TRUE AND c.`status` != 'C' AND c.contract_type = 'S' AND c.total_amount > c.dealed_amount AND c.company_credit_id IS NOT NULL")
	List<CtrContract> findHisCreditUserAmount();

	@Query(nativeQuery = true, value = "SELECT GROUP_CONCAT(c.contract_no) FROM t_ctr_contract c WHERE c.`status` != 'C' AND c.contract_status != 'C' AND c.contract_type = 'S' AND c.business_type = 'ZY-BB' AND match_credit_flg = TRUE AND c.seal_flg = TRUE AND c.company_id =?1 AND c.warehouse_number <= 0 AND DATE_ADD(c.delivery_date_to,INTERVAL 3 DAY) <= NOW()")
	String findUnDelivery3Day(Long companyId);

	@Query(nativeQuery = true, value = "SELECT GROUP_CONCAT(c.contract_no) FROM t_ctr_contract c WHERE c.`status` != 'C' AND c.contract_status != 'C' AND c.contract_type = 'S' AND c.business_type = 'ZY-BB' AND match_credit_flg = TRUE AND c.seal_flg = TRUE AND c.company_id =?1 AND c.dealed_amount < c.total_amount AND c.appoint_pay_full_time <= NOW()")
	String findUnReceive(Long companyId);

	@Query(nativeQuery = true, value = "SELECT c.id FROM t_ctr_contract c  WHERE c.credit_type = '1' AND c.`status` != 'C' AND c.contract_type = 'S' AND c.latest_bill_date IS NOT NULL AND DATE(c.latest_bill_date) = DATE_ADD(CURDATE(), INTERVAL 14 DAY) AND c.total_amount > c.billed_amount")
	List<Long> findDaDiUnBillList();

	@Query("from CtrContract c where c.shippingDate is not null")
	public List<CtrContract> findByShippingDateNotNUll();

	@Query("from CtrContract c where c.companyName = ?1 and c.ourCompanyName = ?2 and c.sealDate between ?3 and ?4 and c.status != 'C' and c.contractStatus != 'C' and c.sealFlg = true and c.contractType = 'S' order by c.sealDate desc")
	List<CtrContract> findByCompanyNameAndOurCompanyNameAndSealDate(String companyName, String ourCompanyName, Date sealDateBegin, Date sealDateEnd);

}

