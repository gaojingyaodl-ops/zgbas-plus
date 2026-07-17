package com.spt.bas.server.service;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrContractOphis;
import com.spt.bas.client.entity.SealUsage;
import com.spt.bas.client.vo.BusinessDeliveryExcelVo;
import com.spt.bas.client.vo.CtrContractOphisRequest;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.jpa.service.IBaseService;

import java.util.Date;
import java.util.List;

public interface ICtrContractOphisService extends IBaseService<CtrContractOphis> {

	CtrContractOphis findByCtrContractId(Long contractId);
	CtrContractOphis findByApproveIdAndCtrContractId(Long approveId, Long contractId);

	/**
	 * @param applyType
	 * @param ctrContractId
	 * @param isCancel 是否作废
	 * @param approve
	 */
	void addHis(CtrContractOphisRequest request);

	void addHis(String applyType, Long ctrContractId, PmApprove approve, Date happenDate);

	void addHisDcsx(String applyType,String contractStatus, Long ctrContractId, PmApprove approve,Date happenDate);

	void addHisDcTp(String applyType,String contractStatus, Long ctrContractId, PmApprove approve,Date happenDate);

	void addHis(CtrContract contract, PmApprove approve, List<Long> lstBuyId);

//	void updateContractStatusByContractId(Long ctrContractId, String contractStatus);

	BusinessDeliveryExcelVo getBusinessDelivery(Long approveId);
	// 添加合同盖章追回记录
	void addRollBackHis(CtrContract contract, PmApprove approve);

	// 添加合同盖章发起记录
	void addSealStartHis(PmApprove approve);

	// 添加驳回记录
	void addRejectHis(CtrContract contract, PmApprove approve);
}
