package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.bas.client.constant.BasConstants;
import com.spt.tools.core.annotation.LogEntityName;
import com.spt.tools.core.annotation.LogField;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 企业信息
 */
@Entity
@Table(name = "t_bs_company")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@LogEntityName("企业管理")
@DynamicUpdate
@DynamicInsert
public class BsCompany extends IdEntity {

    private static final long serialVersionUID = 4598929051349290152L;
    @LogField("企业名称")
    private String companyName;// 企业名称
//    @LogField("税号")
    private String taxNo; // 税号
    @LogField("开户银行")
    private String bankName; // 开户银行
    @LogField("公司电话")
    private String companyPhone; // 公司电话
    @LogField("银行账号")
    private String bankAccount; // 银行账号
    @LogField("联系人")
    private String contactName; // 联系人
    @LogField("联系电话")
    private String contactPhone; // 联系电话
    @LogField("状态,dict,companyStatus,admin")
    private String status = BasConstants.COMPANY_STATUS_N; // 状态：N-新增、F-已跟进、D-已合作
    @LogField("营业执照附件Id")
    private String fileId; // 上传企业营业执照id,多个附件Id
    private Long createUserId;//创建人id
    @LogField("领用人,user")
    private Long matchUserId;//领用人
    private Long matchUserDeptId;//领用人

    /**
     * 所属部门：有领用人时为领用人部门ID，无领用人时为开户人部门ID，两者都没则为空
     */
    private Long deptId;
    private String matchUserName;
    private Date matchFllowDate;//私海用户，则要输入跟进时间(领用时间)
    private Long enterpriseId;
    private Long ownerOfAccountId; //开户人id
    //关联查询使用：我审批过
    private List<BsCompanyShare> companyShares;
    @LogField("客户分类,dict,bsCompanyType,admin")
    private String companyType;//客户分类  I-工业客户  T-贸易商 P-上游石化
    @LogField("客户区域")
    private String companyArea;//客户区域
    @LogField("信用等级,dict,creditRating,admin")
    private String creditRating; //信用等级 W-白名单 G-灰名单 B-黑名单
    @LogField("员工人数")
    private Long stafferNumber;//员工人数
    @LogField("网址")
    private String companyUrl;//网址
    @LogField("是否有效")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;// 是否有效
    @LogField("邮箱")
    private String email;//邮箱
    @LogField("传真")
    private String companyFax;//传真
    @LogField("注册资金")
    private String registerCapital;//注册资金
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date registerDate;// 注册时间
    @LogField("法人代表")
    private String legalRepresent;//法人代表
    @LogField("公司地址")
    private String address; // 公司地址
    private String companyCreditNo;//企业信用代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean riskFlg;//企业是否存在风险标识

    private Long assignedUserId;//指派人
    @LogField("指派人")
    private String assignedUserName;
    @LogField("总授信额度")
    private BigDecimal totalCreditAmount = BigDecimal.ZERO;//总授信额度
    @LogField("额度上浮之前的总额度")
    private BigDecimal totalTemporaryAmount = BigDecimal.ZERO;//额度上浮之前的总额度
    @LogField("已使用授信额度")
    private BigDecimal usedCreditAmount = BigDecimal.ZERO;//已使用授信额度
    @LogField("账期")
    private Long creditDays;            //账期
    @LogField("审批中的授信额度")
    private BigDecimal approveCreditAmount = BigDecimal.ZERO;//审批中的授信额度

    @LogField("企业类别,dict,companyCategory,bas")
    private String companyCategory;//企业类别，A、大型国企，上市公司，工厂及其子公司；B、A类中有诉讼或者其他行政处罚的；C、贸易商法人股东；D、贸易商个人股东
    @LogField("是否准入")
    private String allowed;//是否准入，Y、准入，N、禁止，NEW、新增
    @LogField("代采现货额度")
    private BigDecimal totalSpotAmount = BigDecimal.ZERO;//代采现货额度
    @LogField("已用代采现货额度")
    private BigDecimal usedSpotAmount = BigDecimal.ZERO;//已用代采现货额度
    @LogField("代采期货额度")
    private BigDecimal totalFuturesAmount = BigDecimal.ZERO;//代采期货额度
    @LogField("已用代采期货额度")
    private BigDecimal usedFuturesAmount = BigDecimal.ZERO;//已用代采期货额度

    @LogField("线上化标识")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean onLineFlg = false;//线上化标识
    @LogField("saas是否开通公司")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean openAccountFlg = false;//saas是否开通公司
    @LogField("saas是否开通管理员")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean openAdminFlg = false;//saas是否开通管理员
    @LogField("安心签是否开通")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean openCfcaFlg = false;//安心签是否开通
    @LogField("访厂报告是否通过,dict,accessReportFlg,admin")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean accessReportFlg;//访厂报告是否通过
    @LogField("是否申请线上化")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean onlineApplyFlg = false;//访厂报告是否通过

    private String industry;    //行业分类
    @LogField("上传法人身份证正面")
    private String cardFrontId;    //上传法人身份证正面
    @LogField("上传法人身份证反面")
    private String cardReverseId;    //上传法人身份证反面
    @LogField("上传企业征信授权书")
    private String corporateCreditId;    //上传企业征信授权书
    @LogField("上传个人征信授权书")
    private String personalCreditId;    //上传个人征信授权书
    @LogField("上传商标注册证")
    private String trademarkId;    //上传商标注册证
    @LogField("上传专利说明书")
    private String patentId;    //上传专利说明书
    @LogField("上传个人担保函")
    private String personalGuaranteeId;    //上传个人担保函
    @LogField("上传资产担保函")
    private String assetGuaranteeId;    //上传资产担保函
    @LogField("上传资产负债表")
    private String assetsId;    //上传资产负债表
    @LogField("上传现金流量表")
    private String cashFlowId;    //上传现金流量表
    @LogField("上传利润表")
    private String profitId;    //上传利润表
    @LogField("上传审计报告表")
    private String auditReportId;    //上传审计报告表
    @LogField("上传土地证明")
    private String landId;    //上传土地证明
    @LogField("土地类型")
    private String landType;//土地类型
    @LogField("上传厂房证明")
    private String plantId;    //上传厂房证明
    @LogField("厂房类型")
    private String plantType;//厂房类型
    @LogField("上传机械设备证明")
    private String equipmentId;    //上传机械设备证明
    @LogField("机械设备类型")
    private String equipmentType;//机械设备类型
    @LogField("销售合同服务费的费率")
    private BigDecimal rate = BigDecimal.ZERO;            //销售合同服务费的费率
    @LogField("逾期罚金的费率")
    private BigDecimal interestRate = BigDecimal.ZERO;    //逾期罚金的费率
    @LogField("服务合同类型")
    private String rateType;                            // 服务合同类型（服务费先收B，或服务费后收A）
    @LogField("赊销类型,dict,creditCycleType,admin")
    private String creditCycleType;                        // 固定赊销S，或浮动赊销D
    @LogField("赊销天数")
    private Integer creditCycle;                            //赊销天数，固定赊销类型的用户记录赊销周期
    @LogField("上传访厂报告")
    private String accessReportId;//上传访厂报告

    // 法人身份证
    @LogField("法人身份证")
    private String identityCardNumber;

    // 营业执照副本加盖公章附件ID
    @LogField("营业执照副本加盖公章附件ID")
    private String businessLicenseWithSealUrl;

    // 法人证件类型 0：身份证 1：港澳通行证 2：护照 3：台胞证
    @LogField("法人证件类型")
    private String cardType;

    // 联系人邮箱
    private String contactEmail;

    /**
     * 公司资料确认状态
     */
    private String companyConfirm;

    /**
     *
     */
    private String cfcaConfirm;

    /**
     * 企业征信确认状态
     */
    private String financialConfirm;

    /**
     * 仓库地址信息确认
     */
    private String warehouseConfirm;

    /**
     * 发票信息确认状态
     */
    private String billsConfirm;

    /**
     * 组织机构代码
     */
    private String orgNo;

    /**
     * 注册号
     */
    private String regNo;

    /**
     * 经营状态
     */
    private String operationStatus;

    /**
     * 成立日期
     */
    private String startDate;

    /**
     * 公司类型
     */
    private String econKind;

    /**
     * 经营期限起始日期
     */
    private String termStart;

    /**
     * 经营期限结束日期
     */
    private String termEnd;

    /**
     * 核准日期
     */
    private String checkDate;

    /**
     * 登记机关
     */
    private String belongOrg;

    /**
     * 省份代码
     */
    private String provinceCode;

    /**
     * 经营范围
     */
    private String scope;

    /**
     * 公司logo
     */
    private String logoUrl;

    /**
     * 变更时间
     */
    private String lastUpdateTime;

    /**
     * 来源 瑞可
     */
    private String sourceReg;

    /**
     * 所属行业 瑞可
     */
    private String industryReg;

    /**
     * 历史名词
     */
    private String historyNames;

    /**
     * 营业执照号
     */
    private String licenseNumber;

    /**
     * 主要人员
     */
    private String employeeListStr;

    /**
     * 联系人身份
     */
    private String customMyRole;

    /**
     * 白条额度
     */
    @LogField("白条额度")
    private String customQuota;

    /**
     * 申请还款周期
     */
    @LogField("申请还款周期")
    private String customRepaymentPeriod;

    /**
     * 白条准入审批状态
     */
    @LogField("白条准入审批状态")
    private String creditRatingStatus;

    @LogField("塑料分类申请状态")
    private String plasticStatus;

    private String plasticRemark;

    /**
     * 白条额度审批状态
     */
    @LogField("白条额度审批状态")
    private String creditQuotaStatus;
    /**
     * 回款周期审批状态
     */
    @LogField("回款周期审批状态")
    private String creditCycleStatus;
    /**
     * 服务费率审批状态
     */
    @LogField("服务费率审批状态")
    private String interestRateStatus;

    /**
     * 基础赊销额度
     */
    @LogField("基础赊销额度")
    private BigDecimal baseQuota;

    /**
     * 额度浮动审批状态
     */
    @LogField("额度浮动审批状态")
    private String floatingRateStatus;

    /**
     * 保险申报状态
     */
    @LogField("保险申报状态")
    private String applyInsuranceStatus;

    /**
     * 风控审批额度
     */
    @LogField("风控审批额度")
    private BigDecimal applyCreditAmount;

    /**
     * 物流章类型
     */
    private String logisticsSealType;

    /**
     * 人保申请状态
     */
    private String piccApplyStatus;

    /**
     * 是否需要人保限额申请
     */
    private String piccApplyCreditAmountFlg;

    /**
     * 中银申请备注
     */
    private String zhongYinRemark;

    /**
     * 是否需要申请中银
     */
    private String zhongYinApplyFlg;

    /**
     * 人保申请状态
     */
    private String zhongYinApplyStatus;
    
    /**
     * 中银批复日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date zhongYinApproveDate;

    /**
     * 是否参与采购提成
     */
    private Boolean buyCommissionFlag = true;

    /**
     * 是否标记供应商
     */
    private Boolean markSupplierFlag = false;

    /**
     * 供应商资源负责人
     */
    private Long supplierManagerUserId;

    public String getZhongYinRemark() {
        return zhongYinRemark;
    }

    public void setZhongYinRemark(String zhongYinRemark) {
        this.zhongYinRemark = zhongYinRemark;
    }

    public String getZhongYinApplyFlg() {
        return zhongYinApplyFlg;
    }

    public void setZhongYinApplyFlg(String zhongYinApplyFlg) {
        this.zhongYinApplyFlg = zhongYinApplyFlg;
    }

    public String getZhongYinApplyStatus() {
        return zhongYinApplyStatus;
    }

    public void setZhongYinApplyStatus(String zhongYinApplyStatus) {
        this.zhongYinApplyStatus = zhongYinApplyStatus;
    }

    public Date getZhongYinApproveDate() {
        return zhongYinApproveDate;
    }

    public void setZhongYinApproveDate(Date zhongYinApproveDate) {
        this.zhongYinApproveDate = zhongYinApproveDate;
    }

    public String getPiccApplyStatus() {
        return piccApplyStatus;
    }

    public void setPiccApplyStatus(String piccApplyStatus) {
        this.piccApplyStatus = piccApplyStatus;
    }

    public String getPiccApplyCreditAmountFlg() {
        return piccApplyCreditAmountFlg;
    }

    public void setPiccApplyCreditAmountFlg(String piccApplyCreditAmountFlg) {
        this.piccApplyCreditAmountFlg = piccApplyCreditAmountFlg;
    }

    public String getLogisticsSealType() {
        return logisticsSealType;
    }

    public void setLogisticsSealType(String logisticsSealType) {
        this.logisticsSealType = logisticsSealType;
    }

    public BigDecimal getApplyCreditAmount() {
        return applyCreditAmount;
    }

    public void setApplyCreditAmount(BigDecimal applyCreditAmount) {
        this.applyCreditAmount = applyCreditAmount;
    }

    public String getApplyVipStatus() {
        return applyVipStatus;
    }

    public void setApplyVipStatus(String applyVipStatus) {
        this.applyVipStatus = applyVipStatus;
    }

    /**
     * Vip申报状态
     */
    @LogField("Vip申报状态")
    private String applyVipStatus;

    /**
     * 大地保险额度
     */
    @LogField("大地保险额度")
    private BigDecimal daDiCreditAmount;
    
	/**
	 * 人保批复额度
	 */
    @LogField("人保批复额度")
	private BigDecimal piccCreditAmount;

	/**
	 * 人保批复账期
	 */
    @LogField("人保批复账期")
	private Integer piccApprovalPeriod;

	/**
	 * 人保已用金额 实际使用额度
	 */
    @LogField("人保已用金额")
	private BigDecimal piccHaveusedAmount = BigDecimal.ZERO;

	/**
	 * 人保授信超出金额
	 */
    @LogField("人保授信超出金额")
	private BigDecimal piccRemainingAmount = BigDecimal.ZERO;

	/**
	 * 人保赊销剩余额度
	 */
    @LogField("人保赊销剩余额度")
	private BigDecimal piccUseAbleaMount = BigDecimal.ZERO;
    /**
     * 人保批复日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date piccApproveDate;
    /**
     * 人保限额生效日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date piccLimitEffectDate;
    /**
     * 人保限额失效日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date piccLimitLapseDate;

    /**
     * 赔偿比例（%）
     */
    private BigDecimal piccCompensationRatio;

    /**
     * 人保piccCode
     */
    private String piccCode;

    /**
     * 供应商字段。包括供应商背景调查字段，准入申请字段、 额度申请字段、配送审批字段、期货审批字段
     *  2021-5-26
     */
    private String shareholder; // 股东背景。选择：国企、上市公司、普通私企
    private String relatedCompany; // 关联公司
    private String businessAddress; // 实际办公地址
    private Integer employeesNumber; // 员工人数
    @LogField("供应商业务类型")
    private String supplierType; // 供应商业务类型
    private String agentType; // 代理类型
    private String mainProduct; // 主营品种
    private String lastYearTaxableSales; // 去年纳税销售额
    private String netMargin; // 销售净利润率
    private String grossMargin; // 销售毛利率
    private String unallocatedStock; // 常备库存
    private String officeImageId; // 办公场地照片
    private String billImageId; // 与下游的开票照片
    @LogField("供应商准入白名单,dict,creditRating,admin")
    private String supplierRating = "G"; //供应商准入白名单。W-白名单 G-灰名单 B-黑名单
    @LogField("供应商级别,dict,supplierLevel,admin")
    private String supplierLevel; // 供应商级别。{'A级-龙头供应商': 'A', 'B级-扶持供应商': 'B', 'C级-小规模供应商': 'C'},
    @LogField("采购额度")
    private BigDecimal supplierPurchaseAmount = BigDecimal.ZERO; // 采购额度（元）
    @LogField("已使用的采购额度")
    private BigDecimal usedSupplierPurchaseAmount = BigDecimal.ZERO; // 已使用的采购额度（元）
    @LogField("预付款额度")
    private BigDecimal supplierPrepayAmount = BigDecimal.ZERO; // 预付款额度（元）
    @LogField("已使用的预付款额度")
    private BigDecimal usedSupplierPrepayAmount = BigDecimal.ZERO; // 已使用的预付款额度（元）
    @LogField("是否可以供应商配送")
    private String supplierDelivery = "0"; // 是否可以供应商配送
    @LogField("是否可以做期货合同")
    private String supplierFuture = "0"; // 是否可以做期货合同

    /**
     * 新增的审批状态字段 2021-5-26
     */
    private String promoteVipApplyStatus; // VIP提额申请状态
    private String creditDeliveryStatus; // 终端工厂自提审批状态
    private String supplierRatingStatus; // 供应商准入审批状态
    private String supplierQuotaStatus; // 供应商额度审批状态
    private String supplierDeliveryStatus; // 供应商配送状态
    private String supplierFutureStatus; // 供应商期货状态
    /**
     * 终端工厂自提字段 2021-5-26
     */
    private String creditDelivery = "0"; // 是否可以终端工厂自提
    /**
     * 终端工厂自提备注
     */
    private String creditDeliveryRemark;

    /**
     * VIP相关字段。通过定时任务来判断该企业是否仍然是VIP
     *  2021-5-26
     */
//    @JsonSerialize(using = ToStringSerializer.class)
//    private Boolean isVip = false;//是否申请了VIP
    private Integer vipLevel; // vip等级
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private  Date vipStartDate; // VIP开始日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private  Date vipEndDate; // vip到期日期
    private  Integer daysRemaining; // vip剩余天数

    /**
     * 客户分类 等级为 A类客户 B类客户 C类客户 D类客户
     */
    @LogField("客户分类,dict,companyGrade,bas")
    private String companyGrade;

    /**
     * 信用评分
     */
    @LogField("信用评分")
    private BigDecimal creditScore;

    /**
     * 附加分
     */
    private BigDecimal additionalScore;

    /**
     * 供应商分类（A类：60分以上；B类：50-60分；C类：40-50分；D类：40分以下）
     */
    @LogField("供应商分类,dict,supplierGrade,bas")
    private String supplierGrade;

    /**
     * 供应商评分
     */
    @LogField("供应商评分")
    private BigDecimal supplierScore;



    /**
     * 资料审核状态
     */
    @LogField("资料审核状态")
    private String companyApplyStatus;

    /**
     * cfca开户状态
     */
    @LogField("cfca开户状态")
    private String cfcaApprovedStatus;

    /**
     * 委托授权状态
     */
    @LogField("委托授权状态")
    private String entrustApplyStatus;

    /**
     * 可用余额
     * @return
     */
    private BigDecimal availableBalance;

    /**
     * 实控人担保 0-未签署、1-已签署
     */
    @LogField("实控人担保,dict,actualGuaranteeType,bas")
    private Boolean actualGuaranteeFlg;

    /**
     * 是否人保
     */
    @LogField("是否人保")
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean piccFlg = false;

    public Boolean getPiccFlg() {
        return piccFlg;
    }

    public void setPiccFlg(Boolean piccFlg) {
        this.piccFlg = piccFlg;
    }

    /**
     * 是否本次导入
     */
    private Boolean piccThisUpdateFlg;

    /**
     * 第一步准入审批备注
     */
    private String approvalRemark;

    /**
     *申报额度审批备注
     */
    private String applyRemark;

    /**
     * 申报上浮额度审批备注
     */
    private String gearRemark;

    /**
     * 供应商准入申请备注
     */
    private String supplierRemark;

    /**
     * 供应商额度申请备注
     */
    private String supplierSecondRemark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 释放给区域总次数
     */
    private Integer freedToDeptLeaderCount;

    /**
     * 访厂报告上传日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date accessReportUploadDate;

    private List<BsCompanyCredit> companyCreditList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "companyId")
    public List<BsCompanyCredit> getCompanyCreditList() {
        return companyCreditList;
    }

    public void setCompanyCreditList(List<BsCompanyCredit> companyCreditList) {
        this.companyCreditList = companyCreditList;
    }

    public Long getAccessReportApproveId() {
        return accessReportApproveId;
    }

    public void setAccessReportApproveId(Long accessReportApproveId) {
        this.accessReportApproveId = accessReportApproveId;
    }

    /**
     * 访厂报告申请记录
     */
    private Long accessReportApproveId;

    /**
     * 塑料分类
     */
    @LogField("塑料分类")
    private String plasticType;

    /**
     * 临时塑料分类
     */
    private String temporaryPlasticType;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getApplyRemark() {
        return applyRemark;
    }

    public void setApplyRemark(String applyRemark) {
        this.applyRemark = applyRemark;
    }

    public String getGearRemark() {
        return gearRemark;
    }

    public void setGearRemark(String gearRemark) {
        this.gearRemark = gearRemark;
    }

    public String getSupplierRemark() {
        return supplierRemark;
    }

    public void setSupplierRemark(String supplierRemark) {
        this.supplierRemark = supplierRemark;
    }

    public String getSupplierSecondRemark() {
        return supplierSecondRemark;
    }

    public void setSupplierSecondRemark(String supplierSecondRemark) {
        this.supplierSecondRemark = supplierSecondRemark;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }

    public Boolean getActualGuaranteeFlg() {
        return actualGuaranteeFlg;
    }

    public void setActualGuaranteeFlg(Boolean actualGuaranteeFlg) {
        this.actualGuaranteeFlg = actualGuaranteeFlg;
    }

    /* 是否申请线上化 */
    public Boolean getOnlineApplyFlg() {
        return onlineApplyFlg;
    }

    public void setOnlineApplyFlg(Boolean onlineApplyFlg) {
        this.onlineApplyFlg = onlineApplyFlg;
    }

    /* 访厂报告是否通过 */
    public Boolean getAccessReportFlg() {
        return accessReportFlg;
    }

    public void setAccessReportFlg(Boolean accessReportFlg) {
        this.accessReportFlg = accessReportFlg;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getCompanyApplyStatus() {
        return companyApplyStatus;
    }

    public void setCompanyApplyStatus(String companyApplyStatus) {
        this.companyApplyStatus = companyApplyStatus;
    }

    public String getCfcaApprovedStatus() {
        return cfcaApprovedStatus;
    }

    public void setCfcaApprovedStatus(String cfcaApprovedStatus) {
        this.cfcaApprovedStatus = cfcaApprovedStatus;
    }

    public String getEntrustApplyStatus() {
        return entrustApplyStatus;
    }

    public void setEntrustApplyStatus(String entrustApplyStatus) {
        this.entrustApplyStatus = entrustApplyStatus;
    }

    public String getCompanyGrade() {
        return companyGrade;
    }

    public void setCompanyGrade(String companyGrade) {
        this.companyGrade = companyGrade;
    }

    public BigDecimal getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(BigDecimal creditScore) {
        this.creditScore = creditScore;
    }

    public BigDecimal getAdditionalScore() {
        return additionalScore;
    }

    public void setAdditionalScore(BigDecimal additionalScore) {
        this.additionalScore = additionalScore;
    }

    public String getSupplierGrade() {
        return supplierGrade;
    }

    public void setSupplierGrade(String supplierGrade) {
        this.supplierGrade = supplierGrade;
    }

    public BigDecimal getSupplierScore() {
        return supplierScore;
    }

    public void setSupplierScore(BigDecimal supplierScore) {
        this.supplierScore = supplierScore;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getVipStartDate() {
        return vipStartDate;
    }

    public void setVipStartDate(Date vipStartDate) {
        this.vipStartDate = vipStartDate;
    }

    public Date getVipEndDate() {
        return vipEndDate;
    }

    public void setVipEndDate(Date vipEndDate) {
        this.vipEndDate = vipEndDate;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    /**
     *企业来源
     */
    private String companySource;

    /**
     * 额度类型(1-塑融宝；2-浙塑白条;)
     */
    private String creditType;

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getCompanySource() {
        return companySource;
    }

    public void setCompanySource(String companySource) {
        this.companySource = companySource;
    }

    /**
     * 开票抬头
     */
    private BigDecimal companyTitle;

    /**
     * 供应商配送备注
     */
    private String supplierDeliveryRemark;
    /**
     * 授信类别
     */
    private String creditCategory;

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
    }

    public BigDecimal getCompanyTitle() {
        return companyTitle;
    }

    public void setCompanyTitle(BigDecimal companyTitle) {
        this.companyTitle = companyTitle;
    }

    public String getContactEmail() {
		return contactEmail;
	}

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getBusinessLicenseWithSealUrl() {
        return businessLicenseWithSealUrl;
    }

    public void setBusinessLicenseWithSealUrl(String businessLicenseWithSealUrl) {
        this.businessLicenseWithSealUrl = businessLicenseWithSealUrl;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMatchUserId() {
        return matchUserId;
    }

    public void setMatchUserId(Long matchUserId) {
        this.matchUserId = matchUserId;
    }

    public Long getMatchUserDeptId() {
        return matchUserDeptId;
    }

    public void setMatchUserDeptId(Long matchUserDeptId) {
        this.matchUserDeptId = matchUserDeptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Date getMatchFllowDate() {
        return matchFllowDate;
    }

    public void setMatchFllowDate(Date matchFllowDate) {
        this.matchFllowDate = matchFllowDate;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getOwnerOfAccountId() {
        return ownerOfAccountId;
    }

    public void setOwnerOfAccountId(Long ownerOfAccountId) {
        this.ownerOfAccountId = ownerOfAccountId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    public List<BsCompanyShare> getCompanyShares() {
        return companyShares;
    }

    public void setCompanyShares(List<BsCompanyShare> companyShares) {
        this.companyShares = companyShares;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyArea() {
        return companyArea;
    }

    public void setCompanyArea(String companyArea) {
        this.companyArea = companyArea;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getLegalRepresent() {
        return legalRepresent;
    }

    public void setLegalRepresent(String legalRepresent) {
        this.legalRepresent = legalRepresent;
    }

    public String getRegisterCapital() {
        return registerCapital;
    }

    public void setRegisterCapital(String registerCapital) {
        this.registerCapital = registerCapital;
    }

    public Long getStafferNumber() {
        return stafferNumber;
    }

    public void setStafferNumber(Long stafferNumber) {
        this.stafferNumber = stafferNumber;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getTotalCreditAmount() {
        return Objects.isNull(totalCreditAmount) ? BigDecimal.ZERO : totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getTotalTemporaryAmount() {
        return totalTemporaryAmount;
    }

    public void setTotalTemporaryAmount(BigDecimal totalTemporaryAmount) {
        this.totalTemporaryAmount = totalTemporaryAmount;
    }

    public BigDecimal getUsedCreditAmount() {
        return usedCreditAmount;
    }

    public void setUsedCreditAmount(BigDecimal usedCreditAmount) {
        this.usedCreditAmount = usedCreditAmount;
    }

    public BigDecimal getApproveCreditAmount() {
        return Objects.isNull(approveCreditAmount) ? BigDecimal.ZERO : approveCreditAmount;
    }

    public void setApproveCreditAmount(BigDecimal approveCreditAmount) {
        this.approveCreditAmount = approveCreditAmount;
    }

    public Long getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Long creditDays) {
        this.creditDays = creditDays;
    }

    public Boolean getOnLineFlg() {
        return onLineFlg;
    }

    public void setOnLineFlg(Boolean onLineFlg) {
        this.onLineFlg = onLineFlg;
    }

    public Boolean getOpenAccountFlg() {
        return openAccountFlg;
    }

    public void setOpenAccountFlg(Boolean openAccountFlg) {
        this.openAccountFlg = openAccountFlg;
    }

    public Boolean getOpenAdminFlg() {
        return openAdminFlg;
    }

    public void setOpenAdminFlg(Boolean openAdminFlg) {
        this.openAdminFlg = openAdminFlg;
    }

    public Boolean getOpenCfcaFlg() {
        return openCfcaFlg;
    }

    public void setOpenCfcaFlg(Boolean openCfcaFlg) {
        this.openCfcaFlg = openCfcaFlg;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getCompanyCreditNo() {
        return companyCreditNo;
    }

    public void setCompanyCreditNo(String companyCreditNo) {
        this.companyCreditNo = companyCreditNo;
    }

    public Boolean getRiskFlg() {
        return riskFlg;
    }

    public void setRiskFlg(Boolean riskFlg) {
        this.riskFlg = riskFlg;
    }

    public String getAllowed() {
        return allowed;
    }

    public void setAllowed(String allowed) {
        this.allowed = allowed;
    }

    public BigDecimal getTotalSpotAmount() {
        return totalSpotAmount;
    }

    public void setTotalSpotAmount(BigDecimal totalSpotAmount) {
        this.totalSpotAmount = totalSpotAmount;
    }

    public BigDecimal getUsedSpotAmount() {
        return usedSpotAmount;
    }

    public void setUsedSpotAmount(BigDecimal usedSpotAmount) {
        this.usedSpotAmount = usedSpotAmount;
    }

    public BigDecimal getTotalFuturesAmount() {
        return totalFuturesAmount;
    }

    public void setTotalFuturesAmount(BigDecimal totalFuturesAmount) {
        this.totalFuturesAmount = totalFuturesAmount;
    }

    public BigDecimal getUsedFuturesAmount() {
        return usedFuturesAmount;
    }

    public void setUsedFuturesAmount(BigDecimal usedFuturesAmount) {
        this.usedFuturesAmount = usedFuturesAmount;
    }

    public String getCompanyCategory() {
        return companyCategory;
    }

    public void setCompanyCategory(String companyCategory) {
        this.companyCategory = companyCategory;
    }

    public String getCardFrontId() {
        return cardFrontId;
    }

    public void setCardFrontId(String cardFrontId) {
        this.cardFrontId = cardFrontId;
    }

    public String getCardReverseId() {
        return cardReverseId;
    }

    public void setCardReverseId(String cardReverseId) {
        this.cardReverseId = cardReverseId;
    }

    public String getCorporateCreditId() {
        return corporateCreditId;
    }

    public void setCorporateCreditId(String corporateCreditId) {
        this.corporateCreditId = corporateCreditId;
    }

    public String getPersonalCreditId() {
        return personalCreditId;
    }

    public void setPersonalCreditId(String personalCreditId) {
        this.personalCreditId = personalCreditId;
    }

    public String getTrademarkId() {
        return trademarkId;
    }

    public void setTrademarkId(String trademarkId) {
        this.trademarkId = trademarkId;
    }

    public String getPatentId() {
        return patentId;
    }

    public void setPatentId(String patentId) {
        this.patentId = patentId;
    }

    public String getPersonalGuaranteeId() {
        return personalGuaranteeId;
    }

    public void setPersonalGuaranteeId(String personalGuaranteeId) {
        this.personalGuaranteeId = personalGuaranteeId;
    }

    public String getAssetGuaranteeId() {
        return assetGuaranteeId;
    }

    public void setAssetGuaranteeId(String assetGuaranteeId) {
        this.assetGuaranteeId = assetGuaranteeId;
    }

    public String getAssetsId() {
        return assetsId;
    }

    public void setAssetsId(String assetsId) {
        this.assetsId = assetsId;
    }

    public String getCashFlowId() {
        return cashFlowId;
    }

    public void setCashFlowId(String cashFlowId) {
        this.cashFlowId = cashFlowId;
    }

    public String getProfitId() {
        return profitId;
    }

    public void setProfitId(String profitId) {
        this.profitId = profitId;
    }

    public String getAuditReportId() {
        return auditReportId;
    }

    public void setAuditReportId(String auditReportId) {
        this.auditReportId = auditReportId;
    }

    public String getLandId() {
        return landId;
    }

    public void setLandId(String landId) {
        this.landId = landId;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getCreditCycleType() {
        return creditCycleType;
    }

    public void setCreditCycleType(String creditCycleType) {
        this.creditCycleType = creditCycleType;
    }

    public Integer getCreditCycle() {
        return creditCycle;
    }

    public void setCreditCycle(Integer creditCycle) {
        this.creditCycle = creditCycle;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getCompanyConfirm() {
        return companyConfirm;
    }

    public void setCompanyConfirm(String companyConfirm) {
        this.companyConfirm = companyConfirm;
    }

    public String getCfcaConfirm() {
        return cfcaConfirm;
    }

    public void setCfcaConfirm(String cfcaConfirm) {
        this.cfcaConfirm = cfcaConfirm;
    }

    public String getFinancialConfirm() {
        return financialConfirm;
    }

    public void setFinancialConfirm(String financialConfirm) {
        this.financialConfirm = financialConfirm;
    }

    public String getWarehouseConfirm() {
        return warehouseConfirm;
    }

    public void setWarehouseConfirm(String warehouseConfirm) {
        this.warehouseConfirm = warehouseConfirm;
    }

    public String getBillsConfirm() {
        return billsConfirm;
    }

    public void setBillsConfirm(String billsConfirm) {
        this.billsConfirm = billsConfirm;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEconKind() {
        return econKind;
    }

    public void setEconKind(String econKind) {
        this.econKind = econKind;
    }

    public String getTermStart() {
        return termStart;
    }

    public void setTermStart(String termStart) {
        this.termStart = termStart;
    }

    public String getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(String termEnd) {
        this.termEnd = termEnd;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getBelongOrg() {
        return belongOrg;
    }

    public void setBelongOrg(String belongOrg) {
        this.belongOrg = belongOrg;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getSourceReg() {
        return sourceReg;
    }

    public void setSourceReg(String sourceReg) {
        this.sourceReg = sourceReg;
    }

    public String getIndustryReg() {
        return industryReg;
    }

    public void setIndustryReg(String industryReg) {
        this.industryReg = industryReg;
    }

    public String getHistoryNames() {
        return historyNames;
    }

    public void setHistoryNames(String historyNames) {
        this.historyNames = historyNames;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getEmployeeListStr() {
        return employeeListStr;
    }

    public void setEmployeeListStr(String employeeListStr) {
        this.employeeListStr = employeeListStr;
    }

    public String getCustomMyRole() {
        return customMyRole;
    }

    public void setCustomMyRole(String customMyRole) {
        this.customMyRole = customMyRole;
    }

    public String getCustomQuota() {
        return customQuota;
    }

    public void setCustomQuota(String customQuota) {
        this.customQuota = customQuota;
    }

    public String getCustomRepaymentPeriod() {
        return customRepaymentPeriod;
    }

    public void setCustomRepaymentPeriod(String customRepaymentPeriod) {
        this.customRepaymentPeriod = customRepaymentPeriod;
    }

    public String getCreditRatingStatus() {
        return creditRatingStatus;
    }

    public void setCreditRatingStatus(String creditRatingStatus) {
        this.creditRatingStatus = creditRatingStatus;
    }

    public String getCreditQuotaStatus() {
        return creditQuotaStatus;
    }

    public void setCreditQuotaStatus(String creditQuotaStatus) {
        this.creditQuotaStatus = creditQuotaStatus;
    }

    public String getCreditCycleStatus() {
        return creditCycleStatus;
    }

    public void setCreditCycleStatus(String creditCycleStatus) {
        this.creditCycleStatus = creditCycleStatus;
    }

    public String getInterestRateStatus() {
        return interestRateStatus;
    }

    public void setInterestRateStatus(String interestRateStatus) {
        this.interestRateStatus = interestRateStatus;
    }

    public BigDecimal getBaseQuota() {
        return baseQuota;
    }

    public void setBaseQuota(BigDecimal baseQuota) {
        this.baseQuota = baseQuota;
    }

    public String getFloatingRateStatus() {
        return floatingRateStatus;
    }

    public void setFloatingRateStatus(String floatingRateStatus) {
        this.floatingRateStatus = floatingRateStatus;
    }

    public String getApplyInsuranceStatus() {
        return applyInsuranceStatus;
    }

	public void setApplyInsuranceStatus(String applyInsuranceStatus) {
		this.applyInsuranceStatus = applyInsuranceStatus;
	}

    public BigDecimal getDaDiCreditAmount() {
        return Objects.isNull(daDiCreditAmount) ? BigDecimal.ZERO : daDiCreditAmount;
    }

    public void setDaDiCreditAmount(BigDecimal daDiCreditAmount) {
        this.daDiCreditAmount = daDiCreditAmount;
    }

    public BigDecimal getPiccCreditAmount() {
		return Objects.isNull(piccCreditAmount) ? BigDecimal.ZERO : piccCreditAmount;
	}

	public void setPiccCreditAmount(BigDecimal piccCreditAmount) {
		this.piccCreditAmount = piccCreditAmount;
	}

	public Integer getPiccApprovalPeriod() {
		return piccApprovalPeriod;
	}

	public void setPiccApprovalPeriod(Integer piccApprovalPeriod) {
		this.piccApprovalPeriod = piccApprovalPeriod;
	}

	public BigDecimal getPiccHaveusedAmount() {
		return piccHaveusedAmount;
	}

	public void setPiccHaveusedAmount(BigDecimal piccHaveusedAmount) {
		this.piccHaveusedAmount = piccHaveusedAmount;
	}

	public BigDecimal getPiccRemainingAmount() {
		return piccRemainingAmount;
	}

	public void setPiccRemainingAmount(BigDecimal piccRemainingAmount) {
		this.piccRemainingAmount = piccRemainingAmount;
	}

    public BigDecimal getPiccUseAbleaMount() {
        return piccUseAbleaMount;
    }

    public void setPiccUseAbleaMount(BigDecimal piccUseAbleaMount) {
        this.piccUseAbleaMount = piccUseAbleaMount;
    }

    public Date getPiccApproveDate() {
        return piccApproveDate;
    }

    public void setPiccApproveDate(Date piccApproveDate) {
        this.piccApproveDate = piccApproveDate;
    }

    public Date getPiccLimitEffectDate() {
        return piccLimitEffectDate;
    }

    public void setPiccLimitEffectDate(Date piccLimitEffectDate) {
        this.piccLimitEffectDate = piccLimitEffectDate;
    }

    public Date getPiccLimitLapseDate() {
        return piccLimitLapseDate;
    }

    public void setPiccLimitLapseDate(Date piccLimitLapseDate) {
        this.piccLimitLapseDate = piccLimitLapseDate;
    }

    public BigDecimal getPiccCompensationRatio() {
        return piccCompensationRatio;
    }

    public void setPiccCompensationRatio(BigDecimal piccCompensationRatio) {
        this.piccCompensationRatio = piccCompensationRatio;
    }

    public String getPiccCode() {
        return piccCode;
    }

    public void setPiccCode(String piccCode) {
        this.piccCode = piccCode;
    }

    public String getShareholder() {
        return shareholder;
    }

    public void setShareholder(String shareholder) {
        this.shareholder = shareholder;
    }

    public String getRelatedCompany() {
        return relatedCompany;
    }

    public void setRelatedCompany(String relatedCompany) {
        this.relatedCompany = relatedCompany;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public Integer getEmployeesNumber() {
        return employeesNumber;
    }

    public void setEmployeesNumber(Integer employeesNumber) {
        this.employeesNumber = employeesNumber;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getMainProduct() {
        return mainProduct;
    }

    public void setMainProduct(String mainProduct) {
        this.mainProduct = mainProduct;
    }

    public String getLastYearTaxableSales() {
        return lastYearTaxableSales;
    }

    public void setLastYearTaxableSales(String lastYearTaxableSales) {
        this.lastYearTaxableSales = lastYearTaxableSales;
    }

    public String getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(String netMargin) {
        this.netMargin = netMargin;
    }

    public String getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(String grossMargin) {
        this.grossMargin = grossMargin;
    }

    public String getUnallocatedStock() {
        return unallocatedStock;
    }

    public void setUnallocatedStock(String unallocatedStock) {
        this.unallocatedStock = unallocatedStock;
    }

    public String getOfficeImageId() {
        return officeImageId;
    }

    public void setOfficeImageId(String officeImageId) {
        this.officeImageId = officeImageId;
    }

    public String getBillImageId() {
        return billImageId;
    }

    public void setBillImageId(String billImageId) {
        this.billImageId = billImageId;
    }

    public String getSupplierRating() {
        return supplierRating;
    }

    public void setSupplierRating(String supplierRating) {
        this.supplierRating = supplierRating;
    }

    public String getSupplierLevel() {
        return supplierLevel;
    }

    public void setSupplierLevel(String supplierLevel) {
        this.supplierLevel = supplierLevel;
    }

    public BigDecimal getSupplierPurchaseAmount() {
        return Objects.isNull(supplierPurchaseAmount) ? BigDecimal.ZERO : supplierPurchaseAmount;
    }

    public void setSupplierPurchaseAmount(BigDecimal supplierPurchaseAmount) {
        this.supplierPurchaseAmount = supplierPurchaseAmount;
    }

    public BigDecimal getUsedSupplierPurchaseAmount() {
        return Objects.isNull(usedSupplierPurchaseAmount) ? BigDecimal.ZERO : usedSupplierPurchaseAmount;
    }

    public void setUsedSupplierPurchaseAmount(BigDecimal usedSupplierPurchaseAmount) {
        this.usedSupplierPurchaseAmount = usedSupplierPurchaseAmount;
    }

    public BigDecimal getSupplierPrepayAmount() {
        return supplierPrepayAmount;
    }

    public void setSupplierPrepayAmount(BigDecimal supplierPrepayAmount) {
        this.supplierPrepayAmount = supplierPrepayAmount;
    }

    public BigDecimal getUsedSupplierPrepayAmount() {
        return usedSupplierPrepayAmount;
    }

    public void setUsedSupplierPrepayAmount(BigDecimal usedSupplierPrepayAmount) {
        this.usedSupplierPrepayAmount = usedSupplierPrepayAmount;
    }

    public String getPromoteVipApplyStatus() {
        return promoteVipApplyStatus;
    }

    public void setPromoteVipApplyStatus(String promoteVipApplyStatus) {
        this.promoteVipApplyStatus = promoteVipApplyStatus;
    }

    public String getCreditDeliveryStatus() {
        return creditDeliveryStatus;
    }

    public void setCreditDeliveryStatus(String creditDeliveryStatus) {
        this.creditDeliveryStatus = creditDeliveryStatus;
    }

    public String getSupplierRatingStatus() {
        return supplierRatingStatus;
    }

    public void setSupplierRatingStatus(String supplierRatingStatus) {
        this.supplierRatingStatus = supplierRatingStatus;
    }

    public String getSupplierQuotaStatus() {
        return supplierQuotaStatus;
    }

    public void setSupplierQuotaStatus(String supplierQuotaStatus) {
        this.supplierQuotaStatus = supplierQuotaStatus;
    }

    public String getSupplierDeliveryStatus() {
        return supplierDeliveryStatus;
    }

    public void setSupplierDeliveryStatus(String supplierDeliveryStatus) {
        this.supplierDeliveryStatus = supplierDeliveryStatus;
    }

    public String getSupplierFutureStatus() {
        return supplierFutureStatus;
    }

    public void setSupplierFutureStatus(String supplierFutureStatus) {
        this.supplierFutureStatus = supplierFutureStatus;
    }

    public String getSupplierDelivery() {
        return supplierDelivery;
    }

    public void setSupplierDelivery(String supplierDelivery) {
        this.supplierDelivery = supplierDelivery;
    }

    public String getSupplierFuture() {
        return supplierFuture;
    }

    public void setSupplierFuture(String supplierFuture) {
        this.supplierFuture = supplierFuture;
    }

    public String getCreditDelivery() {
        return creditDelivery;
    }

    public void setCreditDelivery(String creditDelivery) {
        this.creditDelivery = creditDelivery;
    }

    public String getAccessReportId() {
        return accessReportId;
    }

    public void setAccessReportId(String accessReportId) {
        this.accessReportId = accessReportId;
    }

    public String getMatchUserName() {
        return matchUserName;
    }

    public void setMatchUserName(String matchUserName) {
        this.matchUserName = matchUserName;
    }

    public Integer getFreedToDeptLeaderCount() {
        return freedToDeptLeaderCount;
    }

    public void setFreedToDeptLeaderCount(Integer freedToDeptLeaderCount) {
        this.freedToDeptLeaderCount = freedToDeptLeaderCount;
    }

    public Boolean getPiccThisUpdateFlg() {
        return piccThisUpdateFlg;
    }

    public void setPiccThisUpdateFlg(Boolean piccThisUpdateFlg) {
        this.piccThisUpdateFlg = piccThisUpdateFlg;
    }

    public Date getAccessReportUploadDate() {
        return accessReportUploadDate;
    }

    public void setAccessReportUploadDate(Date accessReportUploadDate) {
        this.accessReportUploadDate = accessReportUploadDate;
    }

    public String getSupplierDeliveryRemark() {
        return supplierDeliveryRemark;
    }

    public void setSupplierDeliveryRemark(String supplierDeliveryRemark) {
        this.supplierDeliveryRemark = supplierDeliveryRemark;
    }

    public String getCreditDeliveryRemark() {
        return creditDeliveryRemark;
    }

    public void setCreditDeliveryRemark(String creditDeliveryRemark) {
        this.creditDeliveryRemark = creditDeliveryRemark;
    }

    public Boolean getBuyCommissionFlag() {
        return buyCommissionFlag;
    }

    public void setBuyCommissionFlag(Boolean buyCommissionFlag) {
        this.buyCommissionFlag = buyCommissionFlag;
    }

    public Boolean getMarkSupplierFlag() {
        return markSupplierFlag;
    }

    public void setMarkSupplierFlag(Boolean markSupplierFlag) {
        this.markSupplierFlag = markSupplierFlag;
    }

    public Long getSupplierManagerUserId() {
        return supplierManagerUserId;
    }

    public void setSupplierManagerUserId(Long supplierManagerUserId) {
        this.supplierManagerUserId = supplierManagerUserId;
    }

    public String getPlasticType(){
        return plasticType;
    }

    public String getPlasticStatus() {
        return plasticStatus;
    }

    public void setPlasticStatus(String plasticStatus) {
        this.plasticStatus = plasticStatus;
    }

    public void setPlasticType(String plasticType){
        this.plasticType = plasticType;
    }

    public String getTemporaryPlasticType() {
        return temporaryPlasticType;
    }

    public void setTemporaryPlasticType(String temporaryPlasticType) {
        this.temporaryPlasticType = temporaryPlasticType;
    }

    public String getPlasticRemark() {
        return plasticRemark;
    }

    public void setPlasticRemark(String plasticRemark) {
        this.plasticRemark = plasticRemark;
    }
}
