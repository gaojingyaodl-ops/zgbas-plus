package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 事项申请单
 */
@Entity
@Table(name = "t_apply_matters")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyMatters extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -2823344963914806824L;

    /**
     * 事项类型
     */
    private String mattersType;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 审批单号
     */
    private String approveNo;

    /**
     * 内容
     */
    private String content;

    /**
     * 附件
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 所属区域
     */
    private String ownRegion;

    public String getMattersType() {
        return mattersType;
    }

    public void setMattersType(String mattersType) {
        this.mattersType = mattersType;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
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

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnRegion() {
        return ownRegion;
    }

    public void setOwnRegion(String ownRegion) {
        this.ownRegion = ownRegion;
    }
}
