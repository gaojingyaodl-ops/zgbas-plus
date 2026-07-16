package com.spt.bas.client.vo.basTrade;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 预算发起vo
 * @author MoonLight
 * @version 1.0
 * @description
 * @date 2025/6/18 15:47
 */
@Data
public class BasTradeMatchVo {

    private Long enterpriseId;

    private String status;

    private String ourCompanyName;

    private String businessType;

    private String qualityStandard;

    private String productName;

    private String productCd;

    private String brandNumber;

    private Long factoryId;

    private String factoryName;

    private BigDecimal dealNumber;

    private String wrapSpecs;

    private String contractModel;

    private Boolean liabilityFlg;

    private Long createdUserId;

    private Long applyUserId;

    private List<BasTradeMatchVo.BasTradeMatchDetailVo> lstInsert;

    private Boolean tradeFlg;


    @Data
    @Accessors(chain = true)
    public static class BasTradeMatchDetailVo {

        private String contractType;

        private Date payFullTime;

        private Date receiveFullTime;

        private Date payBondTime;

        private BigDecimal payBondAmount;

        private String payType;

        private String receiveType;

        private String payRemark;

        private String receiveRemark;

        private BigDecimal receiveBondAmount;

        private String deliveryMode;

        private String deliveryType;

        private BigDecimal warehouseCost;

        private BigDecimal transportCost;

        private String companyName;

        private String contactAddr;

        private Long companyId;

        private String contractNo;

        private Long matchUserId;

        private String matchUserName;

        private String arrivalTimeExt;

        private String extraTerm;

        private BigDecimal totalAmount;

        private String deliveryAddr;

        private String buySource;

        private String sellSource;

        private BigDecimal payRateAmount;

        private Date deliveryDate;

        private BigDecimal dealPrice;

        private Double dealAmountNotax;

        private Long buyTemplateId;

        private String buyContentTemplateId;

        private Long sellTemplateId;

        private String sellContentTemplateId;

        private String ourCompanyName;

        private String sellOurCompanyName;

        private BigDecimal stevedorage;

        private String provinceName;

        private String cityName;

        private String areaCode;

        private String receiptArrivedFlg;

        private Integer creditDays;

        private BigDecimal grossProfit;
        
        private BigDecimal premium;

        private BigDecimal payRate;

    }
}
