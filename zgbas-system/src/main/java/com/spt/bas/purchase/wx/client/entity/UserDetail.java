package com.spt.bas.purchase.wx.client.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-09-23 15:12
 */
@Entity
@Table(name = "t_wx_company_user_detail")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicInsert
@DynamicUpdate
@LogEntityName("用户信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetail extends IdEntity {
    private static final long serialVersionUID = 3619471799202811123L;

    /**
     * 关联表wx_company_user主键
     */
    private Long userId;

    /**
     * 公司id
     */
    private Long companyId;

    /**
     * 信息完善步骤
     */
    private Integer infoStep;

    private Boolean enableFlg;

    /**
     * 量身定制公司类型 0：贸易商 1：终端工厂
     */
    private String customCompanyType;

    /**
     * 量身定制企业类型：0：基础化工 1：通用塑料
     */
    private String customCompanySource;

    /**
     * 量身定制 我的角色 0：企业主 1：采购经理 2：业务员
     */
    private String customMyRole;

    /**
     * 自定义额度
     * 0：30万以内
     * 1：30到100万
     * 2：大于100万
     */
    private String customQuota;

    /**
     * 自定义还款周期
     * 0：15天
     * 1：30天
     * 2：60天
     */
    private String customRepaymentPeriod;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 企业信息提交状态
     * 状态说明：
     *
     * 0：未开始
     *
     * 1：审批中
     *
     * 2：未确认
     *
     * 3：审批驳回
     *
     * 4：完成
     */
    private String companyApplyStatus;

    /**
     * 委托授权状态
     * 状态说明：同上
     */
    private String entrustApplyStatus;

    /**
     * 入金状态
     * 状态说明：同上
     */
    private String depositStatus;

    /**
     * 申请白条
     * 状态说明：同上
     */
    private String applyIouStatus;

    /**
     * 白条服务费
     * 状态说明：同上
     */
    private String serviceFeeForIouStatus;

    /**
     * cfca平台审核状态
     * 状态说明：同上
     */
    private String cfcaApprovedStatus;

    /**
     * cfca费用支付状态
     * 状态说明：同上
     */
    private String cfcaPayFeeStatus;

    /**
     * 合伙人的申请状态
     * 状态说明：同上
     */
    private String partnerApplyStatus;

    /**
     * 额度测试状态
     * 转态说明：同上
     */
    private String quotaTestStatus;

    /**
     * 是否已绑定企业
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean isBind;

    /**
     * 已付入金金额 一共3位，保留2位小数
     */
    private BigDecimal paidDepositPrice;
    /**
     * 应付入金金额 一共3位，保留2位小数
     */
    private BigDecimal totalDepositPrice;

    /**
     * 已付服务费金额
     */
    private BigDecimal paidServiceFeePrice;

    /**
     * 应付服务费金额
     */
    private BigDecimal totalServiceFeePrice;

    /**
     * 已付cfca服务费
     */
    private BigDecimal paidCfcaFeePrice;

    /**
     * 应付cfca服务费金额
     */
    private BigDecimal totalCfcaFeePrice;

    /**
     * 白条额度
     */
    private BigDecimal totalCreditAmount;

    /**
     * 已使用白条额度
     */
    private BigDecimal usedCreditAmount;

    /**
     * 可使用白条额度
     */
    private BigDecimal availableCreditAmount;

    /**
     * 期货额度
     */
    private BigDecimal totalFuturesAmount;

    /**
     * 已使用期货额度
     */
    private BigDecimal usedFuturesAmount;

    /**
     * 可使用期货额度
     */
    private BigDecimal availableFuturesAmount;

    /**
     * 现货额度
     */
    private BigDecimal totalSpotAmount;

    /**
     * 已使用现货额度
     */
    private BigDecimal usedSpotAmount;

    /**
     * 可使用现货额度
     */
    private BigDecimal availableSpotAmount;

    /**
     * 合伙人推荐码
     */
    private String partnerCode;

    /**
     * 委托授权是否确认
     */
    private String entrustConfirm;

}
