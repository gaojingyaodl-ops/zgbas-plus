/**
 * 
 */
package com.spt.bas.client.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.spt.tools.jpa.vo.IdEntity;

/**
 * 模板配置表
 * @author wlddh
 *
 */
@Entity
@Table(name = "t_bs_template_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsTemplateConfig extends IdEntity {

	private static final long serialVersionUID = 5162964809546283424L;
	private String templateCat; // 模板类别
	private String templateTag; // 模板标识
	private String templateName; // 模板名称
	private String lang; // 模板语言区分
	private String content; // 模板内容
	private String templateid;
	private Long enterpriseId;

	public String getTemplateCat() {
		return templateCat;
	}

	public void setTemplateCat(String templateCat) {
		this.templateCat = templateCat;
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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(nullable = true)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
}
