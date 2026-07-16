package com.spt.bas.client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author 田起立
 * @Date 2024/5/31 15:00
 * @Description:
 */
@Data
public class ApplyCompanyVisitVo {
    private Long id; // id
    private String approveId; // 申请id
    private String approveNo; // 审批编号
    private String status; // 状态（N-新增，A-审批中，D-完成）
    private Long companyId; // 企业ID
    private String companyName; // 企业名称
    private String businessTerm; // 企业期限
    private String companyCategory; // 企业性质，A、大型国企，上市公司，工厂及其子公司；B、A类中有诉讼或者其他行政处罚的；C、贸易商法人股东；D、贸易商个人股东
    private String companyCategoryName;
    private String registerCapital; // 注册资本
    private String paidCapital; // 实缴资本
    private String legalRepresentative; // 法定代表人
    private String nationality; // 国籍
    private String personIdNumber; // 身份证号
    private String telephone; // 电话
    private String businessScope; // 经营范围
    private String registerAddress; // 主持地址
    private String factoryAddress; // 工厂地址
    private String mainProduct; // 主要产品
    private String productNature; // 产品性质
    private String salesScale; // 销售规模
    private String clientPortrait; // 客户画像
    private String majorCustomer; // 主要客户
    private String customerQuality; // 客户质量
    private String ingredientName; // 原料名称
    private String quantityRequired; // 需求数量（吨/月）
    private String siteAddress; // 厂区地区
    private String plantArea; // 厂区面积
    private String plantEnvironment; // 厂区环境
    private String deviceNameQuantity; // 设备名称和数量
    private String degreeOfAutomation; // 自动化程度
    private String workSituation; // 开工情况
    private String productionLineNumber; // 产线人数
    private String workshopEnvironment; // 车间环境
    private String natureOfWarehouse; // 仓库性质
    private String finishedGoodsInventory; // 成品库存情况
    private String warehouseManagement; // 仓库管理
    private String warehouseEnvironment; // 仓库环境
    private String stockOfRawMaterials; // 原料库存情况（吨）
    private String landBuilding; // 土地厂房
    private String accountsReceivable; // 应收账款
    private String machineryEquipment; // 机器设备
    private String personalAssets; // 个人资产
    private String otherPropertyClues; // 其他财产线索
    private String associatedCompanyName; // 关联企业名称
    private String businessInquiry; // 工商查询
    private String litigationInquiry; // 诉讼查询
    private String mediaInquiry; // 媒体查询
    private String sceneInquiry; // 现场查询
    private String actualDesignedCapacityLogicMatch; // 实际与设计产能逻辑匹配
    private String electricityProductionLogicMatch; // 用电量与产量逻辑匹配
    private String taxSalesLoginMatch; // 纳税申报与销售金额的逻辑匹配
    private String fileId; // 现场照片file_id
    private String surveyResult; // 调查结果
    private Long riskControlSurveyUserId; // 风控调查人Id
    private String riskControlSurveyUserName; // 风控调查人名称
    private Long businessManagerUserId; // 业务经理ID
    private String businessManagerUserName; // 业务经理名称
    private Long businessDivisionId; // 事业部ID
    private String businessDivisionName; // 事业部名称
    private String contractPersonName; // 对接人姓名
    private String contractPersonPosition; // 对接人职务
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date surveyTime; // 调查时间
    private String surveySite; // 调查地点
    private String surveyObjective; // 调查目的
    private String peopleInsuranceApproval; // 人保批复额度
    private String companyCurrentQuota; // 公司现行额度
    private String surveryConclusion; // 调查结论
    private String recommendedAmount; // 推荐提额
    private Long createdUserId; // 创建人
    private String travelFileId; //出差申请单
    private Long createdDeptId; // 创建人部门
    // 财务报表
    private String financialStatementFileId;
    // 增值税纳税申报表
    private String vatTaxReturnFileId;
    // 企业下游销售合同
    private String companySalesContractFileId;
    // 企业征信，法人实控人个人征信
    private String companyLegalCreditFileId;
    // 土地证，房产证，租赁协议
    private String landPropertyLeasesFileId;
    // 设备融资租赁合同
    private String equipmentFinanceLeaseFileId;
    // 借款合同，担保合同
    private String loanGuaranteeContractFileId;
    // 票据信息
    private String billInformationFileId;
    // 企业银行流水
    private String companyBankStatementFileId;
    // 涉诉和被执行案件的结案证明，诉讼文书，调解书
    private String closeLitigationMediateFileId;
}
