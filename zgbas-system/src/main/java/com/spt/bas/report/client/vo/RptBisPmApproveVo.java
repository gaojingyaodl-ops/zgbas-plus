package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2023/11/8 10:23
 */

public class RptBisPmApproveVo {

    /**
     * 审批编号
     */
    private String approveNo;

    private String type;

    /**
     * 差旅费/公关费
     */
    private BigDecimal money;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
