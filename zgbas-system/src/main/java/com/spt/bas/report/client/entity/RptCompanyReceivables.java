package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户应收款 VO
 */
@Data
public class RptCompanyReceivables extends IdEntity {

    private Long id;

    /**
     * 我方抬头
     */
    private String ourCompanyName;

    /**
     * 企业名称
     */
    private String companyName;

	/**
	 * 交易吨数
	 */
	private BigDecimal tradeTonnes;

	/**
	 * 销售总价
	 */
	private BigDecimal totalAmount;

    /**
     * 交易单数
     */
    private Integer tradeCount;

    /**
     * 已收本金
     */
    private BigDecimal dealedAmount;
    
    /**
     * 应收本金
     */
    private BigDecimal receivablePrincipal;

    /**
     * 罚息
     */
    private BigDecimal breachAmount;
    /**
     * 已收罚息
     */
    private BigDecimal receiveBreachAmount;
    
    /**
     * 应收罚息
     */
    private BigDecimal receivableBreachAmount;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 产品名称
     */
    private String productName;

    private String businessType;
    private Boolean matchCreditFlg;
    
    private Long matchUserId;
    private String matchUserName;
    private Long deptId;
    private String deptName;
    
    /**
     * 双签日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sealDate;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
