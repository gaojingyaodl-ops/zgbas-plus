package com.spt.bas.client.vo.protocol;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.tools.core.date.DateOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 文件协议-对账单
 *
 * @Author MoonLight
 * @Date 2024/5/28 11:37
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DzdAgreement {
    
    private String signCompanyName;

    /**
     * 客户名称
     */
    private String dzdCompanyName;

    /**
     * 客户联系人
     */
    private String dzdCompanyContact;

    /**
     * 供应商名称
     */
    private String ourCompanyName;

    /**
     * 供应商对账人
     */
    private String ourCompanyContact;

    /**
     * 对账日期-开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dzDateBegin;
    
    /**
     * 对账日期-开始
     */
    public String dzDateBeginStr;
    
    /**
     * 对账日期-结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date dzDateEnd;
    
    /**
     * 对账日期-结束
     */
    public String dzDateEndStr;
    
    /**
     * 截止日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date cutOffDate;

    /**
     * 合同总金额
     */
    private BigDecimal totalAmountSum;

    /**
     * 合同总金额大写
     */
    private String totalAmountSumCN;

    /**
     * 未付金额
     */
    private BigDecimal needReceiveAmountSum;

    /**
     * 未付金额大写
     */
    private String needReceiveAmountSumCN;

    /**
     * 银行账户名称
     */
    private String dzdBankAccountName;
    
    /**
     * 开户行名称
     */
    private String dzdBankName;
    
    /**
     * 银行账户号码
     */
    private String dzdBankAccountNo;
    
    /**
     * 合同明细列表
     */
    private List<DzdAgreementDetail> dzdDetailList;

    private String dzdDetailListStr;

    @Data
    public static class DzdAgreementDetail {

        /**
         * 合同编号
         */
        private String contractNo;

        /**
         * 产品
         */
        private String productsName;

        /**
         * 合同数量
         */
        private BigDecimal totalNumber;
        
        /**
         * 合同单价
         */
        private BigDecimal dealPrice;

        /**
         * 合同金额
         */
        private BigDecimal totalAmount;

        /**
         * 已付金额
         */
        private BigDecimal dealedAmount;

        /**
         * 未付金额
         */
        private BigDecimal needReceiveAmount;

        /**
         * 逾期罚息金额
         */
        private BigDecimal breachAmount;

        /**
         * 发货日期
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
        private Date shippingDate;
        
        /**
         * 发货日期
         */
        public String shippingDateStr;

        /**
         * 收货确认日期
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
        private Date confirmDate;
        
        /**
         * 收货确认日期
         */
        public String confirmDateStr;

        public DzdAgreementDetail() {
        }
        
        public String getShippingDateStr() {
            if (this.shippingDate == null) {
                return "";
            }
            return DateOperator.formatDate(shippingDate, "yyyy-MM-dd");
        }

        public String getConfirmDateStr() {
            if (this.confirmDate == null) {
                return "";
            }
            return DateOperator.formatDate(confirmDate, "yyyy-MM-dd");
        }
    }

    public String getDzDateBeginStr() {
        if (this.dzDateBegin == null) {
            return "";
        }
        return DateOperator.formatDate(dzDateBegin, "yyyy年MM月dd日");
    }
    
    public String getDzDateEndStr() {
        if (this.dzDateEnd == null) {
            return "";
        }
        return DateOperator.formatDate(dzDateEnd, "yyyy年MM月dd日");
    }

    public List<DzdAgreementDetail> getDzdDetailList() {
        return StringUtils.isNotBlank(dzdDetailListStr) ? JSONUtil.toList(dzdDetailListStr, DzdAgreement.DzdAgreementDetail.class) : dzdDetailList;
    }

    public void setDzdDetailList(List<DzdAgreementDetail> detailList) {
        this.dzdDetailList = StringUtils.isNotBlank(dzdDetailListStr) ? JSONUtil.toList(dzdDetailListStr, DzdAgreement.DzdAgreementDetail.class) : detailList;
    }

    public BigDecimal getNeedReceiveAmountSum() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<DzdAgreementDetail> detailList = new ArrayList<>();
        if (StringUtils.isNotBlank(dzdDetailListStr)) {
            detailList = JSONUtil.toList(dzdDetailListStr, DzdAgreementDetail.class);
        }
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (DzdAgreementDetail detail : detailList) {
                BigDecimal needReceiveAmount = detail.getNeedReceiveAmount();
                if (needReceiveAmount == null) {
                    needReceiveAmount = BigDecimal.ZERO;
                }
                totalAmount = totalAmount.add(needReceiveAmount);
            }
        }
        return totalAmount;
    }
    

}
