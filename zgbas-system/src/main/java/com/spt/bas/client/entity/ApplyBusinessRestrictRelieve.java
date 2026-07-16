package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 业务限制解除申请单
 */
@Data
@Entity
@Table(name = "t_apply_business_restrict_relieve")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ApplyBusinessRestrictRelieve extends IdEntity implements IPmEntity {

    private static final long serialVersionUID = -2823344963914806824L;

    /**
     * 审批ID
     */
    private Long approveId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 企业ID
     */
    private Long companyId;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 人保批复额度
     */
    private BigDecimal piccCreditAmount;

    /**
     * 大地额度
     */
    private BigDecimal daDiCreditAmount;

    /**
     * 已用额度
     */
    private BigDecimal usedCreditAmount;

    /**
     * 是否访厂
     */
    private Boolean accessReportFlg;

    /**
     * 是否签署实控人连带
     */
    private Boolean actualGuaranteeFlg;

    /**
     * 新单金额
     */
    private BigDecimal newCreditAmount;

    /**
     * 新单账期天数
     */
    private Integer newCreditDays;

    /**
     * 新单上游付款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date newBuyPayDate;

    /**
     * 是否逾期
     */
    private Boolean overdueFlg;

    /**
     * 逾期金额合计
     */
    private BigDecimal overdueAmountTotal;

    /**
     * 逾期本金
     */
    private BigDecimal overduePrincipal;

    /**
     * 逾期单数
     */
    private Integer overdueCount;

    /**
     * 预计逾期回款日
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date expectOverduePayDate;

    /**
     * 逾期原因
     */
    private String overdueReason;

    /**
     * 新单是否超额
     */
    private Boolean newExcessFlg;

    /**
     * 新单超额金额
     */
    private BigDecimal newExcessAmount;

    /**
     * 新单超额比例
     */
    private String newExcessRate;
    
    /**
     * 超额比例
     */
    private BigDecimal excessRate = BigDecimal.ZERO;

    /**
     * 新单毛利率不达万七
     */
    private Boolean newProfitRateLack;

    /**
     * 本单实际毛利率
     */
    private String newRealProfitRate;

    /**
     * 申请理由
     */
    private String applyReason;

    /**
     * 附件
     */
    private String fileId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属区域
     */
    private String ownRegion;

    /**
     * 人保可用额度
     */
    private BigDecimal piccAmount;

    /**
     * 大地可用额度
     */
    private BigDecimal daDiAmount;

    /**
     * 中银可用额度
     */
    private BigDecimal zhongYinAmount;

    /**
     * 人保风控额度
     */
    private BigDecimal piccRiskAmount;

    /**
     * 大地风控额度
     */
    private BigDecimal daDiRiskAmount;
    
    /**
     * 中银风控额度
     */
    private BigDecimal zhongYinRiskAmount;

    /**
     * 人保临时额度
     */
    private BigDecimal piccTemporaryAmount;

    /**
     * 大地临时额度
     */
    private BigDecimal daDiTemporaryAmount;

    /**
     * 中银临时额度
     */
    private BigDecimal zhongYinTemporaryAmount;
    
    /**
     * 是否逾期
     */
    private Boolean relieveOverdue;

    /**
     * 是否3天未发货
     */
    private Boolean relieveNotDeliver;

    /**
     * 授信类别
     */
    private String creditType;

}
