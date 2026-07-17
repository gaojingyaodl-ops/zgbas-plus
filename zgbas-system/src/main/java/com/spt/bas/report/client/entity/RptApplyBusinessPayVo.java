package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RptApplyBusinessPayVo extends PageSearchVo {
    private Long id;
    private String belogDept;				//所属部门
    private BigDecimal dealAmount;			//金额
    private Long approveId;					//审批ID
    private String status;					//状态 N-新增，A-审批中，B-驳回，D-完成
    private Long applyUserId;				//申请人ID
    private String applyUserName;			//申请人
    private String costType;				//费用类型
    private String companyName;				//公司名称
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyDate;					//申请日期
    private String subject;					//摘要
    private String remark;					//备注
    private String fileId;					//附件
    private Long enterpriseId;				//企业账套ID
    private Long deptId;                     //部门Id
    private Long contractId;                     //合同Id
    private Long pairId;
    private  BigDecimal sum;
    /**
     * 合并付款list
     */
    private String contractList;
    /**
     * 收款方
     */
    private String receiveCompanyName;
    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 银行名字
     */
    private  String bankName;

    /**
     * 承运商
     */
    private String carrier;

    private  String  costType2;

    private List<String> typeList;

    private   String beginTime;

    private  String endTime;

    private  BigDecimal amount;

    private  BigDecimal amount2;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount2() {
        return amount2;
    }

    public void setAmount2(BigDecimal amount2) {
        this.amount2 = amount2;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }


    public String getCostType2() {
        return costType2;
    }

    public void setCostType2(String costType2) {
        this.costType2 = costType2;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractList() {
        return contractList;
    }

    public void setContractList(String contractList) {
        this.contractList = contractList;
    }

    public String getReceiveCompanyName() {
        return receiveCompanyName;
    }

    public void setReceiveCompanyName(String receiveCompanyName) {
        this.receiveCompanyName = receiveCompanyName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBelogDept() {
        return belogDept;
    }
    public void setBelogDept(String belogDept) {
        this.belogDept = belogDept;
    }
    public BigDecimal getDealAmount() {
        return dealAmount;
    }
    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }
    public Long getApproveId() {
        return approveId;
    }
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Long getApplyUserId() {
        return applyUserId;
    }
    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }
    public String getApplyUserName() {
        return applyUserName;
    }
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }
    public String getCostType() {
        return costType;
    }
    public void setCostType(String costType) {
        this.costType = costType;
    }
    public Date getApplyDate() {
        return applyDate;
    }
    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public Long getEnterpriseId() {
        return enterpriseId;
    }
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
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

}
