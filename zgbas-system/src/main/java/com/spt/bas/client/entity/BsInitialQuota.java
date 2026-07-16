package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>
 *  公司初始额度
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-01-19 17:55
 */
@Entity
@Table(name = "t_bs_initial_quota")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
public class BsInitialQuota extends IdEntity {

    private static final long serialVersionUID = 9004037093331693966L;

    private Long companyId;

    /**
     * 初始额度
     */
    private BigDecimal initialQuota;

    /**
     * 初始账期
     */
    private Integer initialCreditCycle;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getInitialQuota() {
        return initialQuota;
    }

    public void setInitialQuota(BigDecimal initialQuota) {
        this.initialQuota = initialQuota;
    }

    public Integer getInitialCreditCycle() {
        return initialCreditCycle;
    }

    public void setInitialCreditCycle(Integer initialCreditCycle) {
        this.initialCreditCycle = initialCreditCycle;
    }
}
