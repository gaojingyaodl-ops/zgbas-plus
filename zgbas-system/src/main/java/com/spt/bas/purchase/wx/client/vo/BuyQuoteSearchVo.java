package com.spt.bas.purchase.wx.client.vo;

import com.spt.tools.core.bean.PageSearchVo;

/**
 * @Author 田起立
 * @Date 2024/1/30 14:06
 * @Description:
 */
public class BuyQuoteSearchVo extends PageSearchVo {
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 报价单号
     */
    private String oddNumber;

    public String getOddNumber() {
        return oddNumber;
    }

    public void setOddNumber(String oddNumber) {
        this.oddNumber = oddNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
