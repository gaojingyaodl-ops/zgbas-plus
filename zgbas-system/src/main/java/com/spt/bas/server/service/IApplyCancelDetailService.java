package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyCancelDetail;
import com.spt.bas.client.vo.ApplyCancelVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyCancelDetailService extends IBaseService<ApplyCancelDetail> {

	List<ApplyCancelDetail> saveDetailBatch(ApplyCancelVo cancelVo) throws ApplicationException;

	List<ApplyCancelDetail> findByApplyCancelId(Long id);
	
}

