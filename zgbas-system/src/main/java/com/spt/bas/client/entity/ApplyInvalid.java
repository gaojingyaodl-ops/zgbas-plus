package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 作废申请
 *
 * @Author MoonLight
 * @Date 2023/9/11 14:03
 * @Version 1.0
 */
@Entity
@Table(name = "t_apply_invalid")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyInvalid extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = -1L;

    /**
     * 合同尾号
     */
    private String contractTailNo;

    /**
     * 采购合同ID
     */
    private Long buyContractId;

    /**
     * 销售合同iD
     */
    private Long sellContractId;

    /**
     * 预算申请单ID
     */
    private Long budgetApproveId;

    /**
     * 作废类型
     * CC-合同作废
     * CP-采购付款作废
     * CI-采购入库作废
     * CE-采购收票作废
     * CR-销售收款作废
     * CO-销售出库作废
     * CV-销售开票作废
     * CM-确认收货作废
     * CPD-代采赊销付款作废
     * CRD-代采赊销收款作废
     * CED-代采赊销收票作废
     * CVD-代采赊销开票作废
     * CMD-代采赊销确认收货作废
     */
    private String invalidType;

    /**
     * 贸易链条
     */
    private String tradeChain;

    /**
     * 作废原因
     */
    private String invalidRemark;

    /**
     * 作废审批单ID，多单英文逗号连接
     */
    private String invalidApproveIds;

    /**
     * 作废申请状态 'N-新增，A-审批中，B-驳回，D-完成',
     */
    private String status;

    /**
     * 作废申请审批ID
     */
    private Long approveId;

    /**
     * 附件ID
     */
    private String fileId;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 所属区域
     */
    private String ownRegion;

    public String getContractTailNo() {
        return contractTailNo;
    }

    public void setContractTailNo(String contractTailNo) {
        this.contractTailNo = contractTailNo;
    }

    public Long getBuyContractId() {
        return buyContractId;
    }

    public void setBuyContractId(Long buyContractId) {
        this.buyContractId = buyContractId;
    }

    public Long getSellContractId() {
        return sellContractId;
    }

    public void setSellContractId(Long sellContractId) {
        this.sellContractId = sellContractId;
    }

    public Long getBudgetApproveId() {
        return budgetApproveId;
    }

    public void setBudgetApproveId(Long budgetApproveId) {
        this.budgetApproveId = budgetApproveId;
    }

    public String getInvalidType() {
        return invalidType;
    }

    public void setInvalidType(String invalidType) {
        this.invalidType = invalidType;
    }

    public String getTradeChain() {
        return tradeChain;
    }

    public void setTradeChain(String tradeChain) {
        this.tradeChain = tradeChain;
    }

    public String getInvalidRemark() {
        return invalidRemark;
    }

    public void setInvalidRemark(String invalidRemark) {
        this.invalidRemark = invalidRemark;
    }

    public String getInvalidApproveIds() {
        return invalidApproveIds;
    }

    public void setInvalidApproveIds(String invalidApproveIds) {
        this.invalidApproveIds = invalidApproveIds;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getOwnRegion() {
        return ownRegion;
    }

    public void setOwnRegion(String ownRegion) {
        this.ownRegion = ownRegion;
    }
}
