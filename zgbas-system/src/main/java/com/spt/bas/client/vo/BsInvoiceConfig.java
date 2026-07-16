package com.spt.bas.client.vo;

import java.util.List;

/**
 * 自动开收票配置项
 *
 * @author MoonLight
 */
public class BsInvoiceConfig {

    /**
     * 是否自动开票
     */
    private Boolean autoPayInvoice;

    /**
     * 是否自动收票
     */
    private Boolean autoReceiveInvoice;

    /**
     * 自动开票-商品CD
     */
    private List<String> autoPayProductCdList;

    /**
     * 自动收票-商品CD
     */
    private List<String> autoReceiveProductCdList;

    public Boolean getAutoPayInvoice() {
        return autoPayInvoice;
    }

    public void setAutoPayInvoice(Boolean autoPayInvoice) {
        this.autoPayInvoice = autoPayInvoice;
    }

    public Boolean getAutoReceiveInvoice() {
        return autoReceiveInvoice;
    }

    public void setAutoReceiveInvoice(Boolean autoReceiveInvoice) {
        this.autoReceiveInvoice = autoReceiveInvoice;
    }

    public BsInvoiceConfig() {
    }

    public BsInvoiceConfig(Boolean autoPayInvoice, Boolean autoReceiveInvoice) {
        this.autoPayInvoice = autoPayInvoice;
        this.autoReceiveInvoice = autoReceiveInvoice;
    }

    public List<String> getAutoPayProductCdList() {
        return autoPayProductCdList;
    }

    public void setAutoPayProductCdList(List<String> autoPayProductCdList) {
        this.autoPayProductCdList = autoPayProductCdList;
    }

    public List<String> getAutoReceiveProductCdList() {
        return autoReceiveProductCdList;
    }

    public void setAutoReceiveProductCdList(List<String> autoReceiveProductCdList) {
        this.autoReceiveProductCdList = autoReceiveProductCdList;
    }
}
