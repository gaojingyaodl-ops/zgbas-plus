/**
 * 
 */
package com.spt.pm.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wlddh
 *
 */
public class PmPermissionVo {

	private Boolean hasEdit;// 是否有编辑权限
	private Boolean hasApprove;// 是否有审批权限
	private Boolean hasInvalid;// 是否有作废权限
	private Boolean canApproveEdit;// 是否可在审批中编辑
	private Boolean isFromContract = false;
	private Map<String, Options> mapEdit=new HashMap<>();// 审批中可编辑字段定义

	public static class Options {
		private boolean edit;// 是否可编辑
		private boolean require;// 是否必输

		public Options(boolean edit, boolean require) {
			this.edit = edit;
			this.require = require;
		}

		public boolean isEdit() {
			return edit;
		}

		public void setEdit(boolean edit) {
			this.edit = edit;
		}

		public boolean isRequire() {
			return require;
		}

		public void setRequire(boolean require) {
			this.require = require;
		}

	}

	

	@Override
	public String toString() {
		return "PmPermissionVo [hasEdit=" + hasEdit + ", hasApprove=" + hasApprove + ", canApproveEdit="
				+ canApproveEdit + ", mapEdit=" + mapEdit + "]";
	}
	public void addEdit(String fieldName,Options op) {
		mapEdit.put(fieldName, op);
	}
	public Map<String, Options> getMapEdit() {
		return mapEdit;
	}

	public void setMapEdit(Map<String, Options> mapEdit) {
		this.mapEdit = mapEdit;
	}
	public Boolean getHasEdit() {
		return hasEdit;
	}
	public void setHasEdit(Boolean hasEdit) {
		this.hasEdit = hasEdit;
	}
	public Boolean getHasApprove() {
		return hasApprove;
	}
	public void setHasApprove(Boolean hasApprove) {
		this.hasApprove = hasApprove;
	}
	public Boolean getCanApproveEdit() {
		return canApproveEdit;
	}
	public void setCanApproveEdit(Boolean canApproveEdit) {
		this.canApproveEdit = canApproveEdit;
	}
	public Boolean getHasInvalid() {
		return hasInvalid;
	}
	public void setHasInvalid(Boolean hasInvalid) {
		this.hasInvalid = hasInvalid;
	}
	public Boolean getIsFromContract() {
		return isFromContract;
	}
	public void setIsFromContract(Boolean isFromContract) {
		this.isFromContract = isFromContract;
	}
	
}
