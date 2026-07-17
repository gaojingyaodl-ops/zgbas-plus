package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *  支付货款历史详情 支付分批次
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-11-16 14:41
 */
public class RptCtrPayDetailVo {
    /**
     * 对应的付款的编号ID，根据ID可以查询到付款详情
     */
    private String dealedId;

    /**
     * 该付款的金额
     */
    private BigDecimal dealedAmount;

    /**
     * 付款的时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date dealedDate;

    /**
     * 付款类型  B-定金，R-尾款，A-全款，S-仓储费，T-运费
     */
    private String dealedType;

    /**
     * 付款方式 H-票汇，Z-支票，T-电汇，D-信用证
     */
    private String dealedMode;

    /**
     * 备注
     */
    private String remark;

    public String getDealedId() {
        return dealedId;
    }

    public void setDealedId(String dealedId) {
        this.dealedId = dealedId;
    }

    public BigDecimal getDealedAmount() {
        return dealedAmount;
    }

    public void setDealedAmount(BigDecimal dealedAmount) {
        this.dealedAmount = dealedAmount;
    }

    public Date getDealedDate() {
        return dealedDate;
    }

    public void setDealedDate(Date dealedDate) {
        this.dealedDate = dealedDate;
    }

    public String getDealedType() {
        return dealedType;
    }

    public void setDealedType(String dealedType) {
        this.dealedType = dealedType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDealedMode() {
        return dealedMode;
    }

    public void setDealedMode(String dealedMode) {
        this.dealedMode = dealedMode;
    }
}
