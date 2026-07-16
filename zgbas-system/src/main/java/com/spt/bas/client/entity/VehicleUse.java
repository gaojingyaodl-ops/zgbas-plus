package com.spt.bas.client.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spt.pm.inter.IPmEntity;
import com.spt.tools.jpa.vo.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name ="t_vehicle_use")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VehicleUse extends IdEntity implements IPmEntity {
    private static final long serialVersionUID = 8833321958431034250L;

    /**
     * 申请人id
     */
    private Long applyUserId;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 申请时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date applyDate;



    /**
     * 部门
     */
    private String applyDeptName;

    /**
     * 驾驶员
     */
    private String driverName;

    /**
     * 用车事由
     */
    private String useReason;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 出发时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date departDate;

    /**
     * 车牌号 关联数据字典
     */
    private String plateNumber;

    /**
     * 实际里程数
     */
    private Integer actualMileage;

    /**
     * 乘车人数
     */
    private Integer ridingNumber;

    /**
     * 状态 'N-新增，A-审批中，B-驳回，D-完成'，C-作废
     */
    private String status;

    /**
     * 起始公里数
     */
    private Integer startRevenue;

    /**
     * 终止公里数
     */
    private Integer stopRevenue;
    /**
     * 所在地点
     */
    private String locations;

    /**
     * 企业账套ID
     */
    private Long enterpriseId;

    /**
     * 审批单ID
     */
    private Long approveId;

    /**
     * 附件ID
     */
    private String fileId;

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getApplyDeptName() {
        return applyDeptName;
    }

    public void setApplyDeptName(String applyDeptName) {
        this.applyDeptName = applyDeptName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUseReason() {
        return useReason;
    }

    public void setUseReason(String useReason) {
        this.useReason = useReason;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Integer getActualMileage() {
        return actualMileage;
    }

    public void setActualMileage(Integer actualMileage) {
        this.actualMileage = actualMileage;
    }

    public Integer getRidingNumber() {
        return ridingNumber;
    }

    public void setRidingNumber(Integer ridingNumber) {
        this.ridingNumber = ridingNumber;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStartRevenue() {
        return startRevenue;
    }

    public void setStartRevenue(Integer startRevenue) {
        this.startRevenue = startRevenue;
    }

    public Integer getStopRevenue() {
        return stopRevenue;
    }

    public void setStopRevenue(Integer stopRevenue) {
        this.stopRevenue = stopRevenue;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    @Override
    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getApproveId() {
        return approveId;
    }

    @Override
    public void setApproveId(Long approveId) {
        this.approveId = approveId;
    }

    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
