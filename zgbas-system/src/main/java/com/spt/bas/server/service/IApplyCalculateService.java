package com.spt.bas.server.service;

import java.util.List;

import com.spt.bas.client.entity.ApplyCalculate;
import com.spt.bas.client.vo.ApplyCalculateDetailVo;
import com.spt.bas.client.vo.ApplyCalculateFlowVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IApplyCalculateService extends IBaseService<ApplyCalculate> {
	
	public void saveDetail(List<ApplyCalculateDetailVo> detailList) throws ApplicationException;
	
	public void doCalculate(ApplyCalculateFlowVo flowVo) throws ApplicationException;
	
	ApplyCalculate findByImportId(ApplyCalculate calculate);
}

