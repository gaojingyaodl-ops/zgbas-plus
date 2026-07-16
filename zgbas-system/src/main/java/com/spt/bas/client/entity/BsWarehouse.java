package com.spt.bas.client.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.spt.tools.jpa.vo.IdEntity;

@Entity
@Table(name = "t_bs_warehouse")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BsWarehouse extends IdEntity {

    private static final long serialVersionUID = 7774308411832307790L;

    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 所属城市
     */
    private String city;
    /**
     * 仓库状态
     */
    private String warehouseFlag;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 备注
     */
    private String remark;
    /**
     * 企业账套Id
     */
    private Long enterpriseId;
    /**
     * 是否有效
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean enableFlg;

    private List<BsWarehouseAddr> addrs;

    private String warehouseAddr;//地址

    /**
     * 省
     */
    private String province;

    /**
     * 区、县
     */
    private String area;

    private String areaCode; // 地区代码

    /**
     * 小程序用户id
     */
    private Long wxUserId;

    private Long companyId;

    private String provinceCode;

    private String cityCode;

    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean tpBusinessFlg;

    /**
     * 自营业务标识
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean zyBusinessFlg;

    /**
     * 免资金利息
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean freeInterestFundsFlg;

    /**
     * 仓储费计算公式
     */
    private String storageComputeFormula;


    /**
     * 仓库全路径
     */
    private String fullAddr;


    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    private Boolean defaultFlg;//是否默认

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Boolean getDefaultFlg() {
        return defaultFlg;
    }

    public void setDefaultFlg(Boolean defaultFlg) {
        this.defaultFlg = defaultFlg;
    }

    public String getWarehouseAddr() {
        return warehouseAddr;
    }

    public void setWarehouseAddr(String warehouseAddr) {
        this.warehouseAddr = warehouseAddr;
    }

    public String getWarehouseFlag() {
        return warehouseFlag;
    }

    public void setWarehouseFlag(String warehouseFlag) {
        this.warehouseFlag = warehouseFlag;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    /**
     * 仓储费单价
     */
    private BigDecimal warehouseUnitCost;

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Boolean getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(Boolean enableFlg) {
        this.enableFlg = enableFlg;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = BsWarehouseAddr.class)
    @JoinColumn(name = "warehouseId")
    public List<BsWarehouseAddr> getAddrs() {
        return addrs;
    }

    public void setAddrs(List<BsWarehouseAddr> addrs) {
        this.addrs = addrs;
    }

    public BigDecimal getWarehouseUnitCost() {
        return warehouseUnitCost;
    }

    public void setWarehouseUnitCost(BigDecimal warehouseUnitCost) {
        this.warehouseUnitCost = warehouseUnitCost;
    }

    public Long getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Long wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Boolean getTpBusinessFlg() {
        return tpBusinessFlg;
    }

    public void setTpBusinessFlg(Boolean tpBusinessFlg) {
        this.tpBusinessFlg = tpBusinessFlg;
    }

    public Boolean getZyBusinessFlg() {
        return zyBusinessFlg;
    }

    public void setZyBusinessFlg(Boolean zyBusinessFlg) {
        this.zyBusinessFlg = zyBusinessFlg;
    }

    public Boolean getFreeInterestFundsFlg() {
        return freeInterestFundsFlg;
    }

    public void setFreeInterestFundsFlg(Boolean freeInterestFundsFlg) {
        this.freeInterestFundsFlg = freeInterestFundsFlg;
    }

    public String getStorageComputeFormula() {
        return storageComputeFormula;
    }

    public void setStorageComputeFormula(String storageComputeFormula) {
        this.storageComputeFormula = storageComputeFormula;
    }

    public String getFullAddr() {
        return fullAddr;
    }

    public void setFullAddr(String fullAddr) {
        this.fullAddr = fullAddr;
    }
}
