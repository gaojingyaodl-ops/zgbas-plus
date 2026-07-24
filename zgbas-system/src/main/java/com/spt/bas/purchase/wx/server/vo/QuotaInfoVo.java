package com.spt.bas.purchase.wx.server.vo;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>
 * 个人额度信息
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-28 12:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//@ApiModel(value = "个人额度信息")
public class QuotaInfoVo {
    /**
     * 白条额度
     */
    //@ApiModelProperty(value = "白条额度", required = true)
    private BigDecimal totalCreditAmount;

    /**
     * 已使用白条额度
     */
    //@ApiModelProperty(value = "已使用白条额度", required = true)
    private BigDecimal usedCreditAmount;

    /**
     * 可使用白条额度
     */
    //@ApiModelProperty(value = "可使用白条额度", required = true)
    private BigDecimal availableCreditAmount;

    /**
     * 期货额度
     */
    //@ApiModelProperty(value = "期货额度", required = true)
    private BigDecimal totalFuturesAmount;

    /**
     * 已使用期货额度
     */
    //@ApiModelProperty(value = "已使用期货额度", required = true)
    private BigDecimal usedFuturesAmount;

    /**
     * 可使用期货额度
     */
    //@ApiModelProperty(value = "可使用期货额度", required = true)
    private BigDecimal availableFuturesAmount;

    /**
     * 现货额度
     */
    //@ApiModelProperty(value = "现货额度", required = true)
    private BigDecimal totalSpotAmount;

    /**
     * 已使用现货额度
     */
    //@ApiModelProperty(value = "已使用现货额度", required = true)
    private BigDecimal usedSpotAmount;

    /**
     * 可使用现货额度
     */
    //@ApiModelProperty(value = "可使用现货额度", required = true)
    private BigDecimal availableSpotAmount;
}
