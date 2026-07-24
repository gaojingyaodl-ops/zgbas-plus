package com.spt.bas.purchase.wx.server.enums;

import com.spt.bas.purchase.wx.client.entity.BuyMessage;
import com.spt.bas.purchase.wx.server.util.UserHelper;
import com.spt.tools.core.json.JsonUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 微信消息枚举类
 *
 * @author 杨英承
 * @version 1.0.0
 * @date 2024/1/4 09:47
 */

public enum MessageEnums {

    /**
     * 报价消息
     */
    QUOTED_MESSAGE {
        /**
         * 构建报价消息实现
         *
         * @param quoteMessage 报价消息json
         * @return 结果
         */
        public BuyMessage buildQuotedMessage(QuoteMessage quoteMessage, String openId) {
            BuyMessage result = new BuyMessage();
            result.setMessageContent(JsonUtil.obj2Json(quoteMessage));
            result.setOpenId(openId);
            Date now = new Date();
            result.setMessageType(MESSAGE_TYPE_B);
            result.setSourceBy(SOURCE_BY_B);
            result.setReadFlag(READ_FLAG_0);
            result.setCreatedBy(UserHelper.getCurUserId());
            result.setUpdatedBy(UserHelper.getCurUserId());
            result.setCreatedDate(now);
            result.setUpdatedDate(now);
            return result;
        }
    },

    /**
     * 成交消息
     */
    DEAL_MESSAGE {
        public BuyMessage buildDealMessage(DealMessage dealMessage, String openId) {
            BuyMessage result = new BuyMessage();
            result.setOpenId(openId);
            result.setMessageContent(JsonUtil.obj2Json(dealMessage));
            Date now = new Date();
            result.setCreatedBy(UserHelper.getCurUserId());
            result.setUpdatedBy(UserHelper.getCurUserId());
            result.setCreatedDate(now);
            result.setUpdatedDate(now);
            result.setMessageType(MESSAGE_TYPE_C);
            result.setSourceBy(SOURCE_BY_B);
            result.setReadFlag(READ_FLAG_0);
            return result;
        }
    },
    /**
     * 询价消息
     */
    ENQUIRY_MESSAGE {
        public BuyMessage buildEnquiryMessage(EnquiryMessage enquiryMessage, String openId) {
            BuyMessage result = new BuyMessage();
            result.setOpenId(openId);
            result.setMessageContent(JsonUtil.obj2Json(enquiryMessage));
            Date now = new Date();
            result.setSourceBy(MESSAGE_TYPE_B);
            result.setMessageType(MESSAGE_TYPE_X);
            result.setReadFlag(READ_FLAG_0);
            result.setCreatedBy(UserHelper.getCurUserId());
            result.setUpdatedBy(UserHelper.getCurUserId());
            result.setCreatedDate(now);
            result.setUpdatedDate(now);
            return result;
        }
    },

    /**
     * 系统消息
     */
    SYSTEM_MESSAGE {
        public BuyMessage buildSystemMessage(SystemMessage systemMessage, String openId) {
            BuyMessage result = new BuyMessage();
            result.setOpenId(openId);
            result.setMessageContent(JsonUtil.obj2Json(systemMessage));
            Date now = new Date();
            result.setMessageType(MESSAGE_TYPE_S);
            result.setReadFlag(READ_FLAG_0);
            result.setCreatedDate(now);
            result.setCreatedBy(-1L);// -1均为系统创建
            result.setUpdatedBy(-1L);// -1均为系统创建
            result.setUpdatedDate(now);
            return result;
        }
    };

    /**
     * 成交消息
     */
    public final static String MESSAGE_TYPE_C = "C";

    /**
     * 报价消息
     */
    public final static String MESSAGE_TYPE_B = "B";

    /**
     * 系统消息
     */
    public final static String MESSAGE_TYPE_S = "S";

    /**
     * 询价消息
     */
    public final static String MESSAGE_TYPE_X = "X";

    /**
     * 老审批
     */
    public final static String SOURCE_BY_Z = "Z";

    /**
     * 企业微信
     */
    public final static String SOURCE_BY_B = "B";

    /**
     * 未读消息
     */
    private final static String READ_FLAG_0 = "0";

    /**
     * 已读消息
     */
    private final static String READ_FLAG_1 = "1";


    public BuyMessage buildQuotedMessage(QuoteMessage quoteMessage, String openId) {
        throw new AbstractMethodError();
    }

    public BuyMessage buildDealMessage(DealMessage dealMessage, String openId) {
        throw new AbstractMethodError();
    }

    public BuyMessage buildEnquiryMessage(EnquiryMessage enquiryMessage, String openId) {
        throw new AbstractMethodError();
    }

    /**
     * 构建系统消息
     *
     * @param systemMessage 系统消息
     * @return 结果
     */
    public BuyMessage buildSystemMessage(SystemMessage systemMessage, String openId) {
        throw new AbstractMethodError();
    }

    /**
     * 报价消息实体
     */
    public static class QuoteMessage {

        private Long quoteId;

        /**
         * 产品
         */
        private String productName;

        /**
         * 销售价
         */
        private BigDecimal sellPrice;

        /**
         * 账期
         */
        private Integer paymentDays;

        /**
         * 送到日期
         */
        private Date arriveDate;

        /**
         * 失效日期
         */
        private Date expireTime;

        /**
         * 备注
         */
        private String remark;

        @Override
        public String toString() {
            return "QuoteMessage{" +
                    "quoteId=" + quoteId +
                    ", productName='" + productName + '\'' +
                    ", sellPrice=" + sellPrice +
                    ", paymentDays=" + paymentDays +
                    ", arriveDate=" + arriveDate +
                    ", expireTime=" + expireTime +
                    ", remark='" + remark + '\'' +
                    '}';
        }

        private QuoteMessage(Builder builder) {
            setProductName(builder.productName);
            setSellPrice(builder.sellPrice);
            setPaymentDays(builder.paymentDays);
            setArriveDate(builder.arriveDate);
            setExpireTime(builder.expireTime);
            setRemark(builder.remark);
        }


        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(BigDecimal sellPrice) {
            this.sellPrice = sellPrice;
        }

        public Integer getPaymentDays() {
            return paymentDays;
        }

        public void setPaymentDays(Integer paymentDays) {
            this.paymentDays = paymentDays;
        }

        public Date getArriveDate() {
            return arriveDate;
        }

        public void setArriveDate(Date arriveDate) {
            this.arriveDate = arriveDate;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public static final class Builder {
            private String productName;
            private BigDecimal sellPrice;
            private Integer paymentDays;
            private Date arriveDate;
            private Date expireTime;
            private String remark;

            public Builder() {
            }

            public Builder productName(String val) {
                productName = val;
                return this;
            }

            public Builder sellPrice(BigDecimal val) {
                sellPrice = val;
                return this;
            }

            public Builder paymentDays(Integer val) {
                paymentDays = val;
                return this;
            }

            public Builder arriveDate(Date val) {
                arriveDate = val;
                return this;
            }

            public Builder expireTime(Date val) {
                expireTime = val;
                return this;
            }

            public Builder remark(String val) {
                remark = val;
                return this;
            }

            public QuoteMessage build() {
                return new QuoteMessage(this);
            }
        }
    }

    /**
     * 成交消息实体
     */
    public static class DealMessage {
        /**
         * 询价单号
         */
        private String enquiryOddNumber;
        /**
         * 报价单号
         */
        private String oddNumber;
        /**
         * 询价ID
         */
        private Long enquiryId;
        /**
         * 产品
         */
        private String productName;

        /**
         * 销售价
         */
        private BigDecimal sellPrice;

        /**
         * 数量
         */
        private BigDecimal dealNumber;

        /**
         * 账期
         */
        private Integer paymentDays;

        /**
         * 送到日期
         */
        private Date arriveDate;

        /**
         * 确定日期
         */
        private Date confirmTime;

        /**
         * 备注
         */
        private String remark;

        @Override
        public String toString() {
            return "DealMessage{" +
                    "enquiryId=" + enquiryId +
                    ", productName='" + productName + '\'' +
                    ", sellPrice=" + sellPrice +
                    ", dealNumber=" + dealNumber +
                    ", paymentDays=" + paymentDays +
                    ", arriveDate=" + arriveDate +
                    ", confirmTime=" + confirmTime +
                    ", remark='" + remark + '\'' +
                    '}';
        }

        private DealMessage(Builder builder) {
            setEnquiryOddNumber(builder.enquiryOddNumber);
            setOddNumber(builder.oddNumber);
            setEnquiryId(builder.enquiryId);
            setProductName(builder.productName);
            setSellPrice(builder.sellPrice);
            setDealNumber(builder.dealNumber);
            setPaymentDays(builder.paymentDays);
            setArriveDate(builder.arriveDate);
            setConfirmTime(builder.confirmTime);
            setRemark(builder.remark);
        }
        public String getEnquiryOddNumber() {
            return enquiryOddNumber;
        }

        public void setEnquiryOddNumber(String enquiryOddNumber) {
            this.enquiryOddNumber = enquiryOddNumber;
        }
        public String getOddNumber() {
            return oddNumber;
        }

        public void setOddNumber(String oddNumber) {
            this.oddNumber = oddNumber;
        }

        public Long getEnquiryId() {
            return enquiryId;
        }

        public void setEnquiryId(Long enquiryId) {
            this.enquiryId = enquiryId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(BigDecimal sellPrice) {
            this.sellPrice = sellPrice;
        }

        public BigDecimal getDealNumber() {
            return dealNumber;
        }

        public void setDealNumber(BigDecimal dealNumber) {
            this.dealNumber = dealNumber;
        }

        public Integer getPaymentDays() {
            return paymentDays;
        }

        public void setPaymentDays(Integer paymentDays) {
            this.paymentDays = paymentDays;
        }

        public Date getArriveDate() {
            return arriveDate;
        }

        public void setArriveDate(Date arriveDate) {
            this.arriveDate = arriveDate;
        }

        public Date getConfirmTime() {
            return confirmTime;
        }

        public void setConfirmTime(Date confirmTime) {
            this.confirmTime = confirmTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public static final class Builder {
            private String enquiryOddNumber;
            private String oddNumber;
            private Long enquiryId;
            private String productName;
            private BigDecimal sellPrice;
            private BigDecimal dealNumber;
            private Integer paymentDays;
            private Date arriveDate;
            private Date confirmTime;
            private String remark;

            public Builder() {
            }
            public Builder enquiryOddNumber(String val) {
                enquiryOddNumber = val;
                return this;
            }
            public Builder oddNumber(String val) {
                oddNumber = val;
                return this;
            }

            public Builder enquiryId(Long val) {
                enquiryId = val;
                return this;
            }

            public Builder productName(String val) {
                productName = val;
                return this;
            }

            public Builder sellPrice(BigDecimal val) {
                sellPrice = val;
                return this;
            }

            public Builder dealNumber(BigDecimal val) {
                dealNumber = val;
                return this;
            }

            public Builder paymentDays(Integer val) {
                paymentDays = val;
                return this;
            }

            public Builder arriveDate(Date val) {
                arriveDate = val;
                return this;
            }

            public Builder confirmTime(Date val) {
                confirmTime = val;
                return this;
            }

            public Builder remark(String val) {
                remark = val;
                return this;
            }

            public DealMessage build() {
                return new DealMessage(this);
            }
        }
    }

    /**
     * 系统消息实体
     */
    public static class SystemMessage {
        /**
         * 系统消息
         */
        private String systemMessage;

        /**
         * 备注
         */
        private String remark;

        private SystemMessage(Builder builder) {
            setSystemMessage(builder.systemMessage);
            setRemark(builder.remark);
        }


        public String getSystemMessage() {
            return systemMessage;
        }

        public void setSystemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public static final class Builder {
            private String systemMessage;
            private String remark;

            public Builder() {
            }

            public Builder systemMessage(String val) {
                systemMessage = val;
                return this;
            }

            public Builder remark(String val) {
                remark = val;
                return this;
            }

            public SystemMessage build() {
                return new SystemMessage(this);
            }
        }
    }

    /**
     * 询价消息实体
     */
    public static class EnquiryMessage {
        /**
         * 单号
         */
        private String oddNumber;

        private Long enquiryId;
        /**
         * 产品
         */
        private String productName;

        /**
         * 数量
         */
        private BigDecimal dealNumber;

        /**
         * 账期
         */
        private Integer paymentDays;

        /**
         * 送到日期
         */
        private Date arriveDate;

        /**
         * 失效日期
         */
        private Date expireTime;

        /**
         * 备注
         */
        private String remark;

        @Override
        public String toString() {
            return "EnquiryMessage{" +
                    "enquiryId=" + enquiryId +
                    ", productName='" + productName + '\'' +
                    ", dealNumber=" + dealNumber +
                    ", paymentDays=" + paymentDays +
                    ", arriveDate=" + arriveDate +
                    ", expireTime=" + expireTime +
                    ", remark='" + remark + '\'' +
                    '}';
        }

        private EnquiryMessage(Builder builder) {
            setOddNumber(builder.oddNumber);
            setEnquiryId(builder.enquiryId);
            setProductName(builder.productName);
            setDealNumber(builder.dealNumber);
            setPaymentDays(builder.paymentDays);
            setArriveDate(builder.arriveDate);
            setExpireTime(builder.expireTime);
            setRemark(builder.remark);
        }

        public String getOddNumber() {
            return oddNumber;
        }

        public void setOddNumber(String oddNumber) {
            this.oddNumber = oddNumber;
        }

        public Long getEnquiryId() {
            return enquiryId;
        }

        public void setEnquiryId(Long enquiryId) {
            this.enquiryId = enquiryId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public BigDecimal getDealNumber() {
            return dealNumber;
        }

        public void setDealNumber(BigDecimal dealNumber) {
            this.dealNumber = dealNumber;
        }

        public Integer getPaymentDays() {
            return paymentDays;
        }

        public void setPaymentDays(Integer paymentDays) {
            this.paymentDays = paymentDays;
        }

        public Date getArriveDate() {
            return arriveDate;
        }

        public void setArriveDate(Date arriveDate) {
            this.arriveDate = arriveDate;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public static final class Builder {
            private String oddNumber;
            private Long enquiryId;
            private String productName;
            private BigDecimal dealNumber;
            private Integer paymentDays;
            private Date arriveDate;
            private Date expireTime;
            private String remark;

            public Builder() {
            }

            public Builder odderNumber(String val) {
                oddNumber = val;
                return this;
            }

            public Builder enquiryId(Long val) {
                enquiryId = val;
                return this;
            }

            public Builder productName(String val) {
                productName = val;
                return this;
            }

            public Builder dealNumber(BigDecimal val) {
                dealNumber = val;
                return this;
            }

            public Builder paymentDays(Integer val) {
                paymentDays = val;
                return this;
            }

            public Builder arriveDate(Date val) {
                arriveDate = val;
                return this;
            }

            public Builder expireTime(Date val) {
                expireTime = val;
                return this;
            }

            public Builder remark(String val) {
                remark = val;
                return this;
            }

            public EnquiryMessage build() {
                return new EnquiryMessage(this);
            }
        }
    }


}
