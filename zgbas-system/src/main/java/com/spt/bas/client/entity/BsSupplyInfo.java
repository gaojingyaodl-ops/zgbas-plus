package com.spt.bas.client.entity;

import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 *     企业补充信息表
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2020-10-28 11:56
 */
@Entity
@Table(name = "t_bs_supply_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsSupplyInfo extends IdEntity {
    private static final long serialVersionUID = -1194728704829592332L;
    /**
     * 企业id
     */
    private Long companyId;
    /**
     * 上传企业征信授权书
     */
    private String corporateCreditId;
    /**
     * 上传个人征信授权书
     */
    private String personalCreditId;
    /**
     * 上传商标注册证
     */
    private String trademarkId;
    /**
     * 上传专利说明书
     */
    private String patentId;
    /**
     * 上传个人担保函
     */
    private String personalGuaranteeId;
    /**
     * 上传资产担保函
     */
    private String assetGuaranteeId;
    /**
     * 上传资产负债表
     */
    private String assetsId;
    /**
     * 上传现金流量表
     */
    private String cashFlowId;
    /**
     * 上传利润表
     */
    private String profitId;
    /**
     * 上传审计报告表
     */
    private String auditReportId;
    /**
     * 上传土地证明
     */
    private String landId;
    /**
     * 土地类型
     */
    private String landType;
    /**
     * 上传厂房证明
     */
    private String plantId;
    /**
     * 厂房类型
     */
    private String plantType;
    /**
     * 上传机械设备证明
     */
    private String equipmentId;

    /**
     * 上传访厂报告
     */
    private String accessReportId;
    /**
     * 机械设备类型
     */
    private String equipmentType;
    /**
     * 微信用户id
     */
    private Long wxUserId;

    private Long createUserId;

    private Long updateUserId;

    /**
     * 行业政策
     */
    private String industryPolicyId;

    /**
     * 下游知名企业
     */
    private String wellKnownEnterpriseId;

    /**
     * 电费缴费数额
     */
    private String electricityPaymentType;

    /**
     * 近六个电费缴费记录（月均）
     */
    private String electricityPaymentId;

    /**
     * 工厂门面照片
     */
    private String factoryFacadeId;

    /**
     * 仓库照片
     */
    private String warehousePhotoId;

    /**
     * 生产线照片
     */
    private String productionLineId;

    /**
     * 其他企业征信信息
     */
    private String otherInfoId;


    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getIndustryPolicyId() {
        return industryPolicyId;
    }

    public void setIndustryPolicyId(String industryPolicyId) {
        this.industryPolicyId = industryPolicyId;
    }

    public String getWellKnownEnterpriseId() {
        return wellKnownEnterpriseId;
    }

    public void setWellKnownEnterpriseId(String wellKnownEnterpriseId) {
        this.wellKnownEnterpriseId = wellKnownEnterpriseId;
    }

    public String getElectricityPaymentType() {
        return electricityPaymentType;
    }

    public void setElectricityPaymentType(String electricityPaymentType) {
        this.electricityPaymentType = electricityPaymentType;
    }

    public String getElectricityPaymentId() {
        return electricityPaymentId;
    }

    public void setElectricityPaymentId(String electricityPaymentId) {
        this.electricityPaymentId = electricityPaymentId;
    }

    public String getFactoryFacadeId() {
        return factoryFacadeId;
    }

    public void setFactoryFacadeId(String factoryFacadeId) {
        this.factoryFacadeId = factoryFacadeId;
    }

    public String getWarehousePhotoId() {
        return warehousePhotoId;
    }

    public void setWarehousePhotoId(String warehousePhotoId) {
        this.warehousePhotoId = warehousePhotoId;
    }

    public String getProductionLineId() {
        return productionLineId;
    }

    public void setProductionLineId(String productionLineId) {
        this.productionLineId = productionLineId;
    }

    public String getOtherInfoId() {
        return otherInfoId;
    }

    public void setOtherInfoId(String otherInfoId) {
        this.otherInfoId = otherInfoId;
    }

    public String getAccessReportId() {
        return accessReportId;
    }

    public void setAccessReportId(String accessReportId) {
        this.accessReportId = accessReportId;
    }
}
