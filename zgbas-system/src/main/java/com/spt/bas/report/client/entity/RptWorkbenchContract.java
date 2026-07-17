package com.spt.bas.report.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 业务经理工作台合同数据
 */
@Data
public class RptWorkbenchContract {

    /**
     * 合同ID
     */
    private Long id;

    /**
     * 预算审批编号
     */
    private Long approveId;
    
    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同类型
     */
    private String contractType;
    
    /**
     * 特殊链条合同标识
     */
    private Boolean specialChainFlag;
    
    /**
     * 对方企业ID
     */
    private Long companyId;

    /**
     * 对方企业名称
     */
    private String companyName;

    /**
     * 我方企业名称
     */
    private String ourCompanyName;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 合同状态
     * 采购：N-新增，A-审批中，S-已签约(已盖章)，F1-已付款，G1-已收货，V1-已收票，D-完成，B-已审批，C-作废 W-等待
     * 销售：N-新增，A-审批中，S-已签约(已盖章)，F2-已收款，G2-已发货，V2-已开票，D-完成，B-已审批，C-作废 W-等待
     */
    private String contractStatus;
    

    /**
     * 约定收付全款日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date appointPayFullTime;

    /**
     * 业务员ID
     */
    private Long matchUserId;

    /**
     * 业务员名称
     */
    private String matchUserName;

    /**
     * 合同总价
     */
    private BigDecimal totalAmount;

    /**
     * 合同数量
     */
    private BigDecimal totalNumber;
    
    /**
     * 已开票已收票金额
     */
    private BigDecimal billedAmount = BigDecimal.ZERO;

    /**
     * 已收付款金额
     */
    private BigDecimal dealedAmount = BigDecimal.ZERO;

    /**
     * 实际已入\出库数量
     */
    private BigDecimal warehouseNumber = BigDecimal.ZERO;

    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 是否已通过盖章审核
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean sealFlg = false;

    /**
     * 双签日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date sealDate;

    /**
     * 逾期天数
     */
    private Long breachDays;

    /**
     * 账期
     */
    private Long creditCycle;

    /**
     * 逾期罚息
     */
    private BigDecimal breachAmount = BigDecimal.ZERO;

    /**
     * 已收逾期罚息
     */
    private BigDecimal receiveBreachAmount = BigDecimal.ZERO;

    /**
     * 未收罚息
     */
    private BigDecimal noReceiveBreachAmount = BigDecimal.ZERO;

    /**
     * 未收金额合计（未收本金+未收违约金）
     */
    private BigDecimal noReceiveTotalAmount = BigDecimal.ZERO;

    /**
     * 背靠背赊销标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean matchCreditFlg = false;

    /**
     * 合同时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date contractTime;

    /**
     * 开票审批ID
     */
    private Long billedApproveId;

    /**
     * 采购合同总价 (订单执行用于能否开票判断)
     */
    private BigDecimal buyTotalAmount;

    /**
     * 采购已收票金额 (订单执行用于能否开票判断)
     */
    private BigDecimal buyBilledAmount = BigDecimal.ZERO;

    /**
     * 履约状态 N：即将到期；B：宽期限；D：催告期；S：逾期；P：诉讼；
     */
    private String performanceStatus;
    /**
     * 业务类型
     */
    private String businessKind;
    /**
     * 最终局决算价
     */
    private BigDecimal finalTotalAmount = BigDecimal.ZERO;
    /**
     * 确认收货数量
     */
    private BigDecimal confirmReceiveNumber = BigDecimal.ZERO;
    /**
     * 是否货到票到
     */
    private Boolean receiptArrivedFlg;
    /**
     * 是否发起作废申请
     */
    private Boolean applyCancelFlg;
    /**
     * 货名
     */
    private String productsName;

    /**
     * 虚拟类型
     * KC-库存采购
     * XY-协议采购
     */
    private String virtualType;
    
    /**
     * 虚拟库存采购ID
     */
    private Long virtualContractId;

    /**
     * 库存合同号
     */
    private String virtualContractNo;

}
