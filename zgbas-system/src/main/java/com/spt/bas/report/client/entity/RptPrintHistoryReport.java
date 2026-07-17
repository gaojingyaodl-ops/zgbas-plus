package com.spt.bas.report.client.entity;

import java.io.Serializable;

/**
 * @author 杨英承
 * @version 1.0.0
 * @date 2022/6/16 10:44
 */

public class RptPrintHistoryReport implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 审批单号
     */
    private String approveNo;
    /**
     * 打印人id
     */
    private Long printId;
    /**
     * 打印人名字
     */
    private String printName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }

    public Long getPrintId() {
        return printId;
    }

    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String printName) {
        this.printName = printName;
    }

    @Override
    public String toString() {
        return "RptPrintHistoryReport{" +
                "id=" + id +
                ", approveNo='" + approveNo + '\'' +
                ", printId=" + printId +
                ", printName='" + printName + '\'' +
                '}';
    }
}
