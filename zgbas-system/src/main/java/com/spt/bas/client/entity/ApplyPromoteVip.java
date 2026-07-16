package com.spt.bas.client.entity;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * vip提额
 */
@Entity
@Table(name = "t_apply_promote_vip_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class ApplyPromoteVip extends IdEntity implements IPmEntity {

    /**
     * 提升至额度
     */
    private BigDecimal promoteCreditAmount;

    private Long approveId;

    /**
     * 差额
     */
    private BigDecimal promoteVipAmout;

    /**
     *  公司名称
     */
    private String companyName;
    /**
     * 公司id
     *
     *
     */
    private  Long  companyId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 部门Id
     * @return
     */
    private Long deptId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }



    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ApplyPromoteVip() {
    }

    public BigDecimal getPromoteCreditAmount() {
        return promoteCreditAmount;
    }

    public void setPromoteCreditAmount(BigDecimal promoteCreditAmount) {
        this.promoteCreditAmount = promoteCreditAmount;
    }

    public BigDecimal getPromoteVipAmout() {
        return promoteVipAmout;
    }

    public void setPromoteVipAmout(BigDecimal promoteVipAmout) {
        this.promoteVipAmout = promoteVipAmout;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}
