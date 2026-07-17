package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyDeliveryIn;
import com.spt.bas.client.entity.CtrLogisticsFile;
import com.spt.bas.client.vo.ApplyDeliveryInVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IApplyDeliveryInService extends IBaseService<ApplyDeliveryIn> {

	void updateFileId(Long id, String fileId);

	List<ApplyDeliveryIn> findByContractId(Long contractId);

	void updateApplyStatus(Long contractId);

	List<ApplyDeliveryIn> findDeliveryInContractId(Long contractId);

	void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

	ApplyDeliveryIn generateApplyNo(Long contractId);

	void doSignLogistics();
}

