package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 公司准入批复表
 *
 */
@Entity
@Table(name = "t_bs_company_allowed_ophis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyAllowedOphis extends IdEntity {
    private static final long serialVersionUID = 5355741198542159112L;
    private Long baCompanyAllowedId;				//公司准入申请id
    private Long opUserId;				//操作人ID
    private String opUserName;			//操作人
    private Long enterpriseId;			//企业账套ID
    private String remark;				//备注

    public Long getBaCompanyAllowedId() {
        return baCompanyAllowedId;
    }

    public void setBaCompanyAllowedId(Long baCompanyAllowedId) {
        this.baCompanyAllowedId = baCompanyAllowedId;
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

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
