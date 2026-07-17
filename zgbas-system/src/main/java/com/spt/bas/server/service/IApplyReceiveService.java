package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyCtrDCSX;
import com.spt.bas.client.entity.ApplyReceive;
import com.spt.bas.client.vo.ApplyReceiveAmountSumVo;
import com.spt.pm.vo.PmApproveWithdrawVo;
import com.spt.tools.core.bean.PageSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IApplyReceiveService extends IBaseService<ApplyReceive> {
	void updateFileId(Long id, String fileId);

	List<ApplyReceive> findByContractId(Long contractId);

	List<ApplyReceive> findListByContractIdAndStatus(Long contractId, String status);

	ApplyReceiveAmountSumVo findReceiveAmountSum(Long contractId);
	
	ApplyReceiveAmountSumVo findReceiveAmountSumByContractNo(String contractNo);

	void updateApplyStatus(Long contractId);

	ApplyReceive findPageSum(PageSearchVo searchVo);

	void doWithdraw(PmApproveWithdrawVo pwVo) throws ApplicationException;

//	List<ApplyReceiveDetail> findByApplyReceiveId(Long applyReceiveId);

	void autoApplyDcsxPay(ApplyCtrDCSX applyCtrDCSX);
	void autoApplyDcsxPayScheduled();
}

