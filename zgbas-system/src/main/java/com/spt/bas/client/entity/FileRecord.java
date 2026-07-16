package com.spt.bas.client.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-12-02 13:54
 */
@Entity
@Table(name = "t_file_record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("附件记录")
public class FileRecord extends IdEntity {
    /**
     * 附件id
     */
    private String fileId;

    /**
     * 附件类型
     */
    private Long fileTypeId;

    /**
     * 有效性
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;

    /**
     * 相关联合同id
     */
    private Long contractId;

    /**
     * 相关联合同号
     */
    private String contractNo;

    /**
     * 相关联审批号
     */
    private String approveNo;

    /**
     * 相关联审批id
     */
    private Long approveId;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    private String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
