package com.spt.bas.client.entity;

import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 代采赊销中间链条表
 *
 * @Author: gaojy
 * @create 2022/9/14 10:33
 * @version: 1.0
 * @description:
 */
@Entity
@Table(name = "t_apply_match_chain")
public class ApplyMatchChain extends IdEntity implements IPmEntity {

    /**
     * 代采赊销ID
     */
    private Long applyMatchId;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 链条企业名称
     */
    private String chainCompanyName;

    /**
     * 链条采购单价
     */
    private BigDecimal buyDealPrice;

    /**
     * 链条销售单价
     */
    private BigDecimal sellDealPrice;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 业务类型 DC(代采)
     */
    private String businessType;


    /**
     * 备注
     */
    private String remark;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }


    public Long getApplyMatchId() {
        return applyMatchId;
    }

    public void setApplyMatchId(Long applyMatchId) {
        this.applyMatchId = applyMatchId;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getChainCompanyName() {
        return chainCompanyName;
    }

    public void setChainCompanyName(String chainCompanyName) {
        this.chainCompanyName = chainCompanyName;
    }

    public BigDecimal getBuyDealPrice() {
        return buyDealPrice;
    }

    public void setBuyDealPrice(BigDecimal buyDealPrice) {
        this.buyDealPrice = buyDealPrice;
    }

    public BigDecimal getSellDealPrice() {
        return sellDealPrice;
    }

    public void setSellDealPrice(BigDecimal sellDealPrice) {
        this.sellDealPrice = sellDealPrice;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ApplyMatchChain() {
    }

    public ApplyMatchChain(String chainCompanyName) {
        this.chainCompanyName = chainCompanyName;
    }

    public ApplyMatchChain(String chainCompanyName, BigDecimal buyDealPrice) {
        this.chainCompanyName = chainCompanyName;
        this.buyDealPrice = buyDealPrice;
    }
}
