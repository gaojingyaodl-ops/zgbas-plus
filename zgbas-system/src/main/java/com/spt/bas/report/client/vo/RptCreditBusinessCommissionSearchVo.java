package com.spt.bas.report.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.bean.PageSearchVo;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: gaojy
 * @create 2021/12/23 16:18
 * @version: 1.0
 * @description:
 */
public class RptCreditBusinessCommissionSearchVo extends PageSearchVo {

    /**
     * 合同号
     */
    private String contractNo;

    /**
     * 业务员
     */
    private Long matchUserId;

    /**
     * 销售人员
     */
    private String sellMatchUserName;

    /**
     * 采购人员
     */
    private String buyMatchUserName;

    /**
     * 业务类型
     */
    private String buType;

    /**
     * 查询合同时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTimeStart;

    /**
     * 查询合同时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTimeEnd;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }

    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public Date getContractTimeStart() {
        return contractTimeStart;
    }

    public void setContractTimeStart(Date contractTimeStart) {
        this.contractTimeStart = contractTimeStart;
    }

    public String getBuType() {
        return buType;
    }

    public void setBuType(String buType) {
        this.buType = buType;
    }

    public Date getContractTimeEnd() {
        return contractTimeEnd;
    }

    public void setContractTimeEnd(Date contractTimeEnd) {
        this.contractTimeEnd = contractTimeEnd;
    }
}
