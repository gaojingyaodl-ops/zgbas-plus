package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 代采中间链条电子合同表
 */
@Entity
@Table(name = "t_ctr_contract_chain_text")
public class CtrContractChainText extends IdEntity{

    private static final long serialVersionUID = -3922954498423312769L;

    private Long enterpriseId; //企业账套Id
    private Long ctrContractId;//合同id
    private String contractType;//B-采购  S-销售  F-服务
    private String content;//内容
    private Long templateId;//模板ID
    public Long getEnterpriseId() {
        return enterpriseId;
    }
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
    public Long getCtrContractId() {
        return ctrContractId;
    }
    public void setCtrContractId(Long ctrContractId) {
        this.ctrContractId = ctrContractId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Long getTemplateId() {
        return templateId;
    }
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }
}
