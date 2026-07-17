package com.spt.bas.server.ctr.service;

import com.spt.bas.client.entity.*;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.bas.client.vo.CtrContractSignRequest;
import com.spt.bas.client.vo.CtrContractUpdateVo;
import com.spt.bas.client.vo.protocol.SupplementaryAgreement;
import com.spt.tools.core.exception.ApplicationException;

import java.math.BigDecimal;
import java.util.Date;

public interface ICtrContractUpdateService {

	void updateConfirmReceiveNumber(CtrContractOphisRequest request) throws ApplicationException;

	void addWarehouseNumber(Long contractId, BigDecimal dealAmount, String approveNo, Date warehouseDate) throws ApplicationException;

	void addConfirmReceiptNumber(Long contractId, BigDecimal dealAmount,Date confirmReceiptDate, String approveNo) throws ApplicationException;

	void addBilledAmount(Long contractId, BigDecimal dealAmount, Date inInvoiceDate, String approveNo) throws ApplicationException;

	void addDealedAmount(Long contractId, BigDecimal dealAmount, String approveNo, String payType, Date receiveDate) throws ApplicationException;

	void addReceiveAmount(ApplyReceive applyReceive, String approveNo, Boolean withdrawFlg) throws ApplicationException;

	void addrefundAmount(Long contractId, BigDecimal dealAmount, String approveNo, String refundType) throws ApplicationException;

	void addServiceAmount(Long contractId, BigDecimal dealAmount, String approveNo);

	void updateSellContractInterest(CtrContract sellContract, BudgetSettlement settlement) throws ApplicationException;

	void updateWarehouseFileId(Long id, String fileId);

	void updateDoubleCheckFileId(Long id, String fileId);

	void updateInvoiceFileId(Long id, String fileId);

	void updateFileId(Long id, String fileId);

	void updateDebtCertificateFileId(Long id, String debtCertificateFileId);

	void updateCtrFileId(Long id, String fileId);

	void refreshProdutsName();
	/**合同签约*/
	void doSigning(CtrContractSignRequest req) throws ApplicationException;

	CtrContract setContractStatus(CtrContract contract);

	/**修改合同预估运费仓储费罚息,并生成合同操作记录*/
	void updateContractAmount(CtrContractUpdateVo updateVo);

	/** 刷新代采合同排序号pairCode */
	void makePairCodeForMatch(Long enterpriseId);

	/**
	 * 更新合同-出/入库费用
	 * @param deliveryOptionFee
	 * @param contractId
	 */
	void updateContractDeliveryFee(BigDecimal deliveryOptionFee, Long contractId);

	/**
	 * 更新收货确认附件Id
	 * @param vo
	 */
	void updateGoodsFileId(Long id, String fileId);
	/**
	 * 更新违约标识
	 * @param id
	 */
	void violateFlgUpdate(Long id);

	/**
	 * 更新合同保理状态
	 */
	CtrContract refreshFactorStatus(Long contractId);

	void updateDeliveryStaus(Long id, String staus);
	// 更新履约状态
	void updatePerformanceStatus(Long id, String status);

	void updateDeliveryAmount(Long contractId, BigDecimal warehouseAmount, BigDecimal transportAmount, BigDecimal deliveryFee);

	// 清除罚金
    void clearPenalty(Long id);

	/**
	 * 更新小程序合同状态
	 *
	 * @param contract
	 */
	void refreshContractStatusWx(CtrContract contract);

	/**
	 * 更新合同状态
	 * @param contract
	 */
	void refreshContractStatus(CtrContract contract);

	void refreshVirtualContract(CtrContract targetContract);

	void refreshContractWithLossNumber(CtrContract sellContract, BigDecimal lossNumber, String lossType);

	Date refreshAppointPayFullTimeWithReceipt(CtrContract sellContract, ApplyConfirmReceipt entity) throws ApplicationException;

	void refreshAppointPayFullTimeWithInvoice(CtrContract sellContract, ApplyInvoice invoice) throws ApplicationException;

	void refreshContractWithProtocolDocument(SupplementaryAgreement agreement, String protocolFileId);
}