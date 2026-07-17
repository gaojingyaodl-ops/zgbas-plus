/**
 * 
 */
package com.spt.pm.service;

import java.util.Map;

import com.spt.pm.inter.IPmEntity;
import com.spt.pm.vo.PmProcessConditionStepVo;
import com.spt.tools.core.exception.ApplicationException;

/**
 * @author wlddh
 *
 */
public interface IPmParseService {

	PmProcessConditionStepVo getProcessStep(String processCode, IPmEntity entity, Long enterpriseId, Map<String, Object> mapDefault) throws ApplicationException;

}
