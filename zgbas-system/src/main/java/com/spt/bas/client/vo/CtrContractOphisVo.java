package com.spt.bas.client.vo;

import com.spt.bas.client.entity.CtrContractOphis;

public class CtrContractOphisVo extends CtrContractOphis {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 采购合同id
     */
    private Long ctrBuyContractId;
    /**
     * 销售合同id
     */
    private Long ctrSellContractId;
    /**
     * 采购合同编号
     */
    private String buyContractNo;
    /**
     * 销售合同编号
     */
    private String sellContractNo;

    /**
     * 审批状态
     */
    private String approveStatus;

    /**
     * 审批编号
     */
    private String approveNo;

    /**
     * 审批名
     */
    private String approveTypeName;

    public Long getCtrBuyContractId() {
        return ctrBuyContractId;
    }

    public void setCtrBuyContractId(Long ctrBuyContractId) {
        this.ctrBuyContractId = ctrBuyContractId;
    }

    public Long getCtrSellContractId() {
        return ctrSellContractId;
    }

    public void setCtrSellContractId(Long ctrSellContractId) {
        this.ctrSellContractId = ctrSellContractId;
    }

    public String getBuyContractNo() {
        return buyContractNo;
    }

    public void setBuyContractNo(String buyContractNo) {
        this.buyContractNo = buyContractNo;
    }

    public String getSellContractNo() {
        return sellContractNo;
    }

    public void setSellContractNo(String sellContractNo) {
        this.sellContractNo = sellContractNo;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public String getApproveTypeName() {
        return approveTypeName;
    }

    public void setApproveTypeName(String approveTypeName) {
        this.approveTypeName = approveTypeName;
    }
}
