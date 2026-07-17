package com.spt.bas.report.client.vo;


import java.util.List;

/**
 * 合同日期searchVO
 */
public class RptContractDateSearchVo {
    
    private List<String> buyContractNoList;

    private List<String> sellContractNoList;

    private List<String> dcsxContractNoList;

    
    public List<String> getBuyContractNoList() {
        return buyContractNoList;
    }

    public void setBuyContractNoList(List<String> buyContractNoList) {
        this.buyContractNoList = buyContractNoList;
    }

    public List<String> getSellContractNoList() {
        return sellContractNoList;
    }

    public void setSellContractNoList(List<String> sellContractNoList) {
        this.sellContractNoList = sellContractNoList;
    }

    public List<String> getDcsxContractNoList() {
        return dcsxContractNoList;
    }

    public void setDcsxContractNoList(List<String> dcsxContractNoList) {
        this.dcsxContractNoList = dcsxContractNoList;
    }
}
