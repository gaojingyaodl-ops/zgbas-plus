package com.spt.bas.client.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 印章外借操作记录
 *
 */
@Entity
@Table(name = "t_seal_borrow_ophis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SealBorrowOphis extends IdEntity{

	private static final long serialVersionUID = 3823939981067137134L;
	private Long sealBorrowId;			//印章外借ID
	private String opType;				//操作类型
	private Long opUserId;				//操作人ID
	private String opUserName;			//操作人
	private Long enterpriseId;			//企业账套ID
	private String fileId;				//附件ID
	private String remark;				//备注
	private String itemType;			//物品类型
	public Long getSealBorrowId() {
		return sealBorrowId;
	}
	public void setSealBorrowId(Long sealBorrowId) {
		this.sealBorrowId = sealBorrowId;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getOpUserId() {
		return opUserId;
	}
	public void setOpUserId(Long opUserId) {
		this.opUserId = opUserId;
	}
	public String getOpUserName() {
		return opUserName;
	}
	public void setOpUserName(String opUserName) {
		this.opUserName = opUserName;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
