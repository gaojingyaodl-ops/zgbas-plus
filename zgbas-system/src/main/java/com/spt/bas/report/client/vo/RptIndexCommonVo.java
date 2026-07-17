package com.spt.bas.report.client.vo;

import java.math.BigDecimal;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/17 10:05
 */

public class RptIndexCommonVo {

    /**
     * 类型名字
     */
    private String typeName;
    /**
     * 金额
     */
    private BigDecimal money = BigDecimal.valueOf(0.00);
    /**
     * 数量
     */
    private BigDecimal num = BigDecimal.valueOf(0.00);

    /**
     * 吨数
     */
    private BigDecimal tonnes = BigDecimal.valueOf(0.00);

    /**
     * 业务人员id
     */
    private Long userId;

    public RptIndexCommonVo(String typeName) {
        this.typeName = typeName;
    }

    public RptIndexCommonVo(BigDecimal money, BigDecimal num) {
        this.money = money;
        this.num = num;
    }

    public RptIndexCommonVo(String typeName, BigDecimal money, BigDecimal num) {
        this.typeName = typeName;
        this.money = money;
        this.num = num;
    }

    public RptIndexCommonVo() {
    }

    public RptIndexCommonVo(BigDecimal money, BigDecimal num, BigDecimal tonnes) {
        this.money = money;
        this.num = num;
        this.tonnes = tonnes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }

    public BigDecimal getTonnes() {
        return tonnes;
    }

    public void setTonnes(BigDecimal tonnes) {
        this.tonnes = tonnes;
    }

    @Override
    public String toString() {
        return "IndexCommonVo{" +
                "money=" + money +
                ", num=" + num +
                ", tonnes=" + tonnes +
                '}';
    }
}
