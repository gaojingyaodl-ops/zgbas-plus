package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 人保还款
 */
@Entity
@Table(name = "t_picc_pay_apply")
public class PiccPayApply extends IdEntity {
    private static final long serialVersionUID = 6330631828677838481L;

    public String contractNo;
    public Long contractId;// 合同id
    public BigDecimal recoverAmount;// 还款金额
    public Date recoverDate;// 回款日期
    public String state;//状态：1 人保发送成功 2人保发送失败 3 回款正常 4 赊销申请批复成功
    public Long piccShipmentApplyId;
    private String applyNo;//收款申请单号

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public BigDecimal getRecoverAmount() {
        return recoverAmount;
    }

    public void setRecoverAmount(BigDecimal recoverAmount) {
        this.recoverAmount = recoverAmount;
    }

    public Date getRecoverDate() {
        return recoverDate;
    }

    public void setRecoverDate(Date recoverDate) {
        this.recoverDate = recoverDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getPiccShipmentApplyId() {
        return piccShipmentApplyId;
    }

    public void setPiccShipmentApplyId(Long piccShipmentApplyId) {
        this.piccShipmentApplyId = piccShipmentApplyId;
    }
}
