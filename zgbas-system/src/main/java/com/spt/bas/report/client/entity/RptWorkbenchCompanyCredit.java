package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 业务经理工作台企业数据
 */
@Data
public class RptWorkbenchCompanyCredit {

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 授信类别
     * 0-人保
     * 1-大地
     * 9-自主
     */
    private String creditType;

    /**
     * 授信额度
     */
    private BigDecimal creditAmount;

    /**
     * 风控额度
     */
    private BigDecimal riskAmount;

    /**
     * 已用额度
     */
    private BigDecimal usedCreditAmount;

    /**
     * 临时额度
     */
    private BigDecimal temporaryAmount;

    /**
     * 是否有效
     */
    private Boolean enableFlg;

}
