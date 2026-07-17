package com.spt.pm.service;

import java.util.List;

import com.spt.pm.entity.PmProcessAccess;
import com.spt.pm.vo.PmProcessAccessVo;
import com.spt.tools.core.exception.ApplicationException;
import com.spt.tools.jpa.service.IBaseService;

public interface IPmProcessAccessService extends IBaseService<PmProcessAccess> {
	
	List<PmProcessAccess> findByProcessId(Long processId);
	
	void saveChanges(List<PmProcessAccess> list)throws ApplicationException ;
	
	List<PmProcessAccess> findByUserId(Long userId);
	
	void saveByUser(PmProcessAccessVo vo);

	Boolean verifyUserProcessPermission(PmProcessAccessVo vo);
}

