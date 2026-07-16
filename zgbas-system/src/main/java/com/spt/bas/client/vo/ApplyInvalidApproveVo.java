package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author MoonLight
 * @Date 2023/9/12 16:08
 * @Version 1.0
 */
public class ApplyInvalidApproveVo {
    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 审批单号
     */
    private String approveNo;

    /**
     * 合同ID
     */
    private Long contractId;


    /**
     * 流程ID
     */
    private Long processId;

    /**
     * 流程代码
     */
    private String processCode;

    /**
     * 作废类型代码
     */
    private String invalidTypeCode;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 发起人
     */
    private String startUserName;


    /**
     * 发起时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date startDate;

    /**
     * 摘要
     */
    private String subject;

    public Long getApproveId() {
        return approveId;
    }

    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getStartUserName() {
        return startUserName;
    }

    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInvalidTypeCode() {
        return invalidTypeCode;
    }

    public void setInvalidTypeCode(String invalidTypeCode) {
        this.invalidTypeCode = invalidTypeCode;
    }
}
