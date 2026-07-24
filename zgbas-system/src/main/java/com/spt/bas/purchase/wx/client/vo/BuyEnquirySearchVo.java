package com.spt.bas.purchase.wx.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

public class BuyEnquirySearchVo extends PageSearchVo {

    private Long id;

    /**
     * 微信用户唯一标识
     */
    private String openId;

    /**
     * 客户id
     */
    private Long companyId;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 状态(0-为成交，1-已成交)
     */
    private String status;

    /**
     * 单号
     */
    private String oddNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOddNumber() {
        return oddNumber;
    }

    public void setOddNumber(String oddNumber) {
        this.oddNumber = oddNumber;
    }
}
