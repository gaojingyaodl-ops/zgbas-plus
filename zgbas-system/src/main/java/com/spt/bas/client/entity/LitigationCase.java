package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_litigation_case")
public class LitigationCase extends IdEntity {
    private String caseNo; // 案号
    private String competentCourt; // 管辖法院
    private String lawyer; // 接洽律师
    private Boolean settleFlag; // 是否结案标识(0-未结案；1-已结案)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date judgmentDate; // 判决时间
    private String linkContractIds; // 关联合同ID(多个逗号连接)
    private String linkContractNos; // 关联合同No(多个逗号连接)
    private BigDecimal attorneyFee; // 律师费
    private Long attorneyApproveId; // 律师费申请审批ID
    private String attorneyApproveStatus; // 律师费申请状态
    private Long processingApproveId; // 案件受理费申请审批ID
    private BigDecimal processingFee; // 案件受理费
    private String processingApproveStatus; // 案件受理费申请状态
    private BigDecimal preservationFee; // 保全费
    private Long preservationApproveId; // 保全费申请审批ID
    private String preservationApproveStatus; // 保全费申请状态
    private BigDecimal liabilityFee; // 诉责费
    private Long liabilityApproveId; // 诉责费申请审批ID
    private String liabilityApproveStatus; // 诉责费申请状态
    private BigDecimal totalFee; // 费用合计
    private String caseFollow; // 案件进展跟进

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public String getCompetentCourt() {
        return competentCourt;
    }

    public void setCompetentCourt(String competentCourt) {
        this.competentCourt = competentCourt;
    }

    public String getLawyer() {
        return lawyer;
    }

    public void setLawyer(String lawyer) {
        this.lawyer = lawyer;
    }

    public Boolean getSettleFlag() {
        return settleFlag;
    }

    public void setSettleFlag(Boolean settleFlag) {
        this.settleFlag = settleFlag;
    }

    public Date getJudgmentDate() {
        return judgmentDate;
    }

    public void setJudgmentDate(Date judgmentDate) {
        this.judgmentDate = judgmentDate;
    }

    public String getLinkContractIds() {
        return linkContractIds;
    }

    public void setLinkContractIds(String linkContractIds) {
        this.linkContractIds = linkContractIds;
    }

    public String getLinkContractNos() {
        return linkContractNos;
    }

    public void setLinkContractNos(String linkContractNos) {
        this.linkContractNos = linkContractNos;
    }

    public BigDecimal getAttorneyFee() {
        return attorneyFee;
    }

    public void setAttorneyFee(BigDecimal attorneyFee) {
        this.attorneyFee = attorneyFee;
    }

    public Long getAttorneyApproveId() {
        return attorneyApproveId;
    }

    public void setAttorneyApproveId(Long attorneyApproveId) {
        this.attorneyApproveId = attorneyApproveId;
    }

    public String getAttorneyApproveStatus() {
        return attorneyApproveStatus;
    }

    public void setAttorneyApproveStatus(String attorneyApproveStatus) {
        this.attorneyApproveStatus = attorneyApproveStatus;
    }

    public Long getProcessingApproveId() {
        return processingApproveId;
    }

    public void setProcessingApproveId(Long processingApproveId) {
        this.processingApproveId = processingApproveId;
    }

    public BigDecimal getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(BigDecimal processingFee) {
        this.processingFee = processingFee;
    }

    public String getProcessingApproveStatus() {
        return processingApproveStatus;
    }

    public void setProcessingApproveStatus(String processingApproveStatus) {
        this.processingApproveStatus = processingApproveStatus;
    }

    public BigDecimal getPreservationFee() {
        return preservationFee;
    }

    public void setPreservationFee(BigDecimal preservationFee) {
        this.preservationFee = preservationFee;
    }

    public Long getPreservationApproveId() {
        return preservationApproveId;
    }

    public void setPreservationApproveId(Long preservationApproveId) {
        this.preservationApproveId = preservationApproveId;
    }

    public String getPreservationApproveStatus() {
        return preservationApproveStatus;
    }

    public void setPreservationApproveStatus(String preservationApproveStatus) {
        this.preservationApproveStatus = preservationApproveStatus;
    }

    public BigDecimal getLiabilityFee() {
        return liabilityFee;
    }

    public void setLiabilityFee(BigDecimal liabilityFee) {
        this.liabilityFee = liabilityFee;
    }

    public Long getLiabilityApproveId() {
        return liabilityApproveId;
    }

    public void setLiabilityApproveId(Long liabilityApproveId) {
        this.liabilityApproveId = liabilityApproveId;
    }

    public String getLiabilityApproveStatus() {
        return liabilityApproveStatus;
    }

    public void setLiabilityApproveStatus(String liabilityApproveStatus) {
        this.liabilityApproveStatus = liabilityApproveStatus;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getCaseFollow() {
        return caseFollow;
    }

    public void setCaseFollow(String caseFollow) {
        this.caseFollow = caseFollow;
    }
}
