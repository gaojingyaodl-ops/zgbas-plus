package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 合同模板表
 */
@Entity
@Table(name = "t_bs_contract_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsContractTemplate extends IdEntity {
	private static final long serialVersionUID = 5162964809546283424L;

	/**
	 * 合同类型
	 */
	private String contractType;

	/**
	 * 模板标识
	 */
	private String templateTag;

	/**
	 * 模板名称
	 */
	private String templateName;

	/**
	 * 模板内容
	 */
	private String content;

	/**
	 * 附件Id
	 */
	private String fileId;

	/**
	 * 企业帐套ID
	 */
	private Long enterpriseId;

	/**
	 * 是否有效
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Boolean enableFlg;

	/**
	 * 是否包含合同内容
	 */
	private Boolean withContentFlag = false;

	public Boolean getEnableFlg() {
		return enableFlg;
	}

	public void setEnableFlg(Boolean enableFlg) {
		this.enableFlg = enableFlg;
	}

	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getTemplateTag() {
		return templateTag;
	}
	public void setTemplateTag(String templateTag) {
		this.templateTag = templateTag;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public Long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	@Transient
	public Boolean getWithContentFlag() {
		return withContentFlag;
	}

	public void setWithContentFlag(Boolean withContentFlag) {
		this.withContentFlag = withContentFlag;
	}

	public BsContractTemplate() {
	}

	public BsContractTemplate(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public BsContractTemplate(String contractType, Long enterpriseId) {
		this.contractType = contractType;
		this.enterpriseId = enterpriseId;
	}

	public BsContractTemplate(Long enterpriseId, String templateTag) {
		this.enterpriseId = enterpriseId;
		this.templateTag = templateTag;
	}
}
