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
 * 文件协议-还款协议
 *
 * @Author MoonLight
 * @Date 2024/5/28 11:37
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentAgreement {

    /**
     * 甲方
     */
    private String ourCompanyName;

    /**
     * 甲方统一信用代码
     */
    private String ourCompanyNo;

    /**
     * 乙方
     */
    private String targetCompanyName;

    /**
     * 乙方统一信用代码
     */
    private String targetCompanyNameNo;

    /**
     * 丙方
     */
    private String party;

    /**
     * 丙方身份证号
     */
    private String partyCardNo;

    /**
     * 丙方联系电话
     */
    private String partyPhone;

    /**
     * 债务日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date agreementDate;

    /**
     * 债务日期-年
     */
    private String agreementYear;

    /**
     * 债务日期-月
     */
    private String agreementMonth;

    /**
     * 债务日期-日
     */
    private String agreementDay;

    /**
     * 截止日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date cutOffDate;

    /**
     * 债务日期-年
     */
    private String cutOffYear;

    /**
     * 债务日期-月
     */
    private String cutOffMonth;

    /**
     * 债务日期-日
     */
    private String cutOffDay;

    /**
     * 债务金额大写
     */
    private String agreementAmountCN;

    /**
     * 债务金额
     */
    private BigDecimal agreementAmount;

    /**
     * 合同明细列表
     */
    private List<RepaymentAgreementDetail> repaymentDetailList;

    private String repaymentDetailListStr;

    private BigDecimal needReceiveTotalAmount;
    private BigDecimal breachTotalAmount;

    @Data
    public static class RepaymentAgreementDetail {

        /**
         * 合同编号
         */
        private String contractNo;

        /**
         * 合同金额
         */
        private BigDecimal totalAmount;

        /**
         * 已回款金额
         */
        private BigDecimal dealedAmount;

        /**
         * 待回款金额
         */
        private BigDecimal needReceiveAmount;

        /**
         * 逾期罚息金额
         */
        private BigDecimal breachAmount;

        public RepaymentAgreementDetail() {
        }
    }

    public String getAgreementYear() {
        return Objects.nonNull(agreementDate) ? String.valueOf(DateOperator.getYear(agreementDate)) : "";
    }

    public String getAgreementMonth() {
        return Objects.nonNull(agreementDate) ? String.valueOf(DateOperator.getMonth11(agreementDate)+1) : "";
    }

    public String getAgreementDay() {
        return Objects.nonNull(agreementDate) ? String.valueOf(DateOperator.getDay(agreementDate)) : "";
    }

    public String getCutOffYear() {
        return Objects.nonNull(cutOffDate) ? String.valueOf(DateOperator.getYear(cutOffDate)) : "";
    }

    public String getCutOffMonth() {
        return Objects.nonNull(cutOffDate) ? String.valueOf(DateOperator.getMonth11(cutOffDate)+1) : "";
    }

    public String getCutOffDay() {
        return Objects.nonNull(cutOffDate) ? String.valueOf(DateOperator.getDay(cutOffDate)) : "";
    }

    public List<RepaymentAgreementDetail> getDetailList() {
        return StringUtils.isNotBlank(repaymentDetailListStr) ? JSONUtil.toList(repaymentDetailListStr, RepaymentAgreement.RepaymentAgreementDetail.class) : repaymentDetailList;
    }

    public void setDetailList(List<RepaymentAgreementDetail> detailList) {
        this.repaymentDetailList = StringUtils.isNotBlank(repaymentDetailListStr) ? JSONUtil.toList(repaymentDetailListStr, RepaymentAgreement.RepaymentAgreementDetail.class) : detailList;
    }

    public BigDecimal getNeedReceiveTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<RepaymentAgreementDetail> detailList = new ArrayList<>();
        if (StringUtils.isNotBlank(repaymentDetailListStr)) {
            detailList = JSONUtil.toList(repaymentDetailListStr, RepaymentAgreementDetail.class);
        }
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (RepaymentAgreementDetail detail : detailList) {
                BigDecimal needReceiveAmount = detail.getNeedReceiveAmount();
                if (needReceiveAmount == null) {
                    needReceiveAmount = BigDecimal.ZERO;
                }
                totalAmount = totalAmount.add(needReceiveAmount);
            }
        }
        return totalAmount;
    }

    public BigDecimal getBreachTotalAmount() {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<RepaymentAgreementDetail> detailList = new ArrayList<>();
        if (StringUtils.isNotBlank(repaymentDetailListStr)) {
            detailList = JSONUtil.toList(repaymentDetailListStr, RepaymentAgreementDetail.class);
        }
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (RepaymentAgreementDetail detail : detailList) {
                BigDecimal breachAmount = detail.getBreachAmount();
                if (breachAmount == null) {
                    breachAmount = BigDecimal.ZERO;
                }
                totalAmount = totalAmount.add(breachAmount);
            }
        }
        return totalAmount;
    }

}
