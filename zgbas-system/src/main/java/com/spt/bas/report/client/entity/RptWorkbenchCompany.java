package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.core.annotation.LogField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 业务经理工作台企业数据
 */
@Data
public class RptWorkbenchCompany {

    /**
     * 合同ID
     */
    private Long id;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业来源
     */
    private String companySource;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 额度审核评估 （t_bs_company_visit）
     */
    private String recommendedAmount;

    /**
     * 人保录入日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date piccEntryDate;

    /**
     * 申请人保日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date applyPiccDate;

    /**
     * 申请赊销额度
     */
    private String piccApplyCreditAmount;

    /**
     * 人保申请状态
     */
    private String piccApplyStatus;

    /**
     * 企业类型  I-工业客户  T-贸易商
     */
    private String companyType;

    /**
     * 行业分类
     */
    private String industry;

    /**
     * 企业性质
     */
    private String companyCategory;

    /**
     * 授信信息
     */
    private String creditInfo;

    /**
     * 首次成交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date firstContractTime;

    /**
     * 最近成交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date lastContractTime;

    /**
     * 交易总金额
     */
    private BigDecimal totalContractAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Date createdDate;

}
