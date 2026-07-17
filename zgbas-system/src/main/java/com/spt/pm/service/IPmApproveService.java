package com.spt.pm.service;

import com.spt.pm.entity.PmApprove;
import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.*;
import com.spt.tools.core.bean.RespVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPmApproveService extends IBaseService<PmApprove> {

	PmApprove doStepFlow(PmApproveStepFlowVo flowVo) throws ApplicationException;

	PmApprove startFlow(PmApproveSaveVo startVo) throws ApplicationException;

	PmApproveVo getApproveVo(Long approveId);

	Page<PmApproveDownVo> findPageApprove(PmApproveSearchVo queryVo);

	void doWithdraw(PmApproveWithdrawVo vo)  throws ApplicationException;

//	void forceComplete(PmApproveStepFlowVo flowVo) throws ApplicationException;

	/**申请单发起人在未审核情况下,自行撤回重新编辑*/
	void doRetrieve(PmApproveRetrieveVo vo) throws ApplicationException;

	PmApprove findByApproveNo(String approveNo) throws ApplicationException;

	List<PmApprove> findApproveByContractIdAndProcessId(Long contractId, Long processId) throws ApplicationException;

	List<PmApprove> findApproveByContractIdAndStatus(Long contractId, String status) throws ApplicationException;


    PmApprove findApproveNoByApproveId(Long approveId);

	void deleteRecord(Long approveId);

	String findNodeUserId(IPmEntity bizEntity, Long nodeId, Long createUserId) throws ApplicationException;

	void doAutoSign();

    RespVo<?> resServerPmApprove(String param);

	List<PmApprove> getAllBusinessApprove(Long approveId);
}

