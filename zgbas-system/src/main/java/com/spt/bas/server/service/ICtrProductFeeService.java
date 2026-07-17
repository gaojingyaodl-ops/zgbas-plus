package com.spt.bas.server.service;

import com.spt.bas.client.entity.ApplyDelivery;
import com.spt.bas.client.entity.CtrProductFee;
import com.spt.bas.client.vo.ApplyDeliveryReportVo;
import com.spt.bas.client.vo.CtrProductFeeVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface ICtrProductFeeService extends IBaseService<CtrProductFee> {
	
	public void saveCtrProductFee(ApplyDelivery applyDelivery) throws ApplicationException;
	
	public CtrProductFee findByDeliveryId(CtrProductFeeVo vo);

	public void saveProductFee(ApplyDeliveryReportVo delivery) throws ApplicationException;
	
	public void saveContractRealAmount(Long contractId) throws ApplicationException;
	
	public CtrProductFee getDefaultCtrProductFee(Long deliveryOutId);
}

