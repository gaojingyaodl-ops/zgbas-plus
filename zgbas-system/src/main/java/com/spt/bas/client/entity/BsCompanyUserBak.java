package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "t_bs_company_userbak")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsCompanyUserBak extends IdEntity {

    /**
     * 领用人id
     */
    private Long matchUserId;
    /**
     * 领用时间
     */
    private Date matchFollowDate;
    /**
     * 领用企业名称
     */
    private Long companyId;

    /**
     * 弃用时间
     */
    private Date disuseFollowDate;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public Date getMatchFollowDate() {
        return matchFollowDate;
    }

    public void setMatchFollowDate(Date matchFollowDate) {
        this.matchFollowDate = matchFollowDate;
    }

    public Date getDisuseFollowDate() {
        return disuseFollowDate;
    }

    public void setDisuseFollowDate(Date disuseFollowDate) {
        this.disuseFollowDate = disuseFollowDate;
    }

}
