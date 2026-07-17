package com.spt.bas.server.ctr.service;

import com.spt.bas.client.entity.ApplyMatchDetail;
import com.spt.bas.client.entity.ApplyProductDetail;
import com.spt.bas.client.entity.CtrContract;
import com.spt.pm.entity.PmApprove;
import com.spt.tools.core.exception.ApplicationException;

import java.util.List;

public interface ICtrContractSaveService {

	CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve, List<Long> lstBuyId) throws ApplicationException;
	CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve, List<Long> lstBuyId, ApplyMatchDetail matchDetail) throws ApplicationException;

	CtrContract saveContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve) throws ApplicationException;

	CtrContract saveInterContract(CtrContract entity, List<ApplyProductDetail> list, PmApprove approve, CtrContract oContract) throws ApplicationException;

	void refreshContractText(String idStr) throws ApplicationException;

	void deleteContract(Long contractId) throws ApplicationException;

	void refreshRela(String buyContractNo);

	/**账期条款赊销合同,更新合同付全款时间*/
	void refrshPayFullTime(Long contractId);

}
