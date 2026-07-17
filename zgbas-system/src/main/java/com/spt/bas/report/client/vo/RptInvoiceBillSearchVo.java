package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
public class RptInvoiceBillSearchVo extends PageSearchVo {

    private Long id;
    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 我方抬头
     */
    private String ourCompanyName;
    private List<String> ourCompanyNameList;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 签订日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 品种
     */
    private String productName;

    /**
     * 品名
     */
    private String productCd;

    /**
     * 数量
     */
    private BigDecimal dealNumber;

    /**
     * 单价
     */
    private BigDecimal dealPrice;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 申请日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyDate;

    /**
     * 开票日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date invoiceDate;

    /**
     * 发票号码
     */
    private String invoiceNo;


    /**
     * 开票申请编号
     */
    private String approveNo;

    /**
     * 审批状态
     */
    private String approveStatus;

    /**
     * 中游合同号
     */
    private String dcsxContractNo;

    /**
     * 中游开票状态
     */
    private String dcsxInvoiceStatus;

    /**
     * 合同签约日期
     */
    private String contractTimeBegin;
    private String contractTimeEnd;
    private String invoiceBillIds;
    private List<Long> invoiceBillIdList;

    /**
     * 资金方权限
     */
    private Boolean funderFlg = false;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前登录人
     */
    private String currApproveUserId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getInvoiceBillIdList() {
        return invoiceBillIdList;
    }

    public void setInvoiceBillIdList(List<Long> invoiceBillIdList) {
        this.invoiceBillIdList = invoiceBillIdList;
    }
}
