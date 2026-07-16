package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>
 *   终端工厂自提审批申请单
 * </p>
 *
 */
@Entity
@Table(name = "t_apply_terminal_pick")
public class ApplyTerminalPick extends IdEntity implements IPmEntity {

    /**
     * 公司Id
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 是否终端工厂配送
     */
    private String creditDelivery;
    /**
     * 备注
     */
    @Transient
    private String remark;

    /**
     * 审批Id
     */
    private Long approveId;

    /**
     * 附件Id
     */
    private String fileId;

    /**
     *自提审批状态
     */
    private String creditDeliveryStatus;

    /**
     * 部门Id
     * @return
     */
    private Long deptId;

    public String getCreditDeliveryStatus() {
        return creditDeliveryStatus;
    }

    public void setCreditDeliveryStatus(String creditDeliveryStatus) {
        this.creditDeliveryStatus = creditDeliveryStatus;
    }

    public String getCreditDelivery() {
        return creditDelivery;
    }

    public void setCreditDelivery(String creditDelivery) {
        this.creditDelivery = creditDelivery;
    }


    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    @Override
    public void setStatus(String status) {

    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
    @Transient
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
