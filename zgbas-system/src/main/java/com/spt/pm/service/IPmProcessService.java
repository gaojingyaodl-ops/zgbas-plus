package com.spt.pm.service;

import com.spt.pm.entity.PmProcess;
import com.spt.pm.vo.PmProcessSearchVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

import java.util.List;

public interface IPmProcessService extends IBaseService<PmProcess> {

	List<PmProcess> findAccess(PmProcessSearchVo searchVo);
	
	List<PmProcess> findByEnterpriseId(PmProcessSearchVo searchVo);
	
	List<PmProcess> findByEnterpriseIdAndEnableFlgTrue(Long enterpriseId);

	PmProcess findByProcessCode(PmProcessSearchVo searchVo);
	
	void initProcess(Long enterpriseId);
	
	public Long findStartUserByProcess(PmProcess process) throws ApplicationException;

	public String initPmProcessList();
}

