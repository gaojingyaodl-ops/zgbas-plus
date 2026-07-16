/**
 * 
 */
package com.spt.pm.inter;

import com.spt.pm.entity.PmProcess;
import com.spt.tools.core.exception.ApplicationException;

import java.util.Map;

/**
 * @author wlddh
 *
 */
public interface IPmService {

	IPmEntity saveEntity(IPmEntity pmEntity) throws ApplicationException;

	IPmEntity getEntity(Long entityId);

	void delete(Long entityId) throws ApplicationException;

	/** 标题 */
	String getSubject(IPmEntity pmEntity, PmProcess pmProcess);

	/** 获取业务员id */
	default Long getMatchUserId(IPmEntity pmEntity) {
		return null;
	};

	default Map<String, Object> buildConditionDefaultMap(IPmEntity pmEntity){return null;}
}
