package com.spt.bas.client.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 业务提货审批表（面单）导出Vo
 * @Author: gaojy
 * @create 2022/4/14 16:10
 * @version: 1.0
 * @description:
 */
public class BusinessDeliveryExcelVo {

    /**
     * 品名/牌号/厂商
     */
    private String productNames;

    /**
     * 存货仓库
     */
    private String deliveryName;

    /**
     * 仓库地址
     */
    private String deliveryAddress = "";

    /**
     * 业务员
     */
    private String matchUserName;

    private List<ExcelDetail> excelDetailList;


    public static class ExcelDetail{
        /**
         * 提货单位
         */
        private String companyName = "";

        /**
         * 日期
         */
        private Date excelDate;

        /**
         * 摘要
         */
        private String subject;

        /**
         * 合同号
         */
        private String contractNo;

        /**
         * 入库数量
         */
        private BigDecimal deliveryInNumber;

        /**
         * 提/送货数量
         */
        private BigDecimal deliveryNumber;

        /**
         * 未提数量
         */
        private BigDecimal deliveryNoNumber;

        public ExcelDetail() {
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Date getExcelDate() {
            return excelDate;
        }

        public void setExcelDate(Date excelDate) {
            this.excelDate = excelDate;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContractNo() {
            return contractNo;
        }

        public void setContractNo(String contractNo) {
            this.contractNo = contractNo;
        }

        public BigDecimal getDeliveryInNumber() {
            return deliveryInNumber;
        }

        public void setDeliveryInNumber(BigDecimal deliveryInNumber) {
            this.deliveryInNumber = deliveryInNumber;
        }

        public BigDecimal getDeliveryNumber() {
            return deliveryNumber;
        }

        public void setDeliveryNumber(BigDecimal deliveryNumber) {
            this.deliveryNumber = deliveryNumber;
        }

        public BigDecimal getDeliveryNoNumber() {
            return deliveryNoNumber;
        }

        public void setDeliveryNoNumber(BigDecimal deliveryNoNumber) {
            this.deliveryNoNumber = deliveryNoNumber;
        }
    }

    public String getProductNames() {
        return productNames;
    }

    public void setProductNames(String productNames) {
        this.productNames = productNames;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public List<ExcelDetail> getExcelDetailList() {
        return excelDetailList;
    }

    public void setExcelDetailList(List<ExcelDetail> excelDetailList) {
        this.excelDetailList = excelDetailList;
    }

    public BusinessDeliveryExcelVo() {
    }
}
