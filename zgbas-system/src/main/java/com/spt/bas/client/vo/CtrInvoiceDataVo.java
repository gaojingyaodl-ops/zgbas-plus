package com.spt.bas.client.vo;

/**
 * @Author MoonLight
 * @Date 2024/8/15 14:33
 * @Version 1.0
 */
public class CtrInvoiceDataVo {
    private String contractNo;
    private String fileId;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public CtrInvoiceDataVo() {
    }

    public CtrInvoiceDataVo(String contractNo, String fileId) {
        this.contractNo = contractNo;
        this.fileId = fileId;
    }
}
