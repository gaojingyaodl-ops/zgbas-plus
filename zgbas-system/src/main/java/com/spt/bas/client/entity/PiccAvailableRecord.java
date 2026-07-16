package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-04-25 15:16
 */
@Entity
@Table(name = "t_picc_available_record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PiccAvailableRecord extends IdEntity {
    private String limitAmount;

    private String usedAmount;

    private String useAbleaMount;

    private String piccCode;

    private Long companyId;

    public String getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(String limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(String usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getUseAbleaMount() {
        return useAbleaMount;
    }

    public void setUseAbleaMount(String useAbleaMount) {
        this.useAbleaMount = useAbleaMount;
    }

    public String getPiccCode() {
        return piccCode;
    }

    public void setPiccCode(String piccCode) {
        this.piccCode = piccCode;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}
