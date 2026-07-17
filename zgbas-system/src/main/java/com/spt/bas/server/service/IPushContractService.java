package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.CtrContract;
import com.spt.bas.client.entity.CtrProduct;
import com.spt.bas.client.entity.PushContract;
import com.spt.bas.client.vo.ContractStatusResponseVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IPushContractService extends IBaseService<PushContract> {

	void addContractPushTasks(CtrContract contract, List<CtrProduct> ctrProductList) throws ApplicationException;
	
	void addContractStatusPushTasks(ContractStatusResponseVo respVo) throws ApplicationException;
	
	void doContractTask(PushContract pushContract);
	
	void removeContractTasks(String contractNo);
	
	Boolean canInvalidContract(String contractNo);
	
	void addSettlementTasks(String contractNo) throws Exception;
	
	void addSettlementStatusTasks(String contractNo) throws Exception;
}

