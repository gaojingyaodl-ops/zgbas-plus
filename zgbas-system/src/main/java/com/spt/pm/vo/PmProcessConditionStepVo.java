/**
 * 
 */
package com.spt.pm.vo;

import com.spt.pm.entity.PmProcessStep;
import com.spt.pm.inter.IPmEntity;

import java.util.List;
import java.util.Map;

/**
 * @author wlddh
 *
 */
public class PmProcessConditionStepVo {

	private Long conditionId;
	private Long processId;
	private IPmEntity pmEntity;
	private Map<String, Object> mapDefault;
	private List<PmProcessStep> steps;
	private Boolean specialAutoSignFlg = true;

	public Long getConditionId() {
		return conditionId;
	}

	public void setConditionId(Long conditionId) {
		this.conditionId = conditionId;
	}

	public List<PmProcessStep> getSteps() {
		return steps;
	}

	public void setSteps(List<PmProcessStep> steps) {
		this.steps = steps;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public IPmEntity getPmEntity() {
		return pmEntity;
	}

	public void setPmEntity(IPmEntity pmEntity) {
		this.pmEntity = pmEntity;
	}

	public Map<String, Object> getMapDefault() {
		return mapDefault;
	}

	public void setMapDefault(Map<String, Object> mapDefault) {
		this.mapDefault = mapDefault;
	}

	public Boolean getSpecialAutoSignFlg() {
		return specialAutoSignFlg;
	}

	public void setSpecialAutoSignFlg(Boolean specialAutoSignFlg) {
		this.specialAutoSignFlg = specialAutoSignFlg;
	}
}
