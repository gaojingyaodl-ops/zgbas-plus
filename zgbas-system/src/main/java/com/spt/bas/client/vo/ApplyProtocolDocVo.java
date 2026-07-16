package com.spt.bas.client.vo;

import com.spt.bas.client.vo.protocol.DzdAgreement;
import com.spt.bas.client.vo.protocol.ReminderPaymentAgreement;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyProtocolDocVo {

    private String companyName;

    /**
     * 未付金额
     */
    private BigDecimal unPayOverdueAmount;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount = BigDecimal.ZERO;

    /**
     * 合计逾期金额 （unPayAmount + breachAmount）
     * 495330.00元（合同本金）+5543.54元（逾期罚息）
     */
    private String overdueAmountSum;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 账号
     */
    private String bankAccount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 明细详情
     */
    private String detailList;

    private List<ApplyProtocolDocCkhDetailVo> ckhDetailList;
    private List<ApplyProtocolDocZnjDetailVo> znjDetailList;
    private List<ReminderPaymentAgreement> rePaymentList;

    /**
     * 截止日期
     */
    private String endDate;

    /**
     * 盖章日期
     */
    private String signDate;

    /**
     * 逾期滞纳金合计
     */
    private String overdueLateFeesSum;

    /**
     * 回款账户
     */
    private String paymentAccount;

    /**
     * 应收款金额合计
     */
    private BigDecimal totalAmountPayable;
}
